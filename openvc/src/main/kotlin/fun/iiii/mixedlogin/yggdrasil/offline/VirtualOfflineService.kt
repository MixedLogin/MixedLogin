package `fun`.iiii.mixedlogin.yggdrasil.offline

import `fun`.iiii.mixedlogin.yggdrasil.YggdrasilRequestObject
import `fun`.iiii.mixedlogin.yggdrasil.YggdrasilResultProcessor
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.handler.codec.http.*
import io.netty.util.CharsetUtil
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class VirtualOfflineService : YggdrasilResultProcessor {
    private val serverIdReqMap = ConcurrentHashMap<String, String>()

    override fun onReceive(requestObject: YggdrasilRequestObject) {
        val userName = requestObject.userName
        val serverId = requestObject.serverId
        val ctx = requestObject.ctx

        val genServerId = finishRequest(userName)
        if (genServerId == null || genServerId != serverId) {
            val response = DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.FORBIDDEN,
                Unpooled.copiedBuffer("未进行认证", CharsetUtil.UTF_8)
            )
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
            return
        }

        // 创建http响应
        val json = "{id:${generateOfflinePlayerUuid(userName)},name:$userName}"
        val response = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            Unpooled.copiedBuffer(json, CharsetUtil.UTF_8)
        )
        
        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    }

    companion object {
        fun generateOfflinePlayerUuid(username: String): UUID {
            return UUID.nameUUIDFromBytes(("OfflinePlayer:$username").toByteArray(StandardCharsets.UTF_8))
        }

        fun generateServerId(userName: String): String {
            return try {
                val digest = MessageDigest.getInstance("SHA-1")
                digest.update(userName.toByteArray())
                digest.update(UUID.randomUUID().toString().toByteArray())
                twosComplementHexdigest(digest.digest())
            } catch (e: Exception) {
                throw AssertionError(e)
            }
        }

        fun twosComplementHexdigest(digest: ByteArray): String {
            return BigInteger(digest).toString(16)
        }
    }

    fun finishRequest(userName: String): String? = serverIdReqMap.remove(userName)

    fun startRequest(userName: String): String {
        val gen = generateServerId(userName)
        serverIdReqMap[userName] = gen
        return gen
    }
} 