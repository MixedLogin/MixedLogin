package `fun`.iiii.mixedlogin.util.uuid

import java.util.*

object PCL2UUIDUtil {
//    前几个方法主要是模拟和复现PCL2的生成过程
    fun getUUID(name: String): UUID {
        return UUID.fromString(toUUID(getStringUUID(name)))
    }

    private fun getStringUUID(name: String): String = buildString {
        append(fillZeroTo16(Integer.toHexString(name.length)))
        append(fillZeroTo16(java.lang.Long.toHexString(hash(name))))
    }.let(::insertInfo)

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

    private fun insertInfo(originalUUID: String): String = buildString {
        append(originalUUID.substring(0, 12))
        append('3')
        append(originalUUID.substring(13, 16))
        append('9')
        append(originalUUID.substring(17, 32))
    }
//    下面的方法才是主要使用的方法
    private fun fillZeroTo16(str: String): String =
        str.take(16).padStart(16, '0')

    private fun hash(str: String): Long {
        var hash = 5381L
        for (element in str) {
            hash = (hash shl 5) xor hash xor element.code.toLong()
        }
        return hash xor -0x5670afe4397bfcd1L
    }

    fun buildInfoPart(name: String): String {
        val partA=fillZeroTo16(Integer.toHexString(name.length)) + "9"
        return partA.replaceRange(12,13,"3")
    }

    //    粗略匹配函数
    fun isPCL2UUID(uuid: UUID, name: String, deepMatch: Boolean = false): Boolean {
        if (!deepMatch) return hasPCL2Info(uuid,name)

        val strRemove = uuid.toString().replace("-", "")
//      进行需要哈希运算的深度匹配
        val hash = fillZeroTo16(java.lang.Long.toHexString(hash(name)))
//       需要截取后十五位做比较
        return strRemove.substring(17, 32) == hash.substring(1, 16)
    }

    private fun hasPCL2Info(uuid: UUID,name: String): Boolean {
        val strRemove = uuid.toString().replace("-", "")
        val info= buildInfoPart(name)
        return strRemove.substring(0, 17) == info
    }

    private fun hasPCL2Info(uuid: UUID): Boolean {
        val strRemove = uuid.toString().replace("-", "")
//        判断前11位是不是0
        if (strRemove.substring(0, 12) != "000000000000") return false
//        判断 13~15位是不是0
        if (strRemove.substring(13, 15) != "00") return false
        return strRemove[12] == '3' && strRemove[16] == '9'
    }

    @JvmStatic
    fun main(args: Array<String>) {
//        ksqeib:00000000-0000-3006-998f-555b0138dc4d
        println("ksqeib:${getUUID("ksqeib")}")
        println("Diamonds:${getUUID("Diamonds")}")
        println("rule:${isPCL2UUID(getUUID("ksqeib"), "ksqeib",true)}")
        println("rule:${isPCL2UUID(getUUID("ksqeibksqeib"), "ksqeibksqeib",true)}")
    }
} 