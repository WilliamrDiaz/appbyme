package com.byme.app.data.remote.repository

import android.util.Log
import com.byme.app.domain.model.Service
import com.byme.app.domain.repository.ServiceRepositoryInterface
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ServiceRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : ServiceRepositoryInterface {
    override suspend fun getServices(userId: String): Result<List<Service>> {
        return try {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("services")
                .get()
                .await()

            val services = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Service::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("ServiceRepositoryImpl", "Error converting document to Service", e)
                    null
                }
            }
            Result.success(services)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addService(
        userId: String,
        service: Service
    ): Result<Unit> {
        val serviceMap = hashMapOf(
            "name" to service.name,
            "description" to service.description
        )

        return try {
            firestore
                .collection("users")
                .document(userId)
                .collection("services")
                .add(serviceMap)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteService(
        userId: String,
        serviceId: String
    ): Result<Unit> {
        return try {
            firestore
                .collection("users")
                .document(userId)
                .collection("services")
                .document(serviceId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}