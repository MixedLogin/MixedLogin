package `fun`.iiii.mixedlogin.listener.input

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.player.TabCompleteEvent
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.manager.AuthMeManager
import `fun`.iiii.mixedlogin.listener.Listener

class TabCompleteListener @Inject constructor(
    private val authMeManager: AuthMeManager,
    private val eventManager: EventManager,
    private val plugin: MixedLoginMain
) : Listener<TabCompleteEvent> {

    override fun register() {
        eventManager.register(plugin, TabCompleteEvent::class.java, PostOrder.FIRST, this)
    }

    override fun executeAsync(event: TabCompleteEvent): EventTask {
        return EventTask.async {
            if (authMeManager.isLogged(event.player)) {
                plugin.logDebug("TabCompleteEvent | Player ${event.player.username} is already logged")
                return@async
            }

            val command = event.partialMessage
            for (allowed in MixedLoginMain.getConfig().commands.allowedCommands) {
                if (allowed.startsWith(command)) {
                    return@async
                }
            }

            plugin.logDebug("TabCompleteEvent | ${event.player.username} | Not allowed tab-completion")
            event.suggestions.clear()
        }
    }
}