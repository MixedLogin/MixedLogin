package `fun`.iiii.mixedlogin.listener.connection

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.manager.AuthMeManager
import `fun`.iiii.mixedlogin.listener.Listener

@Suppress("UnstableApiUsage")
class PostConnectListener @Inject constructor(
    private val authMeManager: AuthMeManager,
    private val eventManager: EventManager,
    private val plugin: MixedLoginMain
) : Listener<ServerPostConnectEvent> {

    override fun register() {
        eventManager.register(plugin, ServerPostConnectEvent::class.java, this)
    }

    override fun executeAsync(event: ServerPostConnectEvent): EventTask {
        return EventTask.async {
            val player = event.player

            val isLogged = authMeManager.isLogged(player)
            plugin.logDebug { "ServerPostConnectEvent | Player ${player.username} is logged: $isLogged" }
            val server = player.currentServer.map { it.server }.orElse(null)
            if (server == null) {
                plugin.logDebug("ServerPostConnectEvent | Player ${player.username} is not in a server")
                return@async
            }
            val isInAuthServer = authMeManager.isInAuthServer(player)
            plugin.logDebug("ServerPostConnectEvent | Player ${player.username} is in AuthServer: $isInAuthServer")

            if (!(isLogged && isInAuthServer)) {
                return@async
            }

            plugin.logDebug("ServerPostConnectEvent | Already logged player and connected to an Auth server")
            val messageResult = server.sendPluginMessage(AuthMeManager.MODERN_CHANNEL) { encoder ->
                plugin.logDebug { "ServerPostConnectEvent | ${player.username} | Encoding LOGIN data" }
                encoder.writeUTF("LOGIN")
                encoder.writeUTF(player.username)
                plugin.logDebug { "ServerPostConnectEvent | ${player.username} | Sending LOGIN data" }
            }
            if (messageResult) {
                plugin.logDebug { "ServerPostConnectEvent | ${player.username} | Correctly send data" }
            } else {
                plugin.logDebug("ServerPostConnectEvent | Failed to send data")
            }
        }
    }
} 