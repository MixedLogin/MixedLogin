package `fun`.iiii.mixedlogin.listener.input

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.command.CommandExecuteEvent
import com.velocitypowered.api.proxy.Player
import `fun`.iiii.mixedlogin.AuthMeVelocityPlugin
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.listener.Listener
import `fun`.iiii.mixedlogin.utils.AuthMeUtils
import net.kyori.adventure.text.minimessage.MiniMessage

class CommandListener @Inject constructor(
    private val eventManager: EventManager,
    private val plugin: AuthMeVelocityPlugin,
    private val mixedLoginMain: MixedLoginMain
) : Listener<CommandExecuteEvent> {

    override fun register() {
        eventManager.register(mixedLoginMain, CommandExecuteEvent::class.java, PostOrder.FIRST, this)
    }

    override fun executeAsync(event: CommandExecuteEvent): EventTask {
        return EventTask.withContinuation { continuation ->
            val player = event.commandSource as? Player ?: run {
                plugin.logDebug { "CommandExecuteEvent | CommandSource is not a player" }
                continuation.resume()
                return@withContinuation
            }

            if (plugin.isLogged(player)) {
                plugin.logDebug { "CommandExecuteEvent | Player ${player.username} is already logged" }
                continuation.resume()
                return@withContinuation
            }

            if (plugin.isInAuthServer(player)) {
                plugin.logDebug { "CommandExecuteEvent | Player ${player.username} is in Auth Server" }
                val command = AuthMeUtils.getFirstArgument(event.command)
                if (!plugin.config().commands.allowedCommands.contains(command)) {
                    plugin.logDebug { "CommandExecuteEvent | Player ${player.username} executed an blocked command" }
                    sendBlockedMessage(player)
                    event.result=CommandExecuteEvent.CommandResult.denied()
                }
            } else {
                plugin.logDebug { "CommandExecuteEvent | Player ${player.username} is not in auth server" }
                sendBlockedMessage(player)
                event.result=CommandExecuteEvent.CommandResult.denied()
            }
            continuation.resume()
        }
    }

    private fun sendBlockedMessage(player: Player) {
        val blockedMessage = plugin.config().commands.blockedMessage
        if (!blockedMessage.isBlank()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(blockedMessage))
        }
    }
} 