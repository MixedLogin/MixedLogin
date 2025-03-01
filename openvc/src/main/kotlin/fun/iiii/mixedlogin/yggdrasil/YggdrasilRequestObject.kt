package `fun`.iiii.mixedlogin.yggdrasil

import io.netty.channel.ChannelHandlerContext

data class YggdrasilRequestObject(
    val userName: String,
    val serverId: String,
    val ip: String,
    val ctx: ChannelHandlerContext
) 