package `fun`.iiii.mixedlogin.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class MixedLoginConfig {
    @Comment("下层服务器yggd服务器配置,非必要勿动")
    val subYggdrasil = SubYggdrasil()

    @Comment("离线玩家接入yggd服务器配置,非必要勿动")
    val offlineYggdrasil = OfflineYggdrasil()

    @Comment("UUID匹配设定")
    val uuidMatch = UUIDMatch()

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

}