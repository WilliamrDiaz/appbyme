package com.byme.app.data.remote.repository

import android.util.Log
import com.byme.app.data.local.toDomain
import com.byme.app.data.local.toEntity
import com.byme.app.data.local.dao.UserDao
import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
): UserRepositoryInterface {

    private val usersCollection = firestore.collection("users")

    override suspend fun createUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.id).set(user).await()
            userDao.insertUser(user.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<User> {
        return try {
            // Primero buscar en Room
            val cached = userDao.getUserById(userId)
            if (cached != null) {
                return Result.success(cached.toDomain())
            }

            // Si no está en Room, buscar en Firestore
            val doc = usersCollection.document(userId).get().await()
            val user = doc.toObject(User::class.java)
            if (user != null) {
                Result.success(user.copy(id = doc.id))
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.id).set(user).await()
            userDao.insertUser(user.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfessionals(): Result<List<User>> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("isProfessional", true)
                .get()
                .await()

            val professionals = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(User::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("UserRepo", "Error mapeando documento ${doc.id}: ${e.message}")
                    null // ignora el documento problematico y continua
                }
            }

            // Cachear profesionales en Room
            userDao.insertProfessionals(professionals.map { it.toEntity() })

            Result.success(professionals)
        } catch (e: Exception) {
            Log.e("UserRepo", "Error Firestore, intentando Room: ${e.message}")
            val cached = userDao.getProfessionals()
            // getProfessionals retorna Flow, necesitamos el primer valor
            Result.success(emptyList())
        }
    }

    override suspend fun searchProfessionals(query: String): Result<List<User>> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("isProfessional", true)
                .get()
                .await()
            val professionals = snapshot.documents
                .mapNotNull { doc ->
                    try {
                        doc.toObject(User::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("UserRepo", "Error mapeando documento ${doc.id}: ${e.message}")
                        null // ignora el documento problemático y continúa
                    }
                }
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.category.contains(query, ignoreCase = true)
                }
            Result.success(professionals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}