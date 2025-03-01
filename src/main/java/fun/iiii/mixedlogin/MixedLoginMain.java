package fun.iiii.mixedlogin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fun.iiii.mixedlogin.command.MixedLoginCommand;
import fun.iiii.mixedlogin.listener.EventListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "mixedlogin", name = "MixedLogin", version = "0.0.1", authors = {"ksqeib"})
public class MixedLoginMain {
    public static MixedLoginMain getInstance() {
        return Instance;
    }

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private final LoginServerManager loginServerManager=new LoginServerManager();

    private static MixedLoginMain Instance;

    @Inject
    public MixedLoginMain(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        Instance = this;
    }

    @Subscribe
    public void onEnable(final ProxyInitializeEvent event) {
        getProxy().getCommandManager().register("mixedlogin", new MixedLoginCommand());
        getProxy().getEventManager().register(this, new EventListener());
        loginServerManager.start();
    }

    private InputStream getResourceAsStream(String file) {
        return getClass().getClassLoader().getResourceAsStream(file);
    }

    public LoginServerManager getLoginServerManager() {
        return loginServerManager;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public ProxyServer getServer() {
        return server;
    }


    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getProxy() {
        return server;
    }

}
