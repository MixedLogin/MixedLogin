package `fun`.iiii.mixedlogin

import com.google.inject.Inject
import com.google.inject.Injector
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import `fun`.iiii.mixedlogin.command.MixedLoginCommand
import `fun`.iiii.mixedlogin.config.MixedLoginConfig
import `fun`.iiii.mixedlogin.listener.EventListener
import `fun`.iiii.mixedlogin.manager.AuthMeManager
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.kotlin.dataClassFieldDiscoverer
import org.spongepowered.configurate.objectmapping.ObjectMapper
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Supplier

class MixedLoginMain @Inject constructor(
    private val server: ProxyServer,
    val logger: ComponentLogger,
    @DataDirectory private val dataDirectory: Path,
    private val injector: Injector
) {
    lateinit var loginServerManager: LoginServerManager

    companion object {
        private lateinit var instance: MixedLoginMain
        private lateinit var mixedLoginConfig: MixedLoginConfig
        private lateinit var authMeManager: AuthMeManager

        @JvmStatic
        fun getInstance(): MixedLoginMain = instance

        @JvmStatic
        fun getConfig(): MixedLoginConfig = mixedLoginConfig
    }

    init {
        instance = this
    }

    @Subscribe
    fun onEnable(event: ProxyInitializeEvent) {
        loadConfig()
        loginServerManager = LoginServerManager()

        proxy.commandManager.register("mixedlogin", MixedLoginCommand())
        proxy.eventManager.register(this, EventListener())
        loginServerManager.start()

        authMeManager = AuthMeManager(this)
        authMeManager.onProxyInitialization(injector, server)

    }

    val proxy: ProxyServer
        get() = server

    private fun loadConfig() {
        val path = dataDirectory.resolve("config.conf")
        val firstCreation = Files.notExists(path)
        val loader = HoconConfigurationLoader.builder()
            .defaultOptions { opts: ConfigurationOptions ->
                opts
                    .shouldCopyDefaults(true)
                    .header(
                        """
                            MixedLogin | by ksqeib
                            
                        """.trimIndent()
                    ).serializers { s ->
                        s.registerAnnotatedObjects(
                            ObjectMapper.factoryBuilder().addDiscoverer(dataClassFieldDiscoverer()).build()
                        )
                    }
            }
            .path(path)
            .build()
        val node = loader.load()
        val config = node.get(MixedLoginConfig::class.java)
        if (firstCreation) {
            node.set(config)
            loader.save(node)
        }
        if (config != null) {
            mixedLoginConfig = config
        }
    }


    fun logInfo(msg: String) {
        logger.info(msg)
    }

    fun logDebug(msg: String) {
        if (mixedLoginConfig.advanced.debug) {
            logger.info("[DEBUG] $msg")
        }
    }

    fun logDebug(msg: Supplier<String>) {
        if (mixedLoginConfig.advanced.debug) {
            logger.info("[DEBUG] ${msg.get()}")
        }
    }

} 