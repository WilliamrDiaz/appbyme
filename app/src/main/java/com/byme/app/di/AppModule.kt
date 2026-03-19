package com.byme.app.di

import android.content.Context
import com.byme.app.data.remote.repository.AppointmentRepositoryImpl
import com.byme.app.data.remote.repository.CategoryRepositoryImpl
import com.byme.app.data.remote.repository.ChatRepositoryImpl
import com.byme.app.data.remote.repository.ReviewRepositoryImpl
import com.byme.app.data.remote.repository.ScheduleRepositoryImpl
import com.byme.app.data.remote.repository.ServiceRepositoryImpl
import com.byme.app.data.remote.repository.UserRepositoryImpl
import com.byme.app.domain.repository.AppointmentRepositoryInterface
import com.byme.app.domain.repository.CategoryRepositoryInterface
import com.byme.app.domain.repository.ChatRepositoryInterface
import com.byme.app.domain.repository.ReviewRepositoryInterface
import com.byme.app.domain.repository.ScheduleRepositoryInterface
import com.byme.app.domain.repository.ServiceRepositoryInterface
import com.byme.app.domain.repository.UserRepositoryInterface
import com.byme.app.domain.usecase.GetChatsUseCase
import com.byme.app.domain.usecase.GetMessagesUseCase
import com.byme.app.domain.usecase.GetProfessionalsUseCase
import com.byme.app.domain.usecase.GetUserUseCase
import com.byme.app.domain.usecase.LoginUseCase
import com.byme.app.domain.usecase.RegisterUseCase
import com.byme.app.domain.usecase.SearchProfessionalsUseCase
import com.byme.app.domain.usecase.SendMessageUseCase
import com.byme.app.domain.usecase.UpdateUserUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.room.Room
import com.byme.app.data.local.dao.UserDao
import com.byme.app.data.local.database.ByMeDatabase
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Inyeccion de dependencias para Firebase
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    // Inyeccion de dependencias para Repositorios

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        userDao: UserDao
    ): UserRepositoryInterface {
        return UserRepositoryImpl(firestore, userDao)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        firestore: FirebaseFirestore
    ): ChatRepositoryInterface {
        return ChatRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(
        firestore: FirebaseFirestore
    ): ReviewRepositoryInterface {
        return ReviewRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideAppointmentRepository(
        firestore: FirebaseFirestore
    ): AppointmentRepositoryInterface {
        return AppointmentRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideServiceRepository(firestore: FirebaseFirestore): ServiceRepositoryInterface {
        return ServiceRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(firestore: FirebaseFirestore): ScheduleRepositoryInterface {
        return ScheduleRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(firestore: FirebaseFirestore): CategoryRepositoryInterface {
        return CategoryRepositoryImpl(firestore)
    }

    // Inyeccion de dependencias para Casos de uso

    @Provides
    fun provideRegisterUseCase(
        userRepository: UserRepositoryInterface,
        auth: FirebaseAuth
    ): RegisterUseCase {
        return RegisterUseCase(userRepository, auth)
    }

    @Provides
    fun provideLoginUseCase(
        auth: FirebaseAuth
    ): LoginUseCase {
        return LoginUseCase(auth)
    }

    @Provides
    fun provideGetProfessionalsUseCase(
        userRepository: UserRepositoryInterface
    ): GetProfessionalsUseCase {
        return GetProfessionalsUseCase(userRepository)
    }

    @Provides
    fun provideSearchProfessionalsUseCase(
        userRepository: UserRepositoryInterface
    ): SearchProfessionalsUseCase {
        return SearchProfessionalsUseCase(userRepository)
    }

    @Provides
    fun provideGetUserUseCase(
        userRepository: UserRepositoryInterface
    ): GetUserUseCase {
        return GetUserUseCase(userRepository)
    }

    @Provides
    fun provideUpdateUserUseCase(
        userRepository: UserRepositoryInterface
    ): UpdateUserUseCase {
        return UpdateUserUseCase(userRepository)
    }

    @Provides
    fun provideSendMessageUseCase(
        chatRepository: ChatRepositoryInterface
    ): SendMessageUseCase {
        return SendMessageUseCase(chatRepository)
    }

    @Provides
    fun provideGetChatsUseCase(
        chatRepository: ChatRepositoryInterface
    ): GetChatsUseCase {
        return GetChatsUseCase(chatRepository)
    }

    @Provides
    fun provideGetMessagesUseCase(
        chatRepository: ChatRepositoryInterface
    ): GetMessagesUseCase {
        return GetMessagesUseCase(chatRepository)
    }

    // Inyección de dependencias para Room
    @Provides
    @Singleton
    fun provideByMeDatabase(@ApplicationContext context: Context): ByMeDatabase {
        return Room.databaseBuilder(
            context,
            ByMeDatabase::class.java,
            "byme_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: ByMeDatabase): UserDao {
        return database.userDao()
    }
}