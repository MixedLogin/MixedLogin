package `fun`.iiii.mixedlogin.yggdrasil

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import java.net.InetSocketAddress

class VirtualYggdrasilServer(
    private val port: Int,
    private val ip: String,
    private val processor: YggdrasilResultProcessor
) {
    private lateinit var f: ChannelFuture

    fun start() {
        val bootstrap = ServerBootstrap()
        val boss = NioEventLoopGroup()
        val work = NioEventLoopGroup()
        bootstrap.group(boss, work)
            .handler(LoggingHandler(LogLevel.DEBUG))
            .channel(NioServerSocketChannel::class.java)
            .childHandler(HttpServerInitializer(processor))

        f = bootstrap.bind(InetSocketAddress(ip, port)).sync()
        println("yggdrasil server start up on : $ip:$port")
    }

    fun getChannelFuture(): ChannelFuture = f
} 