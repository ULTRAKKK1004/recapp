package com.recapp.ml

import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

/**
 * Extracts voice embeddings from audio data using a TFLite model.
 * This class handles the initialization of the TFLite interpreter and
 * provides methods for inference and similarity calculation.
 */
class VoiceEmbeddingExtractor(private var interpreter: Interpreter? = null) {

    companion object {
        const val EMBEDDING_SIZE = 256
    }

    /**
     * Extracts a 256-dimension embedding from the provided audio data.
     *
     * @param audioData The audio data as a FloatArray.
     * @return A FloatArray representing the voice embedding.
     */
    fun extractEmbedding(audioData: FloatArray): FloatArray {
        val output = FloatArray(EMBEDDING_SIZE)
        
        interpreter?.let { tflite ->
            // In a real implementation, we would reshape the input according to the model's requirements.
            // For now, we assume the model takes the audio data and produces the embedding.
            tflite.run(audioData, output)
        } ?: run {
            // Mock implementation if interpreter is not initialized
            // In a real scenario, this would likely log a warning or return a zero-filled array.
            for (i in 0 until EMBEDDING_SIZE) {
                output[i] = (audioData.getOrNull(i) ?: 0f)
            }
        }
        
        return normalize(output)
    }

    /**
     * Extracts a 256-dimension embedding from the provided audio data.
     *
     * @param byteBuffer The audio data as a ByteBuffer.
     * @return A FloatArray representing the voice embedding.
     */
    fun extractEmbedding(byteBuffer: ByteBuffer): FloatArray {
        val output = FloatArray(EMBEDDING_SIZE)
        
        interpreter?.let { tflite ->
            tflite.run(byteBuffer, output)
        } ?: run {
            // Mock implementation
            byteBuffer.rewind()
            val floatBuffer = byteBuffer.asFloatBuffer()
            for (i in 0 until EMBEDDING_SIZE) {
                if (floatBuffer.hasRemaining()) {
                    output[i] = floatBuffer.get()
                } else {
                    output[i] = 0f
                }
            }
        }
        
        return normalize(output)
    }

    /**
     * Calculates the Cosine Similarity between two embeddings.
     *
     * @param emb1 The first embedding.
     * @param emb2 The second embedding.
     * @return The cosine similarity score (between -1.0 and 1.0).
     */
    fun calculateCosineSimilarity(emb1: FloatArray, emb2: FloatArray): Float {
        if (emb1.size != emb2.size) return 0f
        
        var dotProduct = 0f
        var norm1 = 0f
        var norm2 = 0f
        
        for (i in emb1.indices) {
            dotProduct += emb1[i] * emb2[i]
            norm1 += emb1[i] * emb1[i]
            norm2 += emb2[i] * emb2[i]
        }
        
        val denominator = sqrt(norm1.toDouble()) * sqrt(norm2.toDouble())
        return if (denominator != 0.0) (dotProduct / denominator).toFloat() else 0f
    }

    /**
     * Normalizes a vector to have unit length.
     */
    private fun normalize(vector: FloatArray): FloatArray {
        var norm = 0f
        for (v in vector) {
            norm += v * v
        }
        norm = sqrt(norm.toDouble()).toFloat()
        
        if (norm != 0f) {
            for (i in vector.indices) {
                vector[i] /= norm
            }
        }
        return vector
    }

    /**
     * Updates the TFLite interpreter.
     */
    fun setInterpreter(newInterpreter: Interpreter) {
        this.interpreter = newInterpreter
    }
}
