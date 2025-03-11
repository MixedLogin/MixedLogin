/*
 * Copyright (C) 2024 AuthMeVelocity Contributors
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

package io.github._4drian3d.authmevelocity.common.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import io.github._4drian3d.authmevelocity.common.enums.SendMode;

import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class ProxyConfiguration {
    @Comment("登入服务器")
    private List<String> authServers = List.of("login");
    public List<String> authServers() {
        return this.authServers;
    }
    
    private SendOnLogin sendOnLogin = new SendOnLogin();
    public SendOnLogin sendOnLogin() {
        return this.sendOnLogin;
    }

    private Commands commands = new Commands();
    public Commands commands() {
        return this.commands;
    }

    private EnsureAuthServer ensureAuthServer = new EnsureAuthServer();
    public EnsureAuthServer ensureAuthServer() {
        return this.ensureAuthServer;
    }

    private Advanced advanced = new Advanced();
    public Advanced advanced() {
        return this.advanced;
    }

    @ConfigSerializable
    public static class EnsureAuthServer {
        @Comment("确保玩家连接登入服务器")
        private boolean ensureAuthServer = true;
        public boolean ensureFirstServerIsAuthServer() {
            return this.ensureAuthServer;
        }

        @Comment("""
            玩家初始服务器选择模式
            TO_FIRST | 发送到第一个配置的服务器
            TO_EMPTIEST_SERVER | 发送到玩家最少的服务器
            RANDOM | 发送到随机服务器""")
        private SendMode sendMode = SendMode.RANDOM;
        public SendMode sendMode() {
            return this.sendMode;
        }
    }

    @ConfigSerializable
    public static class SendOnLogin {
        @Comment("发送登入的玩家到其他服务器")
        private boolean sendOnLogin = true;
        public boolean sendToServerOnLogin() {
            return this.sendOnLogin;
        }

        @Comment("需要 authmevelocity.send-on-login 权限")
        private boolean requirePermission = false;
        public boolean isRequirePermission() {
            return this.requirePermission;
        }

        @Comment("""
            登入的玩家会被送到的服务器""")
        private List<String> teleportServers = List.of("vc");
        public List<String> teleportServers() {
            return this.teleportServers;
        }

        @Comment("""
            玩家初始服务器选择模式
            TO_FIRST | 发送到第一个配置的服务器
            TO_EMPTIEST_SERVER | 发送到玩家最少的服务器
            RANDOM | 发送到随机服务器""")
        private SendMode sendMode = SendMode.RANDOM;
        public SendMode sendMode() {
            return this.sendMode;
        }
    }

    @ConfigSerializable
    public static class Commands {
        @Comment("设定未登入可执行的命令")
        private List<String> allowedCommands = List.of("login", "register", "l", "reg", "email", "captcha");
        public List<String> allowedCommands() {
            return this.allowedCommands;
        }

        @Comment("""
            玩家未登入执行命令的提示""")
        private String blockedMessage = "<red>登入才能执行命令！";
        public String blockedCommandMessage() {
            return this.blockedMessage;
        }
    }

    @ConfigSerializable
    public static class Advanced {
        @Comment("开启debug模式")
        private boolean debug = true;
        public boolean debug() {
            return this.debug;
        }
    
        @Comment("随机传送尝试次数")
        private int randomAttempts = 5;
        public int randomAttempts() {
            return this.randomAttempts;
        }

        @Comment("跳过皮肤站/正版玩家的登入")
        private boolean skinOnlineLogin = true;
        public boolean skinOnlineLogin() {
            return this.skinOnlineLogin;
        }
    }
}
