package `fun`.iiii.mixedlogin.utils

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import `fun`.iiii.mixedlogin.enums.SendMode
import java.util.*

object AuthMeUtils {
    fun getFirstArgument(command: String): String {
        return command.split(" ").getOrNull(0) ?: ""
    }

    val RANDOM: Random = Random()

    fun serverToSend(
        sendMode: SendMode, proxy: ProxyServer, servers: List<String?>, attempts: Int
    ): Pair<String?, RegisteredServer?> {
        return when (sendMode) {
            SendMode.TO_FIRST -> {
                var sv: Optional<RegisteredServer?>
                for (st in servers) {
                    sv = proxy.getServer(st)
                    if (sv.isPresent) Pair(st, sv.get())
                }
                Pair(null, null)
            }

            SendMode.TO_EMPTIEST_SERVER -> {
                var emptiest: RegisteredServer? = null
                var optional = Optional.empty<RegisteredServer>()
                for (st in servers) {
                    optional = proxy.getServer(st)
                    if (optional.isPresent) {
                        val actualsv = optional.get()
                        val actualConnected = actualsv.playersConnected.size
                        if (actualConnected == 0) {
                            Pair(st, actualsv)
                        }
                        if (emptiest == null || actualConnected < emptiest.playersConnected.size) {
                            emptiest = actualsv
                        }
                    }
                }
                Pair(optional.map { sv: RegisteredServer -> sv.serverInfo.name }.orElse(null), emptiest)
            }

            SendMode.RANDOM -> {
                var server: Optional<RegisteredServer?>
                if (servers.size == 1) {
                    server = proxy.getServer(servers[0])
                    Pair(
                        server.map { sv: RegisteredServer? -> sv!!.serverInfo.name }.orElse(null), server.orElse(null)
                    )
                }
                for (i in 0..<attempts) {
                    val value: Int = RANDOM.nextInt(servers.size)
                    server = proxy.getServer(servers[value])
                    if (server.isPresent) {
                        Pair(server.get().serverInfo.name, server.get())
                    }
                }
                Pair(null, null)
            }
        }
    }
} 