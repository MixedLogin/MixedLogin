package `fun`.iiii.mixedlogin.util.uuid

import java.util.*

object PCL2UUIDUtil {
    fun getUUID(name: String): UUID {
        return UUID.fromString(toUUID(getStringUUID(name)))
    }

    private fun getStringUUID(name: String): String = buildString {
        append(fillZeroTo16(Integer.toHexString(name.length)))
        append(fillZeroTo16(java.lang.Long.toHexString(hash(name))))
    }.let(::insertInfo)

    private fun insertInfo(originalUUID: String): String = buildString {
        append(originalUUID.substring(0, 12))
        append('3')
        append(originalUUID.substring(13, 16))
        append('9')
        append(originalUUID.substring(17, 32))
    }

    private fun fillZeroTo16(str: String): String = 
        str.take(16).padStart(16, '0')

    private fun hash(str: String): Long {
        var hash = 5381L
        for (element in str) {
            hash = (hash shl 5) xor hash xor element.code.toLong()
        }
        return hash xor -0x5670afe4397bfcd1L
    }

    private fun toUUID(no_: String): String = buildString {
        append(no_.substring(0, 8))
        append("-")
        append(no_.substring(8, 12))
        append("-") 
        append(no_.substring(12, 16))
        append("-")
        append(no_.substring(16, 20))
        append("-")
        append(no_.substring(20, 32))
    }

    @JvmStatic
    fun main(args: Array<String>) {
//        ksqeib:00000000-0000-3006-998f-555b0138dc4d
        println("ksqeib:${getUUID("ksqeib")}")
        println("Diamonds:${getUUID("Diamonds")}")
    }
} 