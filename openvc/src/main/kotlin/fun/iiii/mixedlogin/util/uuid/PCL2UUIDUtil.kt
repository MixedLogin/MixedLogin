package `fun`.iiii.mixedlogin.util.uuid

import java.util.*

object PCL2UUIDUtil {
    fun getUUID(name: String): UUID {
        return UUID.fromString(toUUID(getStringUUID(name)))
    }

    private fun getStringUUID(name: String): String {
        val partA = fillZeroTo16(Integer.toHexString(name.length))
        val partB = fillZeroTo16(java.lang.Long.toHexString(hash(name)))
        val full = partA + partB
        println("partA: $partA")
        println("partB: $partB")
        return insertInfo(full)
    }

    private fun insertInfo(originalUUID: String): String {
        return originalUUID.substring(0, 12) + "3" +
                originalUUID.substring(13, 16) + "9" +
                originalUUID.substring(17, 32)
    }

    private fun fillZeroTo16(str: String): String {
        if (str.length > 16) {
            return str.substring(0, 16)
        }
        return "0".repeat(16 - str.length) + str
    }

    private fun hash(str: String): Long {
        var hash = 5381L
        for (element in str) {
            hash = (hash shl 5) xor hash xor element.code.toLong()
        }
        return hash xor -0x5670afe4397bfcd1L
    }

    private fun toUUID(no_: String): String {
        return no_.substring(0, 8) + "-" +
                no_.substring(8, 12) + "-" +
                no_.substring(12, 16) + "-" +
                no_.substring(16, 20) + "-" +
                no_.substring(20, 32)
    }
} 