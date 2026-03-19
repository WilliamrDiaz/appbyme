package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Category
import com.byme.app.domain.repository.CategoryRepositoryInterface
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategoryRepositoryImpl (
    private val firestore: FirebaseFirestore
) : CategoryRepositoryInterface {
    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val snapshot = firestore
                .collection("categories")
                .get()
                .await()

            val categories = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Category::class.java)?.copy(id = doc.id)
                } catch (e: Exception) { null }
            }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}