package `fun`.iiii.mixedlogin.listener.connection

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.connection.DisconnectEvent
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.AuthMeVelocityPlugin
import `fun`.iiii.mixedlogin.listener.Listener
import org.checkerframework.checker.nullness.qual.Nullable

class DisconnectListener @Inject constructor(
    private val plugin: AuthMeVelocityPlugin,
    private val eventManager: EventManager,
    private val mixedLoginMain: MixedLoginMain
) : Listener<DisconnectEvent> {

    override fun register() {
        eventManager.register(mixedLoginMain, DisconnectEvent::class.java, this)
    }

    override fun executeAsync(event: DisconnectEvent): @Nullable EventTask? {
        if (event.loginStatus == DisconnectEvent.LoginStatus.CONFLICTING_LOGIN) {
            return null
        }

        return EventTask.async { plugin.removePlayer(event.player) }
    }
} 