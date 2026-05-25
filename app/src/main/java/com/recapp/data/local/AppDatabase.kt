package com.recapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Conversation::class, SpeakerProfile::class, AudioSegment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
}
