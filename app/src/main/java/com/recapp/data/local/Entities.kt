package com.recapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "end_time") val endTime: Long? = null,
    val summary: String? = null
)

@Entity(tableName = "speaker_profiles")
data class SpeakerProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    @ColumnInfo(name = "embedding_blob") val embeddingBlob: ByteArray
)

@Entity(tableName = "audio_segments")
data class AudioSegment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "conversation_id") val conversationId: Int,
    @ColumnInfo(name = "start_time") val startTime: Long,
    val text: String,
    @ColumnInfo(name = "speaker_id") val speakerId: Int? = null,
    @ColumnInfo(name = "embedding_blob") val embeddingBlob: ByteArray? = null
)
