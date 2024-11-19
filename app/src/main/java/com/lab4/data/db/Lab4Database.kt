package com.lab4.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lab4.data.dao.SubjectDao
import com.lab4.data.dao.SubjectLabsDao
import com.lab4.data.entity.SubjectEntity
import com.lab4.data.entity.SubjectLabEntity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(entities = [SubjectEntity::class, SubjectLabEntity::class], version = 1)
abstract class Lab4Database : RoomDatabase() {
    abstract val subjectsDao: SubjectDao
    abstract val subjectLabsDao: SubjectLabsDao
}

object DatabaseStorage {
    private val coroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        },
    )

    private var _database: Lab4Database? = null

    fun getDatabase(context: Context): Lab4Database {
        if (_database != null) return _database as Lab4Database

        else {
            _database = Room.databaseBuilder(
                context,
                Lab4Database::class.java, "lab4Database"
            ).build()

            preloadData()

            return _database as Lab4Database
        }
    }

    private fun preloadData() {
        val listOfSubject = listOf(
            SubjectEntity(id = 1, title = "Розгортання інформаційно-комунікаційних систем"),
            SubjectEntity(id = 2, title = "Мобільні додатки"),
            SubjectEntity(id = 3, title = "Проєктування мультисервісних інформаційно-комунікаційних систем\n"),
        )

        val listOfSubjectLabs = listOf(
            SubjectLabEntity(
                id = 1,
                subjectId = 1,
                title = "Networking in Docker",
                description = "Зробити акаунт в DockerHub",
                comment = "Зробити",
                inProgress = true
            ),
            SubjectLabEntity(
                id = 2,
                subjectId = 1,
                title = "Docker compose",
                description = "Завантажити Docker на свій девайс",
                comment = "Виконано",
                isCompleted = true
            ),
            SubjectLabEntity(
                id = 3,
                subjectId = 2,
                title = "Завантажити Android Studio",
                description = "Встановити та запустити середовище розробки",
                comment = "Виконано",
                isCompleted = true
            ),
            SubjectLabEntity(
                id = 4,
                subjectId = 2,
                title = "Освоїти DataStore в Android",
                description = "Написати простий додаток який буде відслідковувати стан проходження вашого навчання у семестрі",
                comment = "Виконую",
                inProgress = true
            ),
            SubjectLabEntity(
                id = 5,
                subjectId = 3,
                title = "Визначення вимог до розроблюваної інформаційної\n" +
                        "системи",
                description = "Аналіз тезнічних цілей та обмежень",
                comment = "Виконано",
                isCompleted = true
            ),
            SubjectLabEntity(
                id = 6,
                subjectId = 3 ,
                title = "Стадії і етапи процесу проектування ІС",
                description = "Процес мережевого планування та прогнозування навантаження",
                comment = "Захищено",
                isCompleted = true
            )
        )

        listOfSubject.forEach { subject ->
            coroutineScope.launch {
                _database?.subjectsDao?.addSubject(subject)
            }
        }
        listOfSubjectLabs.forEach { lab ->
            coroutineScope.launch {
                _database?.subjectLabsDao?.addSubjectLab(lab)
            }
        }
    }
}