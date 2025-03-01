package `fun`.iiii.mixedlogin.command

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.SimpleCommand
import net.kyori.adventure.text.TextComponent

class MixedLoginCommand : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        val args = invocation.arguments()
        val sender = invocation.source()
        if (args.size == 0) {
            sender.sendPlainMessage("§e/mixedlogin reload")
            return
        }
        if (args[0].equals("reload", ignoreCase = true)) {
            sender.sendPlainMessage("§aReloaded!")
            return
        }else if (args[0].equals("uuid", ignoreCase = true)) {
            sender.sendPlainMessage("§aTest!")
            return
        }
    }

    override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        return invocation.source().hasPermission("mixedlogin.admin")
    }
} 