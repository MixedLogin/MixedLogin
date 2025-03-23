package `fun`.iiii.mixedlogin.listener.connection

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.proxy.ProxyServer
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.manager.AuthMeManager
import `fun`.iiii.mixedlogin.listener.Listener
import `fun`.iiii.mixedlogin.utils.AuthMeUtils
import org.slf4j.Logger

class InitialServerListener @Inject constructor(
    private val authMeManager: AuthMeManager,
    private val eventManager: EventManager,
    private val proxy: ProxyServer,
    private val logger: Logger,
    private val plugin: MixedLoginMain
) : Listener<PlayerChooseInitialServerEvent> {

    override fun register() {
        eventManager.register(plugin, PlayerChooseInitialServerEvent::class.java, PostOrder.LATE, this)
    }

    override fun executeAsync(event: PlayerChooseInitialServerEvent): EventTask {
        return EventTask.withContinuation { continuation ->
            val config = MixedLoginMain.getConfig()
            if (!config.sendNoLogin.enable) {
                continuation.resume()
                plugin.logDebug("PlayerChooseInitialServerEvent | Not enabled")
                return@withContinuation
            }

            if (config.advanced.skipOnlineLogin && event.player.isOnlineMode) {
                authMeManager.addPlayer(event.player)
                plugin.logDebug { "PlayerChooseInitialServerEvent | Player ${event.player.username} is online" }
                continuation.resume()
                return@withContinuation
            }

            val optionalSV = event.initialServer
            if (optionalSV.isPresent && authMeManager.isAuthServer(optionalSV.get())) {
                continuation.resume()
                plugin.logDebug { "PlayerChooseInitialServerEvent | ${event.player.username} | Player is in auth server" }
                return@withContinuation
            }

            val server = AuthMeUtils.serverToSend(
                config.sendNoLogin.sendMode,
                proxy,
                config.authServers,
                config.advanced.randomAttempts
            )

            // Velocity takes over in case the initial server is not present
            event.setInitialServer(server.second)
            continuation.resume()
            if (server.second == null) {
                plugin.logDebug { "PlayerChooseInitialServerEvent | ${event.player.username} | Null server" }
                logger.error("Cannot send the player {} to an auth server", event.player.username)
            }
        }
    }
} 