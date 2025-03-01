package fun.iiii.mixedlogin.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class MixedLoginCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource sender = invocation.source();

    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("mixedlogin.admin");
    }
}
