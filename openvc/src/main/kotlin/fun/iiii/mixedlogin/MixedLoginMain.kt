package `fun`.iiii.mixedlogin

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import `fun`.iiii.mixedlogin.command.MixedLoginCommand
import `fun`.iiii.mixedlogin.listener.EventListener
import java.io.InputStream
import java.nio.file.Path
import java.util.logging.Logger

class MixedLoginMain @Inject constructor(
    private val server: ProxyServer,
    val logger: Logger,
    @DataDirectory private val dataDirectory: Path
) {
    val loginServerManager = LoginServerManager()

    companion object {
        private lateinit var instance: MixedLoginMain

        @JvmStatic
        fun getInstance(): MixedLoginMain = instance
    }

    init {
        instance = this
    }

    @Subscribe
    fun onEnable(event: ProxyInitializeEvent) {
        proxy.commandManager.register("mixedlogin", MixedLoginCommand())
        proxy.eventManager.register(this, EventListener())
        loginServerManager.start()
    }

    private fun getResourceAsStream(file: String): InputStream? {
        return javaClass.classLoader.getResourceAsStream(file)
    }

    val proxy: ProxyServer
        get() = server
} 