package `fun`.iiii.mixedlogin.yggdrasil.offline

import com.velocitypowered.api.util.GameProfile
import `fun`.iiii.mixedlogin.yggdrasil.YggdrasilRequestObject
import `fun`.iiii.mixedlogin.yggdrasil.YggdrasilResultProcessor
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.handler.codec.http.*
import io.netty.util.CharsetUtil
import java.util.concurrent.ConcurrentHashMap
import com.velocitypowered.proxy.VelocityServer.GENERAL_GSON

class VirtualSubService : YggdrasilResultProcessor {
    private val serverIdReqMap = ConcurrentHashMap<String, GameProfile>()

    override fun onReceive(requestObject: YggdrasilRequestObject) {
        val serverId = requestObject.serverId
        val ctx = requestObject.ctx

        val gameProfile = finishRequest(serverId)
        if (gameProfile == null) {
            val response = DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.FORBIDDEN,
                Unpooled.copiedBuffer("无效验证", CharsetUtil.UTF_8)
            )
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
            return
        }

        val json = GENERAL_GSON.toJson(gameProfile)
        val response = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            Unpooled.copiedBuffer(json, CharsetUtil.UTF_8)
        )
        
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    }

    fun finishRequest(serverId: String): GameProfile? = serverIdReqMap.remove(serverId)

    fun startRequest(serverId: String, gameProfile: GameProfile) {
        serverIdReqMap[serverId] = gameProfile
    }
} 