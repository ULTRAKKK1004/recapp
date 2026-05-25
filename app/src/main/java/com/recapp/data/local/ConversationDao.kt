package com.recapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    // Conversation operations
    @Query("SELECT * FROM conversations ORDER BY start_time DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getConversationById(id: Int): Flow<Conversation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation): Long

    @Update
    suspend fun updateConversation(conversation: Conversation)

    @Delete
    suspend fun deleteConversation(conversation: Conversation)

    // AudioSegment operations
    @Query("SELECT * FROM audio_segments WHERE conversation_id = :conversationId ORDER BY start_time ASC")
    fun getSegmentsForConversation(conversationId: Int): Flow<List<AudioSegment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioSegment(segment: AudioSegment): Long

    // SpeakerProfile operations
    @Query("SELECT * FROM speaker_profiles")
    fun getAllSpeakerProfiles(): Flow<List<SpeakerProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeakerProfile(profile: SpeakerProfile): Long
}
