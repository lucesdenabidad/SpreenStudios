package datta.core.paper;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import datta.core.paper.animations.ExpulseAnimation;
import datta.core.paper.animations.KillAnimation;
import datta.core.paper.animations.am.AnimationManager;
import datta.core.paper.commands.GlobalCMD;
import datta.core.paper.events.MessagesEvent;
import datta.core.paper.items.list.KickStick;
import datta.core.paper.items.list.KillStick;
import datta.core.paper.service.ResetService;
import datta.core.paper.service.SlotService;
import datta.core.paper.task.BroadcastSenderTask;
import datta.core.paper.utilities.builders.MenuBuilder;
import datta.core.paper.utilities.etc.configuration.Configuration;
import datta.core.paper.utilities.etc.configuration.ConfigurationManager;
import datta.core.paper.utilities.score.ScoreHolder;
import datta.core.paper.utilities.services.TimerService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class Core extends JavaPlugin {

    private static @Getter Core instance;
    private static @Getter PaperCommandManager commandManager;

    public static MenuBuilder menuBuilder;

    public ConfigurationManager configurationManager;
    @Getter
    public Configuration config;

    public static Location spawn;

    @Override
    public void onEnable() {
        instance = this;
        menuBuilder = new MenuBuilder(this);
        menuBuilder = new MenuBuilder(this);
        commandManager = new PaperCommandManager(this);
        configurationManager = new ConfigurationManager(this);
        config = configurationManager.getConfig("configuration.yml");
        spawn = Bukkit.getWorlds().get(0).getSpawnLocation().toCenterLocation();

        // HOLY
        BroadcastSenderTask.start();

        //SCORE
        ScoreHolder scoreHolder = new ScoreHolder(this,
                "&a&lCORE",
                "",
                "&fPlugin template",
                "&fby &7datta",
                "",
                "&ewww.holy.gg");
        scoreHolder.start(0, 20L);

        //LISTENERS
        register(new MessagesEvent());

        // SERVICES
        AnimationManager animationManager = new AnimationManager(commandManager);
        animationManager.register(new KillAnimation());
        animationManager.register(new ExpulseAnimation());

        registerBoth(new SlotService());

        ResetService resetService = new ResetService();
        resetService.hook();

        //COMMANDS
        commandManager.registerCommand(new GlobalCMD());

        // ITEMS
        registerBoth(new KickStick());
        registerBoth(new KillStick());

    }

    @Override
    public void onDisable() {
        TimerService.removeBar();
    }

    public static void registerBoth(Object object) {
        register(object);
    }

    public static void register(Object object) {
        if (object instanceof BaseCommand) {
            commandManager.registerCommand((BaseCommand) object);
        }
        if (object instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) object, Core.getInstance());
        }
    }
}