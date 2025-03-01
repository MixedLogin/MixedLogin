package `fun`.iiii.mixedlogin.util

import `fun`.iiii.mixedlogin.type.OfflineUUIDType
import `fun`.iiii.mixedlogin.util.uuid.PCL2UUIDUtil
import java.nio.charset.StandardCharsets
import java.util.UUID

object ExtraUuidUtils {
    private val none: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    fun matchType(holderUUID: UUID?, name: String): OfflineUUIDType {
        if (holderUUID == null) {
            return OfflineUUIDType.NONE
        }
        return when {
            holderUUID == getNormalOfflineUUID(name) -> OfflineUUIDType.OFFLINE
            PCL2UUIDUtil.isPCL2UUID(holderUUID,name) -> OfflineUUIDType.PCL
            holderUUID == none -> OfflineUUIDType.NONE
            else -> OfflineUUIDType.UNKNOWN
        }
    }

    private fun getNormalOfflineUUID(username: String): UUID {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:$username").toByteArray(StandardCharsets.UTF_8))
    }

} 