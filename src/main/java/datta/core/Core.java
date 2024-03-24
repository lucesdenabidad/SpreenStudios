package datta.core;

import co.aikar.commands.Locales;
import co.aikar.commands.PaperCommandManager;
import datta.core.commands.*;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.configuration.Configuration;
import datta.core.content.configuration.ConfigurationManager;
import datta.core.content.portals.PortalManager;
import datta.core.content.weapons.GameItem;
import datta.core.content.weapons.Stick;
import datta.core.content.weapons.Weapon;
import datta.core.content.weapons.sticks.KickStick;
import datta.core.content.weapons.sticks.KillStick;
import datta.core.content.weapons.sticks.PunchStick;
import datta.core.events.PlayerChangeGamemodeListener;
import datta.core.events.PlayerDeathListener;
import datta.core.events.PlayerJoinListener;
import datta.core.events.PlayerQuitListener;
import datta.core.games.CommandGame;
import datta.core.games.Game;
import datta.core.games.GamesCommand;
import datta.core.games.games.*;
import datta.core.menus.GameMenu;
import datta.core.services.CommandService;
import datta.core.commands.CallCMD;
import datta.core.services.individual.FreezeList;
import datta.core.services.individual.Glow;
import datta.core.services.list.ToggleService;
import datta.core.utils.SenderUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static datta.core.CoreMethods.listener;


public class Core extends JavaPlugin {

    private static @Getter Core instance;
    public @Getter PaperCommandManager commandManager;
    public static MenuBuilder menuBuilder;
    public ConfigurationManager configurationManager;
    @Getter
    public Configuration config;

    public CommandService commandService;
    public String prefix;
    public CoreParse coreParse;
    public CommandGame commandGame;
    public static List<Player> disableLog = new ArrayList<>();
    public PortalManager portalManager;

    public String defaultTitle = "&5&lEventos";
    public List<String> defaultLines = new ArrayList<>();


    @Override
    public void onEnable() {
        instance = this;
        menuBuilder = new MenuBuilder(this);
        commandManager = new PaperCommandManager(this);
        commandService = new CommandService(this);
        configurationManager = new ConfigurationManager(this);
        config = configurationManager.getConfig("configuration.yml");

        CoreMethods.loadServices(commandService);
        commandService.loadServices();

        commandGame = new CommandGame(this);
        prefix = "&5&lEvento &8»";
        coreParse = new CoreParse();

        portalManager = new PortalManager(this);

        commandGame.registerGame(new ElSueloEsLavaGame(), true, true);
        commandGame.registerGame(new OleadasDeMobsGame(), true, true);
        commandGame.registerGame(new PuertasGame(), true, true);
        commandGame.registerGame(new ReyDeLaColinaGame(),true,true);
        commandGame.registerGame(new SillitasGame(), true, true);

        commandManager.registerCommand(new DevCMD());
        commandManager.registerCommand(new GameMenu());
        commandManager.registerCommand(new InfoCMD());
        commandManager.registerCommand(new WorldCMD());
        commandManager.registerCommand(new SpeedCMD());
        commandManager.registerCommand(new CopyBlockCMD());
        commandManager.registerCommand(new GamesCommand());
        commandManager.registerCommand(new ColorCMD());
        commandManager.registerCommand(new TpallCMD());
        commandManager.registerCommand(new CallCMD());
        commandManager.registerCommand(new EliminateCMD());

        Stick.registerStick(new PunchStick());
        Stick.registerStick(new KickStick());
        Stick.registerStick(new KillStick());
        Weapon.register(
                new GameItem());

        listener(new PlayerQuitListener());
        listener(new PlayerDeathListener());
        listener(new PlayerJoinListener());
        listener(new PlayerChangeGamemodeListener());

        listener(new FreezeList());
        Glow glow = new Glow();

        commandManager.getLocales().addMessageBundle("core", Locales.SPANISH);
        commandManager.getLocales().setDefaultLocale(Locales.SPANISH);

        coreParse.register();
        stopGames();
    }

    @Override
    public void onDisable() {
        commandService.unloadServices();
        stopGames();
        coreParse.unregister();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public static void info(String... s) {
        for (String e : s) {
            SenderUtil.sendMessage(Bukkit.getServer().getConsoleSender(), "&7[SpreenStudios] [Log] &r"+e);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.isOp() && disableLog.contains(onlinePlayer)) {
                    SenderUtil.sendMessage(onlinePlayer, "&8[Log] &r"+e);
                }
            }
        }
    }


    public static void callSetting(ToggleService.Toggleable toggleable, boolean value){
        toggleable.set(value);
        toggleable.save();
        info(toggleable.name() + " se cambio a &e"+value+"&f.");
    }

    public void stopGames() {
        for (Game game : commandGame.gameList) {
            game.end();
        }

        ToggleService.Toggleable.PVP.set(false);
        ToggleService.Toggleable.PVP.save();
    }
}
