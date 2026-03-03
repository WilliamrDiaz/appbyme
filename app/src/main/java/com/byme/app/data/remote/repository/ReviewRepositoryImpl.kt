package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Review
import com.byme.app.domain.repository.ReviewRepositoryInterface
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReviewRepositoryImpl(
    private val firestore: FirebaseFirestore,
): ReviewRepositoryInterface {

    private val reviewsCollection = firestore.collection("reviews")

    override suspend fun addReview(review: Review): Result<Unit> {
        return try {
            reviewsCollection.add(review).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReviews(professionalId: String): Result<List<Review>> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("professionalId", professionalId)
                .get()
                .await()
            val reviews = snapshot.documents.map { doc ->
                doc.toObject(Review::class.java)!!.copy(id = doc.id)
            }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserReview(
        professionalId: String,
        userId: String
    ): Result<Review?> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("professionalId", professionalId)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val review = snapshot.documents.firstOrNull()
                ?.toObject(Review::class.java)
            Result.success(review)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}