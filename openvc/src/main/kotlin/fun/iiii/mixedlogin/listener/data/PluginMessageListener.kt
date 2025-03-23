package `fun`.iiii.mixedlogin.listener.data

import com.google.common.io.ByteArrayDataInput
import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.server.RegisteredServer
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.config.MixedLoginConfig
import `fun`.iiii.mixedlogin.MessageType
import `fun`.iiii.mixedlogin.AuthMeVelocityPlugin
import `fun`.iiii.mixedlogin.listener.Listener
import `fun`.iiii.mixedlogin.utils.AuthMeUtils
import net.kyori.adventure.util.Index
import org.slf4j.Logger
import java.util.Locale

class PluginMessageListener @Inject constructor(
    private val proxy: ProxyServer,
    private val eventManager: EventManager,
    private val logger: Logger,
    private val plugin: AuthMeVelocityPlugin,
    private val mixedLoginMain: MixedLoginMain
) : Listener<PluginMessageEvent> {

    companion object {
        private val TYPES = Index.create(MessageType::class.java) { it.toString() }
    }

    override fun register() {
        eventManager.register(mixedLoginMain, PluginMessageEvent::class.java, this)
    }

    override fun executeAsync(event: PluginMessageEvent): EventTask {
        return EventTask.async {
            plugin.logDebug { "PluginMessageEvent | Start" }
            if (notHandledEvent(event)) {
                plugin.logDebug { "PluginMessageEvent | Not handled" }
                return@async
            }

            // Set the result to handled, the message is dropped at the proxy
            event.result = PluginMessageEvent.ForwardResult.handled()

            // Make sure the message is S -> P, NOT P -> S
            val connection = event.source as? ServerConnection ?: run {
                plugin.logDebug("PluginMessageEvent | Not ServerConnection")
                return@async
            }

            val input = event.dataAsDataStream()
            val message = input.readUTF()
            val type = TYPES.valueOrThrow(message.uppercase(Locale.ROOT))
            val name = input.readUTF()
            val player = proxy.getPlayer(name).orElse(null)

            when (type) {
                MessageType.LOGIN -> {
                    plugin.logDebug("PluginMessageEvent | Login type")
                    if (player != null && plugin.addPlayer(player)) {
                        if (MixedLoginMain.getConfig().sendOnLogin.enable) {
                            createServerConnectionRequest(player, connection)
                        }
                        plugin.logDebug("PluginMessageEvent | Player not null")
                    }
                }

                MessageType.LOGOUT -> {
                    plugin.logDebug("PluginMessageEvent | Logout type")
                    if (player != null && plugin.removePlayer(player)) {
                        plugin.logDebug { "PluginMessageEvent | Player $name not null" }
                    }
                }

                MessageType.REGISTER -> {
                    plugin.logDebug("PluginMessageEvent | Register")
                    if (player != null) {
                        plugin.logDebug { "PluginMessageEvent | Player $name not null" }
                    }
                }

                MessageType.UNREGISTER -> {
                    plugin.logDebug("PluginMessageEvent | Unregister type")
                    if (player != null) {
                        plugin.logDebug { "PluginMessageEvent | Player $name not null" }
                    }
                }

                MessageType.FORCE_UNREGISTER -> {
                    plugin.logDebug { "PluginMessageEvent | Forced Unregister type, player $name" }
                }
            }
        }
    }

    private fun notHandledEvent(event: PluginMessageEvent): Boolean {
        if (!event.result.isAllowed) {
            plugin.logDebug("PluginMessageEvent | Plugin Message already handled")
            return true
        }
        val identifier = event.identifier
        if (!(identifier == AuthMeVelocityPlugin.MODERN_CHANNEL || identifier == AuthMeVelocityPlugin.LEGACY_CHANNEL)) {
            plugin.logDebug { "PluginMessageEvent | Not AuthMeVelocity Identifier: ${identifier.id}" }
            return true
        }
        return false
    }

    private fun createServerConnectionRequest(player: Player, connection: ServerConnection) {
        val loginServer = player.currentServer.orElse(connection).server

        val config = MixedLoginMain.getConfig()

        val toSend = AuthMeUtils.serverToSend(
            config.sendOnLogin.sendMode,
            proxy,
            config.sendOnLogin.servers,
            config.advanced.randomAttempts
        )

        if (toSend.second == null) {
            if (toSend.first != null) {
                logger.warn("The server {} does not exist", toSend.first)
            } else {
                logger.warn("There is not valid server to send")
            }
            return
        }

        if (MixedLoginMain.getConfig().sendOnLogin.requirePermission && !player.hasPermission("authmevelocity.send-on-login")) {
            plugin.logDebug { "PluginMessageEvent # createServerConnectionRequest | The player does not have permission ${player.username}" }
            return
        }
        player.createConnectionRequest(loginServer)
            .connect()
            .thenAccept { result ->
                if (!result.isSuccessful) {
                    logger.info(
                        "Unable to connect the player {} to the server {}",
                        player.username,
                        result.attemptedConnection.serverInfo.name
                    )
                }
            }
    }
} 