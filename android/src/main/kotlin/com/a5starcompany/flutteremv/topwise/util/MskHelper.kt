package com.a5starcompany.flutteremv.topwise.util

import com.a5starcompany.flutteremv.topwise.app.PosApplication
import java.io.ByteArrayOutputStream
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

object MskHelper {

    /**
     * Generate working key using MSK algorithm
     * @param masterKey The master session key (MSK)
     * @param terminalId Terminal identifier
     * @param transactionCounter Transaction counter
     * @return Working key for encryption
     */
//    fun getWorkingKey(
//        masterKey: String = PosApplication.getApp().mConsumeData.masterKey,
//        terminalId: String = PosApplication.getApp().mConsumeData.terminalId,
//    ): String {
//        // Pad terminal ID and transaction counter to proper lengths
//        val paddedTerminalId = terminalId.padStart(16, '0').take(16)
//        val paddedCounter = transactionCounter.padStart(16, '0').take(16)
//
//        println("Master Key: $masterKey")
//        println("Terminal ID: $paddedTerminalId")
//        println("Transaction Counter: $paddedCounter")
//
//        // Create diversification data by combining terminal ID and counter
//        val diversificationData = paddedTerminalId + paddedCounter
//        println("Diversification Data: $diversificationData")
//
//        // Generate working key using 3DES encryption
//        val workingKey = generateWorkingKey(masterKey, diversificationData)
//        println("Working Key: $workingKey")
//
//        return workingKey
//    }

    /**
     * Generate working key using 3DES encryption with diversification data
     */
    private fun generateWorkingKey(masterKey: String, diversificationData: String): String {
        // Split master key into left and right halves for 3DES
        val leftKey = masterKey.substring(0, 16)
        val rightKey = masterKey.substring(16, 32)

        // Split diversification data into blocks
        val leftData = diversificationData.substring(0, 16)
        val rightData = diversificationData.substring(16, 32)

        // Perform 3DES operation: Encrypt with left key, decrypt with right key, encrypt with left key
        val step1 = desEncrypt(leftData, leftKey)
        val step2 = desDecrypt(step1, rightKey)
        val leftWorkingKey = desEncrypt(step2, leftKey)

        val step3 = desEncrypt(rightData, leftKey)
        val step4 = desDecrypt(step3, rightKey)
        val rightWorkingKey = desEncrypt(step4, leftKey)

        return leftWorkingKey + rightWorkingKey
    }

    /**
     * Encrypt PIN block using MSK working key
     */
    fun encryptPinBlock(workingKey: String, pan: String, pin: String): String {
        val pinBlock = createPinBlock(pan, pin)
        println("PIN Block: $pinBlock")

        // Encrypt PIN block with working key using DES
        val encryptedPinBlock = desEncrypt(pinBlock, workingKey.substring(0, 16))
        println("Encrypted PIN Block: $encryptedPinBlock")

        return encryptedPinBlock
    }

    /**
     * Create PIN block in ISO Format 0
     */
    fun createPinBlock(pan: String, pin: String): String {
        // Format PIN: 0 + PIN length + PIN + padding with F
        val formattedPin = "0" + pin.length.toString(16).uppercase() +
                pin.padEnd(14, 'F')

        // Format PAN: take rightmost 12 digits of PAN (excluding check digit)
        val panSequence = pan.takeLast(13).dropLast(1).padStart(16, '0')

        println("Formatted PIN: $formattedPin")
        println("PAN Sequence: $panSequence")

        // XOR PIN and PAN
        return xorHexStrings(formattedPin, panSequence)
    }

    /**
     * Generate MAC (Message Authentication Code) using MSK
     */
    fun generateMac(workingKey: String, data: String): String {
        // Pad data to multiple of 8 bytes
        val paddedData = data.padEnd(((data.length + 7) / 8) * 8, '0')

        // Use CBC-MAC algorithm
        var mac = "0000000000000000" // Initial vector

        for (i in paddedData.indices step 16) {
            val block = paddedData.substring(i, minOf(i + 16, paddedData.length))
                .padEnd(16, '0')
            mac = xorHexStrings(mac, block)
            mac = desEncrypt(mac, workingKey.substring(0, 16))
        }

        return mac.substring(0, 8) // Return first 4 bytes as MAC
    }

    /**
     * XOR two hexadecimal strings
     */
    fun xorHexStrings(valueA: String, valueB: String): String {
        val a = valueA.toCharArray()
        val b = valueB.toCharArray()
        var result = ""

        val minLength = minOf(a.size, b.size)
        for (i in 0 until minLength) {
            val hexA = a[i].toString().toInt(16)
            val hexB = b[i].toString().toInt(16)
            result += (hexA xor hexB).toString(16).uppercase()
        }
        return result
    }

    /**
     * Convert hex string to byte array
     */
    fun hexStringToByteArray(hex: String): ByteArray {
        val result = ByteArray(hex.length / 2)
        for (i in hex.indices step 2) {
            result[i / 2] = hex.substring(i, i + 2).toInt(16).toByte()
        }
        return result
    }

    /**
     * Convert byte array to hex string
     */
    fun byteArrayToHexString(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02X".format(it) }
    }

