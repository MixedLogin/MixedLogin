package `fun`.iiii.mixedlogin.listener.connection

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.proxy.server.RegisteredServer
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.config.MixedLoginConfig
import `fun`.iiii.mixedlogin.AuthMeVelocityPlugin
import `fun`.iiii.mixedlogin.listener.Listener

class PreConnectListener @Inject constructor(
    private val plugin: AuthMeVelocityPlugin,
    private val eventManager: EventManager,
    private val mixedLoginMain: MixedLoginMain
) : Listener<ServerPreConnectEvent> {

    override fun register() {
        eventManager.register(mixedLoginMain, ServerPreConnectEvent::class.java, this)
    }

    override fun executeAsync(event: ServerPreConnectEvent): EventTask {
        return EventTask.withContinuation { continuation ->
            val config = MixedLoginMain.getConfig()
            if (config.advanced.skipOnlineLogin && event.player.isOnlineMode) {
                plugin.addPlayer(event.player)
                plugin.logDebug { "ServerPreConnectEvent | Player ${event.player.username} is online" }
                continuation.resume()
                return@withContinuation
            }
            if (plugin.isLogged(event.player)) {
                plugin.logDebug { "ServerPreConnectEvent | Player ${event.player.username} is already logged" }
                continuation.resume()
                return@withContinuation
            }

            val server = event.result.server.orElse(null)
            if (server == null) {
                plugin.logDebug { "ServerPreConnectEvent | ${event.player.username} | Null Server" }
                continuation.resume()
                return@withContinuation
            }
            // this should be present, "event.getResult().isAllowed()" is the "isPresent" check
            if (!plugin.isAuthServer(server)) {
                plugin.logDebug("ServerPreConnectEvent | Server ${server.serverInfo.name} is not an auth server")
                event.result = ServerPreConnectEvent.ServerResult.denied()
            } else {
                plugin.logDebug("ServerPreConnectEvent | Server ${server.serverInfo.name} is an auth server")
            }
            continuation.resume()
        }
    }
} 