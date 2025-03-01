package `fun`.iiii.mixedlogin.yggdrasil

import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec

class HttpServerInitializer(
    private val processor: YggdrasilResultProcessor
) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(channel: SocketChannel) {
        val pipeline: ChannelPipeline = channel.pipeline()
        pipeline.addLast(HttpServerCodec()) // http 编解码
        pipeline.addLast("httpAggregator", HttpObjectAggregator(512 * 1024)) // http 消息聚合器, 512*1024为接收的最大contentlength
        pipeline.addLast(HttpRequestHandler(processor)) // 请求处理器
    }
} 