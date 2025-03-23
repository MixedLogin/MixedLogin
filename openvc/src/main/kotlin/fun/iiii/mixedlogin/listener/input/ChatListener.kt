package `fun`.iiii.mixedlogin.listener.input

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.player.PlayerChatEvent
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.AuthMeVelocityPlugin
import `fun`.iiii.mixedlogin.listener.Listener

class ChatListener @Inject constructor(
    private val plugin: AuthMeVelocityPlugin,
    private val eventManager: EventManager,
    private val mixedLoginMain: MixedLoginMain
) : Listener<PlayerChatEvent> {

    override fun register() {
        eventManager.register(mixedLoginMain, PlayerChatEvent::class.java, PostOrder.FIRST, this)
    }

    override fun executeAsync(event: PlayerChatEvent): EventTask {
        return EventTask.withContinuation { continuation ->
            if (plugin.isLogged(event.player)) {
                plugin.logDebug { "PlayerChatEvent | Player ${event.player.username} is already logged" }
                continuation.resume()
                return@withContinuation
            }

            plugin.logDebug { "PlayerChatEvent | Player ${event.player.username} is not logged" }


            event.result = PlayerChatEvent.ChatResult.denied()
            continuation.resume()
        }
    }
} 