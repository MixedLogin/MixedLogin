package `fun`.iiii.mixedlogin

import com.velocitypowered.api.util.GameProfile
import `fun`.iiii.mixedlogin.yggdrasil.VirtualYggdrasilServer
import `fun`.iiii.mixedlogin.yggdrasil.offline.VirtualOfflineService
import `fun`.iiii.mixedlogin.yggdrasil.offline.VirtualSubService

class LoginServerManager {
    private val virtualOfflineService = VirtualOfflineService()
    private val virtualSubService = VirtualSubService()
    private val offlineYggdrasilServer = VirtualYggdrasilServer(
        MixedLoginMain.getConfig().offlineYggdrasil.port,
        MixedLoginMain.getConfig().offlineYggdrasil.host,
        virtualOfflineService
    )
    private val subYggdrasilServer = VirtualYggdrasilServer(
        MixedLoginMain.getConfig().subYggdrasil.port,
        MixedLoginMain.getConfig().subYggdrasil.host,
        virtualSubService
    )

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
        MixedLoginMain.getConfig().hostMatch.start.forEach {
            if (hostName.startsWith(it)) return true
        }
        return false
    }

    fun startOfflineRequest(userName: String): String {
        return virtualOfflineService.startRequest(userName)
    }

    fun startSubRequest(serverId: String, gameProfile: GameProfile) {
        virtualSubService.startRequest(serverId, gameProfile)
    }
} 