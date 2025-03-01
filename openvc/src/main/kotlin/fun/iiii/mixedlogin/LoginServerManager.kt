package `fun`.iiii.mixedlogin

import com.velocitypowered.api.util.GameProfile
import `fun`.iiii.mixedlogin.yggdrasil.VirtualYggdrasilServer
import `fun`.iiii.mixedlogin.yggdrasil.offline.VirtualOfflineService
import `fun`.iiii.mixedlogin.yggdrasil.offline.VirtualSubService

class LoginServerManager {
    private val virtualOfflineService = VirtualOfflineService()
    private val virtualSubService = VirtualSubService()
    private val offlineYggdrasilServer = VirtualYggdrasilServer(26748, "127.0.0.1", virtualOfflineService)
    private val subYggdrasilServer = VirtualYggdrasilServer(26749, "127.0.0.1", virtualSubService)

    fun start() {
        try {
            offlineYggdrasilServer.start()
            subYggdrasilServer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shouldOfflineHost(hostName: String): Boolean {
        if (hostName.isEmpty()) return false
        if (hostName.startsWith("offline")) return true
        if (hostName.startsWith("o-")) return true
        return false
    }

    fun startOfflineRequest(userName: String): String {
        return virtualOfflineService.startRequest(userName)
    }

    fun startSubRequest(serverId: String, gameProfile: GameProfile) {
        virtualSubService.startRequest(serverId, gameProfile)
    }
} 