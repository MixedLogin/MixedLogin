package `fun`.iiii.mixedlogin.listener

import com.velocitypowered.api.event.AwaitingEventExecutor

interface Listener<E> : AwaitingEventExecutor<E> {
    fun register()
} 