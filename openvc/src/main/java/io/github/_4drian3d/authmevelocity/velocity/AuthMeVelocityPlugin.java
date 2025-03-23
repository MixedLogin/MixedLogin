/*
 * Copyright (C) 2025 AuthMeVelocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github._4drian3d.authmevelocity.velocity;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import com.google.inject.Injector;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fun.iiii.mixedlogin.config.MixedLoginConfig;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;
import io.github._4drian3d.authmevelocity.velocity.listener.connection.DisconnectListener;
import io.github._4drian3d.authmevelocity.velocity.listener.connection.InitialServerListener;
import io.github._4drian3d.authmevelocity.velocity.listener.connection.PostConnectListener;
import io.github._4drian3d.authmevelocity.velocity.listener.connection.PreConnectListener;
import io.github._4drian3d.authmevelocity.velocity.listener.data.PluginMessageListener;
import io.github._4drian3d.authmevelocity.velocity.listener.input.ChatListener;
import io.github._4drian3d.authmevelocity.velocity.listener.input.CommandListener;
import io.github._4drian3d.authmevelocity.velocity.listener.input.TabCompleteListener;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

public final class AuthMeVelocityPlugin{
  public static final ChannelIdentifier MODERN_CHANNEL
      = MinecraftChannelIdentifier.create("authmevelocity", "main");
  public static final ChannelIdentifier LEGACY_CHANNEL
      = new LegacyChannelIdentifier("authmevelocity:main");

  final Set<String> authServers = ConcurrentHashMap.newKeySet();
  final Set<UUID> loggedPlayers = ConcurrentHashMap.newKeySet();

  private final ProxyServer proxy;
  private final ComponentLogger logger;
  private final Injector injector;

  public AuthMeVelocityPlugin(ProxyServer proxy, ComponentLogger logger, Injector injector) {
    this.proxy = proxy;
    this.logger = logger;
    this.injector = injector;
  }
  
  private MixedLoginConfig config;

  public void onProxyInitialization(MixedLoginConfig config) {
    try {
      authServers.addAll(config.authServers);
    } catch (Exception e) {
      logger.error("Could not load config.conf file", e);
      return;
    }

    logDebug("Loaded plugin libraries");

    proxy.getChannelRegistrar().register(MODERN_CHANNEL, LEGACY_CHANNEL);

    Injector childInjector=injector.createChildInjector(binder -> {
      binder.bind(AuthMeVelocityPlugin.class).toInstance(this);
    });

    Stream.of(
            PluginMessageListener.class,
            DisconnectListener.class,
            InitialServerListener.class,
            PostConnectListener.class,
            PreConnectListener.class,
            ChatListener.class,
            CommandListener.class,
            TabCompleteListener.class
        ).map(childInjector::getInstance)
        .forEach(Listener::register);

    this.sendInfoMessage();
  }

  public void sendInfoMessage() {
    logger.info(miniMessage().deserialize("<gray>AuthServers: <green>" + config.authServers));
    if (config.sendOnLogin.sendOnLogin) {
      logger.info(miniMessage().deserialize(
          "<gray>LobbyServers: <green>" + config.sendOnLogin.teleportServers));
    }
  }

  public void setAuthServers(List<String> servers) {
    authServers.clear();
    authServers.addAll(servers);
  }

  public MixedLoginConfig config() {
    return this.config;
  }

  public boolean isLogged(@NotNull Player player) {
    return loggedPlayers.contains(player.getUniqueId());
  }

  public boolean addPlayer(@NotNull Player player) {
    return loggedPlayers.add(player.getUniqueId());
  }

  public boolean removePlayer(@NotNull Player player) {
    return loggedPlayers.remove(player.getUniqueId());
  }

  public boolean isInAuthServer(@NotNull Player player) {
    return player.getCurrentServer().map(this::isAuthServer).orElse(false);
  }

  public boolean isAuthServer(@NotNull RegisteredServer server) {
    return isAuthServer(server.getServerInfo().getName());
  }

  public boolean isAuthServer(@NotNull ServerConnection connection) {
    return isAuthServer(connection.getServerInfo().getName());
  }

  public boolean isAuthServer(@NotNull String server) {
    return authServers.contains(server);
  }


  public void logDebug(final String msg) {
    if (config.advanced.debug) {
      logger.info("[DEBUG] {}", msg);
    }
  }

  public void logDebug(final Supplier<String> msg) {
    if (config.advanced.debug) {
      logger.info("[DEBUG] {}", msg.get());
    }
  }
}
