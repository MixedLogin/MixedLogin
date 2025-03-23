package `fun`.iiii.mixedlogin.listener.connection

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.connection.DisconnectEvent
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.manager.AuthMeManager
import `fun`.iiii.mixedlogin.listener.Listener
import org.checkerframework.checker.nullness.qual.Nullable

class DisconnectListener @Inject constructor(
    private val authMeManager: AuthMeManager,
    private val eventManager: EventManager,
    private val plugin: MixedLoginMain
) : Listener<DisconnectEvent> {

    override fun register() {
        eventManager.register(plugin, DisconnectEvent::class.java, this)
    }

    override fun executeAsync(event: DisconnectEvent): @Nullable EventTask? {
        if (event.loginStatus == DisconnectEvent.LoginStatus.CONFLICTING_LOGIN) {
            return null
        }

        return EventTask.async { authMeManager.removePlayer(event.player) }
    }
} 