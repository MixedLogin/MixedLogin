package `fun`.iiii.mixedlogin.listener.connection

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.server.RegisteredServer
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.AuthMeVelocityPlugin
import `fun`.iiii.mixedlogin.listener.Listener

@Suppress("UnstableApiUsage")
class PostConnectListener @Inject constructor(
    private val plugin: AuthMeVelocityPlugin,
    private val eventManager: EventManager,
    private val mixedLoginMain: MixedLoginMain
) : Listener<ServerPostConnectEvent> {

    override fun register() {
        eventManager.register(mixedLoginMain, ServerPostConnectEvent::class.java, this)
    }

    override fun executeAsync(event: ServerPostConnectEvent): EventTask {
        return EventTask.async {
            val player = event.player

            val isLogged = plugin.isLogged(player)
            plugin.logDebug { "ServerPostConnectEvent | Player ${player.username} is logged: $isLogged" }
            val server = player.currentServer.map { it.server }.orElse(null)
            if (server == null) {
                plugin.logDebug("ServerPostConnectEvent | Player ${player.username} is not in a server")
                return@async
            }
            val isInAuthServer = plugin.isInAuthServer(player)
            plugin.logDebug("ServerPostConnectEvent | Player ${player.username} is in AuthServer: $isInAuthServer")

            if (!(isLogged && isInAuthServer)) {
                return@async
            }

            plugin.logDebug("ServerPostConnectEvent | Already logged player and connected to an Auth server")
            val messageResult = server.sendPluginMessage(AuthMeVelocityPlugin.MODERN_CHANNEL) { encoder ->
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