    /**
     * DES encryption
     */
    private fun desEncrypt(data: String, key: String): String {
        return try {
            val keyData = hexStringToByteArray(key)
            val keySpec: KeySpec = DESKeySpec(keyData)
            val secretKey: SecretKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec)
            val cipher: Cipher = Cipher.getInstance("DES/ECB/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encrypted = cipher.doFinal(hexStringToByteArray(data))
            byteArrayToHexString(encrypted)
        } catch (e: Exception) {
            println("DES Encryption error: ${e.message}")
            ""
        }
    }

    /**
     * DES decryption
     */
    private fun desDecrypt(data: String, key: String): String {
        return try {
            val keyData = hexStringToByteArray(key)
            val keySpec: KeySpec = DESKeySpec(keyData)
            val secretKey: SecretKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec)
            val cipher: Cipher = Cipher.getInstance("DES/ECB/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decrypted = cipher.doFinal(hexStringToByteArray(data))
            byteArrayToHexString(decrypted)
        } catch (e: Exception) {
            println("DES Decryption error: ${e.message}")
            ""
        }
    }
//
//    /**
//     * Example usage and testing
//     */
//    fun testMskImplementation() {
//        val masterKey = "0123456789ABCDEF0123456789ABCDEF"
//        val terminalId = "12345678"
//        val transactionCounter = "000001"
//        val pan = "1234567890123456"
//        val pin = "1234"
//
//        println("=== MSK Algorithm Test ===")
//
//        // Generate working key
////        val workingKey = getWorkingKey(masterKey, terminalId, transactionCounter)
////
////        // Encrypt PIN block
////        val encryptedPin = encryptPinBlock(workingKey, pan, pin)
////
////        // Generate MAC for sample data
////        val sampleData = "1234567890ABCDEF"
////        val mac = generateMac(workingKey, sampleData)
////
////        println("\n=== Results ===")
////        println("Working Key: $workingKey")
////        println("Encrypted PIN: $encryptedPin")
////        println("MAC: $mac")
//    }
//}


const val MASTER_KEY_DEFAULT = "0123456789ABCDEFFEDCBA9876543210"
const val DERIVATION_DATA_DEFAULT = "00000000000000000000000000000000"

//object MskHelper {
//    fun generateSessionKey(
//        masterKey: String = MASTER_KEY_DEFAULT,
//        derivationData: String = DERIVATION_DATA_DEFAULT
//    ): String {
//        // MSK typically uses simpler key derivation than DUKPT
//        val sessionKey = deriveKeyUsingMasterKey(masterKey, derivationData)
//        return formatSessionKey(sessionKey)
//    }
//
//    private fun deriveKeyUsingMasterKey(masterKey: String, derivationData: String): String {
//        // Simple key derivation using XOR (replace with your actual MSK derivation algorithm)
//        return XORorANDorORfunction(masterKey, derivationData, "^")
//    }
//
//    private fun formatSessionKey(rawKey: String): String {
//        // Apply any required formatting to the session key
//        return rawKey.substring(0, 32) // Return first 16 bytes (32 hex chars)
//    }
//
//    fun XORorANDorORfunction(valueA: String, valueB: String, symbol: String = "|"): String {
//        val a = valueA.toCharArray()
//        val b = valueB.toCharArray()
//        var result = ""
//
//        for (i in 0 until a.lastIndex + 1) {
//            if (symbol === "|") {
//                result += (Integer.parseInt(a[i].toString(), 16).or
//                    (Integer.parseInt(b[i].toString(), 16)).toString(16).toUpperCase())
//            } else if (symbol === "^") {
//                result += (Integer.parseInt(a[i].toString(), 16).xor
//                    (Integer.parseInt(b[i].toString(), 16)).toString(16).toUpperCase())
//            } else {
//                result += (Integer.parseInt(a[i].toString(), 16).and
//                    (Integer.parseInt(b[i].toString(), 16))).toString(16).toUpperCase()
//            }
//        }
//        return result
//    }
//
//    fun encryptPinBlock(pan: String, pin: String): String {
//        val pan = pan.substring(pan.length - 13).take(12).padStart(16, '0')
//        val pin = '0' + pin.length.toString(16) + pin.padEnd(16, 'F')
//        return XORorANDorORfunction(pan, pin, "^")
//    }
//
//    fun hexStringToByteArray(key: String): ByteArray {
//        var result: ByteArray = ByteArray(0)
//        for (i in 0 until key.length step 2) {
//            result += Integer.parseInt(key.substring(i, (i + 2)), 16).toByte()
//        }
//        return result
//    }
//
//    fun byteArrayToHexString(key: ByteArray): String {
//        var st = ""
//        for (b in key) {
//            st += String.format("%02X", b)
//        }
//        return st
//    }
//
//    fun encryptWithSessionKey(sessionKey: String, data: String): String {
//        val keyData = hexStringToByteArray(sessionKey)
//        val bout = ByteArrayOutputStream()
//        try {
//            val keySpec: KeySpec = DESKeySpec(keyData)
//            val key: SecretKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec)
//            val cipher: Cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
//            cipher.init(Cipher.ENCRYPT_MODE, key)
//            bout.write(cipher.doFinal(hexStringToByteArray(data)))
//        } catch (e: Exception) {
//            println("Exception during encryption: " + e.message)
//        }
//        return byteArrayToHexString(bout.toByteArray())
//    }
//
//    fun encryptPinWithMsk(sessionKey: String, pan: String, clearPin: String): String {
//        val pinBlock = encryptPinBlock(pan, clearPin)
//        return encryptWithSessionKey(sessionKey, pinBlock)
//    }
//
}