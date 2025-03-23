package `fun`.iiii.mixedlogin.config

import `fun`.iiii.mixedlogin.enums.SendMode
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import kotlin.jvm.JvmField

@ConfigSerializable
class MixedLoginConfig {

    @Comment("下层服务器yggd服务器配置,非必要勿动")
    val subYggdrasil = SubYggdrasil()

    @Comment("离线玩家接入yggd服务器配置,非必要勿动")
    val offlineYggdrasil = OfflineYggdrasil()

    @Comment("UUID匹配设定")
    val uuidMatch = UUIDMatch()

    @Comment("Host匹配设定")
    val hostMatch = HostMatch()

    @ConfigSerializable
    class SubYggdrasil {
        val port = 26749
        val host = "127.0.0.1"
    }

    @ConfigSerializable
    class OfflineYggdrasil {
        val port = 26748
        val host = "127.0.0.1"
    }

    @ConfigSerializable
    class UUIDMatch {
        @ConfigSerializable
        class PCL2 {
            @Comment("PCL2的UUID匹配")
            val enable = true

            @Comment("PCL2的UUID进行哈希计算匹配")
            val hash = true

            @Comment("PCL2的苗条模型UUID匹配")
            val slim = true
        }

        @Comment("是否允许全0的UUID(Zalith) 匹配为离线")
        val zero = true

        @Comment("是否允许默认uuid生成方法 匹配为离线")
        val offline = true

        @Comment("关于PCL2启动器匹配的细节设定")
        val pcl2 = PCL2()
    }

    @ConfigSerializable
    class HostMatch {
        val start = listOf("offline", "o-")
    }

    @Comment("登入服务器")
    @JvmField
    val authServers = listOf("login")

    @Comment("登入服务器设定")
    @JvmField
    val sendOnLogin = SendOnLogin()

    @Comment("未登入服务器设定")
    @JvmField
    val sendNoLogin = SendNoLogin()

    @Comment("登入中命令设定")
    @JvmField
    val commands = Commands()


    @Comment("高级设定")
    @JvmField
    val advanced = Advanced()

    @ConfigSerializable
    class SendNoLogin {
        @Comment("确保未登入的玩家玩家连接登入服务器")
        @JvmField
        val enable = true

        @Comment(
            """
            玩家初始服务器选择模式
            TO_FIRST | 发送到第一个配置的服务器
            TO_EMPTIEST_SERVER | 发送到玩家最少的服务器
            RANDOM | 发送到随机服务器
        """
        )
        @JvmField
        val sendMode = SendMode.RANDOM
    }

    @ConfigSerializable
    class SendOnLogin {
        @Comment("登入后的玩家进行服务器加入控制")
        @JvmField
        val enable = true

        @Comment("需要 authmevelocity.send-on-login 权限")
        @JvmField
        val requirePermission = false

        @Comment("登入的玩家会被送到的服务器")
        @JvmField
        val servers = listOf("vc")

        @Comment(
            """
            玩家初始服务器选择模式
            TO_FIRST | 发送到第一个配置的服务器
            TO_EMPTIEST_SERVER | 发送到玩家最少的服务器
            RANDOM | 发送到随机服务器
        """
        )
        @JvmField
        val sendMode = SendMode.RANDOM
    }

    @ConfigSerializable
    class Commands {
        @Comment("设定未登入可执行的命令")
        @JvmField
        val allowedCommands = listOf("login", "register", "l", "reg", "email", "captcha")

        @Comment("玩家未登入执行命令的提示")
        @JvmField
        val blockedMessage = "<red>登入才能执行命令！"
    }

    @ConfigSerializable
    class Advanced {
        @Comment("开启debug模式")
        @JvmField
        val debug = true

        @Comment("随机传送尝试次数")
        @JvmField
        val randomAttempts = 5

        @Comment("跳过皮肤站/正版玩家的登入")
        @JvmField
        val skipOnlineLogin = true
    }
}
