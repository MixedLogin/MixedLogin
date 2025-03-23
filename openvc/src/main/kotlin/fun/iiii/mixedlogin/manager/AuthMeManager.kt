package `fun`.iiii.mixedlogin.manager

import com.google.inject.Binder
import com.google.inject.Injector
import com.google.inject.Module
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.ChannelIdentifier
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import com.velocitypowered.api.proxy.server.RegisteredServer
import `fun`.iiii.mixedlogin.MixedLoginMain
import `fun`.iiii.mixedlogin.listener.Listener
import `fun`.iiii.mixedlogin.listener.connection.DisconnectListener
import `fun`.iiii.mixedlogin.listener.connection.InitialServerListener
import `fun`.iiii.mixedlogin.listener.connection.PostConnectListener
import `fun`.iiii.mixedlogin.listener.connection.PreConnectListener
import `fun`.iiii.mixedlogin.listener.data.PluginMessageListener
import `fun`.iiii.mixedlogin.listener.input.ChatListener
import `fun`.iiii.mixedlogin.listener.input.CommandListener
import `fun`.iiii.mixedlogin.listener.input.TabCompleteListener
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream

class AuthMeManager(val plugin: MixedLoginMain) {
    companion object {
        val MODERN_CHANNEL: ChannelIdentifier = MinecraftChannelIdentifier.create("authmevelocity", "main")
        val LEGACY_CHANNEL: ChannelIdentifier = LegacyChannelIdentifier("authmevelocity:main")
    }

    private val authServers: MutableSet<String> = ConcurrentHashMap.newKeySet()
    private val loggedPlayers: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    fun onProxyInitialization(injector: Injector, proxy: ProxyServer) {
        authServers.addAll(MixedLoginMain.getConfig().authServers)
        proxy.channelRegistrar.register(MODERN_CHANNEL, LEGACY_CHANNEL)

        val childInjector = injector.createChildInjector(object : Module {
            override fun configure(binder: Binder) {
                binder.bind(AuthMeManager::class.java).toInstance(this@AuthMeManager)
            }
        })

        Stream.of(
            PluginMessageListener::class.java,
            DisconnectListener::class.java,
            InitialServerListener::class.java,
            PostConnectListener::class.java,
            PreConnectListener::class.java,
            ChatListener::class.java,
            CommandListener::class.java,
            TabCompleteListener::class.java
        ).map { clazz: Class<out Listener<*>> -> childInjector.getInstance(clazz) }
            .forEach(Listener<*>::register)

        plugin.logInfo("未登入服务器: $authServers")
        if (MixedLoginMain.getConfig().sendOnLogin.enable)
            plugin.logInfo("登入服务器: ${MixedLoginMain.getConfig().sendOnLogin.servers}")
    }

    fun isLogged(player: Player): Boolean = loggedPlayers.contains(player.uniqueId)

    fun addPlayer(player: Player): Boolean = loggedPlayers.add(player.uniqueId)

    fun removePlayer(player: Player): Boolean = loggedPlayers.remove(player.uniqueId)

    fun isInAuthServer(player: Player): Boolean =
        player.currentServer.map { isAuthServer(it) }.orElse(false)

    fun isAuthServer(server: RegisteredServer): Boolean =
        isAuthServer(server.serverInfo.name)

    fun isAuthServer(connection: ServerConnection): Boolean =
        isAuthServer(connection.serverInfo.name)

    fun isAuthServer(server: String): Boolean = authServers.contains(server)

}