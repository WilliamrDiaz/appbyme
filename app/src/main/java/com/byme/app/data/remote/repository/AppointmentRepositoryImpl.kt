package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Appointment
import com.byme.app.domain.repository.AppointmentRepositoryInterface
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AppointmentRepositoryImpl(
    private val firestore: FirebaseFirestore,
): AppointmentRepositoryInterface {
    private val appointmentsCollection = firestore.collection("appointments")

    override suspend fun createAppointment(appointment: Appointment): Result<Unit> {
        return try {
            appointmentsCollection.add(appointment).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserAppointments(userId: String): Result<List<Appointment>> {
        return try {
            val snapshot = appointmentsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val appointments = snapshot.documents.map { doc ->
                doc.toObject(Appointment::class.java)!!.copy(id = doc.id)
            }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfessionalAppointments(professionalId: String): Result<List<Appointment>> {
        return try {
            val snapshot = appointmentsCollection
                .whereEqualTo("professionalId", professionalId)
                .get()
                .await()
            val appointments = snapshot.documents.map { doc ->
                doc.toObject(Appointment::class.java)!!.copy(id = doc.id)
            }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAppointmentStatus(
        appointmentId: String,
        status: String
    ): Result<Unit> {
        return try {
            appointmentsCollection.document(appointmentId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}