package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Chat
import com.byme.app.domain.model.Message
import com.byme.app.domain.repository.ChatRepositoryInterface
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl(
    private val firestore: FirebaseFirestore,
): ChatRepositoryInterface {

    private val chatsCollection = firestore.collection("chats")

    override suspend fun createChat(chat: Chat): Result<String> {
        return try {
            val docRef = chatsCollection.add(chat).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChats(userId: String): Flow<List<Chat>> {
        return callbackFlow {
            val listener = chatsCollection
                .whereEqualTo("userId", userId)
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val chats = snapshot?.documents?.map { doc ->
                        doc.toObject(Chat::class.java)!!.copy(id = doc.id)
                    } ?: emptyList()
                    trySend(chats)
                }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun sendMessage(
        chatId: String,
        message: Message
    ): Result<Unit> {
        return try {
            chatsCollection
                .document(chatId)
                .collection("messages")
                .add(message)
                .await()
            // Actualizar ultimo mensaje del chat
            chatsCollection.document(chatId).update(
                mapOf(
                    "lastMessage" to message.text,
                    "lastMessageTime" to message.timestamp
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMessages(chatId: String): Flow<List<Message>> {
        return callbackFlow {
            val listener = chatsCollection
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val messages = snapshot?.documents?.map { doc ->
                        doc.toObject(Message::class.java)!!.copy(id = doc.id)
                    } ?: emptyList()
                    trySend(messages)
                }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun markAsRead(
        chatId: String,
        userId: String
    ): Result<Unit> {
        return try {
            chatsCollection.document(chatId)
                .update("unreadCount", 0)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}