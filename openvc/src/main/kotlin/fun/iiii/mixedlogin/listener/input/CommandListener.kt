package `fun`.iiii.mixedlogin.listener.input

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.command.CommandExecuteEvent
import com.velocitypowered.api.proxy.Player
import `fun`.iiii.mixedlogin.manager.AuthMeManager
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.listener.Listener
import `fun`.iiii.mixedlogin.utils.AuthMeUtils
import net.kyori.adventure.text.minimessage.MiniMessage

class CommandListener @Inject constructor(
    private val eventManager: EventManager,
    private val authMeManager: AuthMeManager,
    private val plugin: MixedLoginMain
) : Listener<CommandExecuteEvent> {

    override fun register() {
        eventManager.register(plugin, CommandExecuteEvent::class.java, PostOrder.FIRST, this)
    }

    override fun executeAsync(event: CommandExecuteEvent): EventTask {
        return EventTask.withContinuation { continuation ->
            val player = event.commandSource as? Player ?: run {
                plugin.logDebug { "CommandExecuteEvent | CommandSource is not a player" }
                continuation.resume()
                return@withContinuation
            }

            if (authMeManager.isLogged(player)) {
                plugin.logDebug { "CommandExecuteEvent | Player ${player.username} is already logged" }
                continuation.resume()
                return@withContinuation
            }

            if (authMeManager.isInAuthServer(player)) {
                plugin.logDebug { "CommandExecuteEvent | Player ${player.username} is in Auth Server" }
                val command = AuthMeUtils.getFirstArgument(event.command)
                if (!MixedLoginMain.getConfig().commands.allowedCommands.contains(command)) {
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
        val blockedMessage = MixedLoginMain.getConfig().commands.blockedMessage
        if (!blockedMessage.isBlank()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(blockedMessage))
        }
    }
} 