package com.byme.app.data.remote.repository

import com.byme.app.model.Professional
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfessionalRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("professional")

    suspend fun getAllProfessionals(): List<Professional> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.map { doc ->
                doc.toObject(Professional::class.java)!!.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchProfessionals(query: String): List<Professional> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.map { doc ->
                doc.toObject(Professional::class.java)!!.copy(id = doc.id)
            }.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}