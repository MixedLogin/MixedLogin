package `fun`.iiii.mixedlogin.yggdrasil

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.*
import io.netty.util.CharsetUtil

class HttpRequestHandler(
    private val processor: YggdrasilResultProcessor
) : SimpleChannelInboundHandler<FullHttpRequest>() {

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, req: FullHttpRequest) {
        // 100 Continue
        if (HttpUtil.is100ContinueExpected(req)) {
            ctx.write(DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.CONTINUE
            ))
        }

        // 获取请求的uri
        val uri = req.uri()
        val firstSplitURL = uri.split("?")
        if (firstSplitURL.size < 2) {
            return
        }

        if (firstSplitURL[0] != "/api/yggdrasil/sessionserver/session/minecraft/hasJoined") {
            val response = DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND,
                Unpooled.copiedBuffer("404 Not Found", CharsetUtil.UTF_8)
            )
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
            return
        }

        val resMap = mutableMapOf<String, String>()
        resMap["url"] = firstSplitURL[0]
        
        val secondSplitURL = firstSplitURL[1].split("&")
        for (s in secondSplitURL) {
            val thirdSplitURL = s.split("=")
            resMap[thirdSplitURL[0]] = thirdSplitURL[1]
        }

        resMap["method"] = req.method().name()

        val userName = resMap["username"] ?: ""
        val serverId = resMap["serverId"] ?: ""
        val ip = resMap["ip"] ?: ""
        
        val requestObject = YggdrasilRequestObject(userName, serverId, ip, ctx)
        processor.onReceive(requestObject)
    }
} 