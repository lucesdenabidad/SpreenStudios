package datta.core;

import datta.core.services.CommandService;
import datta.core.services.list.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class CoreMethods {

    public static void loadServices(CommandService commandService) {
        commandService.registerService(new ScreenColorService());
        commandService.registerService(new WhitelistService());
        commandService.registerService(new TimerService());
        commandService.registerService(new StreamerService());
        commandService.registerService(new CinemaService());
        commandService.registerService(new ToggleService());
        commandService.registerService(new LightService());
        commandService.registerService(new SitService());
        commandService.registerService(new ScoreboardService(Core.getInstance().defaultTitle, Core.getInstance().defaultLines));
        commandService.registerService(new WarpService());
    }

    public static void listener(Listener listener){
        Bukkit.getServer().getPluginManager().registerEvents(listener, Core.getInstance());
    }
}
