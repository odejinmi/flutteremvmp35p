package com.a5starcompany.flutteremv.topwise.util

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import android.util.Base64
import com.a5starcompany.flutteremv.topwise.app.PosApplication
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.SecretKeyFactory

class Encrypt {

    companion object {
        private const val KEYSIZE = 256
        private const val DERIVATION_ITERATIONS = 1000
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
//        private const val KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1"
        private const val SALT_SIZE = 32 // 128 bits
        private const val IV_SIZE = 32 // 128 bits
    }

    /**
     * Encrypts plaintext using AES encryption with PBKDF2 key derivation
     * @param plainText The text to encrypt
     * @param password The password to use for encryption
     * @return Base64 encoded string containing salt + IV + encrypted data
     */
    fun encrypt(plainText: String): String {
        try {
            // Generate random salt and IV
            val salt = generate128BitsOfRandomEntropy()
            val iv = generate128BitsOfRandomEntropy()

            // Convert plaintext to bytes
            val plainTextBytes = plainText.toByteArray(StandardCharsets.UTF_8)

            // Derive key from password using PBKDF2
            val keySpec = PBEKeySpec(PosApplication.getApp().mConsumeData.ipeklive.toCharArray(), salt, DERIVATION_ITERATIONS, KEYSIZE)
            val keyFactory = SecretKeyFactory.getInstance(PosApplication.getApp().mConsumeData.ksnlive)
            val keyBytes = keyFactory.generateSecret(keySpec).encoded
            val secretKey = SecretKeySpec(keyBytes, ALGORITHM)

            // Initialize cipher for encryption
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

            // Encrypt the plaintext
            val encryptedBytes = cipher.doFinal(plainTextBytes)

            // Concatenate salt + IV + encrypted data
            val result = ByteArray(salt.size + iv.size + encryptedBytes.size)
            System.arraycopy(salt, 0, result, 0, salt.size)
            System.arraycopy(iv, 0, result, salt.size, iv.size)
            System.arraycopy(encryptedBytes, 0, result, salt.size + iv.size, encryptedBytes.size)

            // Return Base64 encoded result
//            return Base64.getEncoder().encodeToString(result)
            return Base64.encodeToString(result, Base64.DEFAULT)

        } catch (e: Exception) {
            throw RuntimeException("Encryption failed", e)
        }
    }

    /**
     * Decrypts ciphertext that was encrypted using the encrypt method
     * @param cipherText Base64 encoded string containing salt + IV + encrypted data
     * @param password The password used for encryption
     * @return The decrypted plaintext
     */
    fun decrypt(cipherText: String, password: String): String {
        try {
            // Decode Base64 ciphertext
//            val cipherTextBytesWithSaltAndIv = Base64.getDecoder().decode(cipherText)
            val cipherTextBytesWithSaltAndIv = Base64.decode(cipherText, Base64.DEFAULT)

            // Extract salt (first 16 bytes)
            val salt = ByteArray(SALT_SIZE)
            System.arraycopy(cipherTextBytesWithSaltAndIv, 0, salt, 0, SALT_SIZE)

            // Extract IV (next 16 bytes)
            val iv = ByteArray(IV_SIZE)
            System.arraycopy(cipherTextBytesWithSaltAndIv, SALT_SIZE, iv, 0, IV_SIZE)

            // Extract encrypted data (remaining bytes)
            val encryptedBytes = ByteArray(cipherTextBytesWithSaltAndIv.size - SALT_SIZE - IV_SIZE)
            System.arraycopy(
                cipherTextBytesWithSaltAndIv,
                SALT_SIZE + IV_SIZE,
                encryptedBytes,
                0,
                encryptedBytes.size
            )

            // Derive key from password using same parameters as encryption
            val keySpec = PBEKeySpec(password.toCharArray(), salt, DERIVATION_ITERATIONS, KEYSIZE)
            val keyFactory = SecretKeyFactory.getInstance(PosApplication.getApp().mConsumeData.ksnlive)
            val keyBytes = keyFactory.generateSecret(keySpec).encoded
            val secretKey = SecretKeySpec(keyBytes, ALGORITHM)

            // Initialize cipher for decryption
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

            // Decrypt the data
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            // Convert back to string
            return String(decryptedBytes, StandardCharsets.UTF_8)

        } catch (e: Exception) {
            throw RuntimeException("Decryption failed", e)
        }
    }

    /**
     * Generates 128 bits (16 bytes) of cryptographically secure random data
     * @return ByteArray containing 16 random bytes
     */
    private fun generate128BitsOfRandomEntropy(): ByteArray {
        val randomBytes = ByteArray(32)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(randomBytes)
        return randomBytes
    }
}

// Usage example and test
//fun main() {
//    val encryptor = Encrypt()
//
//    try {
//        // Test encryption and decryption
//        val originalText = "Hello, World! This is a test message for encryption."
//        val password = "mySecretPassword123"
//
//        println("Original text: $originalText")
//
//        // Encrypt
//        val encrypted = encryptor.encrypt(originalText, password)
//        println("Encrypted: $encrypted")
//
//        // Decrypt
//        val decrypted = encryptor.decrypt(encrypted, password)
//        println("Decrypted: $decrypted")
//
//        // Verify
//        println("Encryption/Decryption successful: ${originalText == decrypted}")
//
//    } catch (e: Exception) {
//        println("Error: ${e.message}")
//        e.printStackTrace()
//    }
//}