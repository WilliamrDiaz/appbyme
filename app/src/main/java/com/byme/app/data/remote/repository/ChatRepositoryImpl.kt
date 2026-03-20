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
    override suspend fun getOrCreateChat(
        userId: String,
        professionalId: String,
        userName: String,
        professionalName: String
    ): Result<Chat> {
        return try {
            // ID determinístico
            val chatId = "${userId}_${professionalId}"
            val docRef = chatsCollection.document(chatId)
            val doc = docRef.get().await()

            if (doc.exists()) {
                // Ya existe el chat
                val chat = doc.toObject(Chat::class.java)!!.copy(id = doc.id)
                Result.success(chat)
            } else {
                // Crear nuevo chat
                val newChat = Chat(
                    id = chatId,
                    userId = userId,
                    professionalId = professionalId,
                    userName = userName,
                    professionalName = professionalName,
                    lastMessage = "",
                    lastMessageTime = System.currentTimeMillis(),
                    unreadCount = 0
                )
                docRef.set(newChat).await()
                Result.success(newChat)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
            val combined = mutableMapOf<String, Chat>()

            val listenerUser = chatsCollection
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Chat::class.java)?.copy(id = doc.id)
                    }?.forEach { chat ->
                        combined[chat.id] = chat
                    }
                    trySend(combined.values.sortedByDescending { it.lastMessageTime })
                }

            val listenerProfessional = chatsCollection
                .whereEqualTo("professionalId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Chat::class.java)?.copy(id = doc.id)
                    }?.forEach { chat ->
                        combined[chat.id] = chat
                    }
                    trySend(combined.values.sortedByDescending { it.lastMessageTime })
                }

            awaitClose {
                listenerUser.remove()
                listenerProfessional.remove()
            }
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