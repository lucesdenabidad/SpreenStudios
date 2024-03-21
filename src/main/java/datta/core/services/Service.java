package datta.core.services;

import co.aikar.commands.BaseCommand;
import datta.core.Core;
import org.bukkit.event.Listener;

public abstract class Service extends BaseCommand implements Listener {
    public abstract Core instance();

    public abstract String name();

    public abstract String[] info();

    public abstract void onLoad();

    public abstract void onUnload();

    public void register(boolean cmd, boolean event) {
        if (cmd) {
            instance().commandManager.registerCommand(this);
        }
        if (event){
            instance().getServer().getPluginManager().registerEvents(this, instance());
        }

    }
}