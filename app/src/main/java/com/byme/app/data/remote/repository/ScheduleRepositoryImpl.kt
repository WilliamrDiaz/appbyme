package com.byme.app.data.remote.repository

import android.util.Log
import com.byme.app.domain.model.Schedule
import com.byme.app.domain.repository.ScheduleRepositoryInterface
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ScheduleRepositoryImpl (
    private val firestore: FirebaseFirestore
) : ScheduleRepositoryInterface {

    override suspend fun getSchedules(userId: String): Result<List<Schedule>> {
        return try {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("schedules")
                .get()
                .await()

            val schedules = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Schedule::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("ScheduleRepositoryImpl", "Error converting document to Schedule", e)
                    null
                }
            }
            Result.success(schedules)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addSchedule(userId: String, schedule: Schedule): Result<Unit> {
        return try {
            firestore
                .collection("users")
                .document(userId)
                .collection("schedules")
                .add(schedule)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSchedule(userId: String, scheduleId: String): Result<Unit> {
        return try {
            firestore
                .collection("users")
                .document(userId)
                .collection("schedules")
                .document(scheduleId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}