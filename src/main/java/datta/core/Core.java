package datta.core;

import co.aikar.commands.Locales;
import co.aikar.commands.PaperCommandManager;
import com.google.gson.JsonObject;
import datta.core.commands.*;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.configuration.Configuration;
import datta.core.content.configuration.ConfigurationManager;
import datta.core.content.portals.PortalManager;
import datta.core.hooks.voicechat.VoiceChatHook;
import datta.core.netty.PacketManager;
import datta.core.weapons.GameItem;
import datta.core.weapons.Stick;
import datta.core.weapons.Weapon;
import datta.core.weapons.sticks.*;
import datta.core.events.*;
import datta.core.games.Game;
import datta.core.games.GameManager;
import datta.core.games.commands.GamesCommand;
import datta.core.menus.GameMenu;
import datta.core.services.CommandService;
import datta.core.services.individual.FreezeList;
import datta.core.services.individual.Glow;
import datta.core.services.list.ToggleService;
import datta.core.utils.SenderUtil;
import datta.events.simondice.SimonDice;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static datta.core.CoreMethods.listener;


public class Core extends JavaPlugin {

    public static String endAt = "?";
    private static @Getter Core instance;
    public @Getter PaperCommandManager commandManager;
    public static MenuBuilder menuBuilder;
    public ConfigurationManager configurationManager;
    @Getter
    public Configuration config;

    public CommandService commandService;
    public String prefix;
    public CoreParse coreParse;
    public GameManager gameManager;
    public static List<Player> disableLog = new ArrayList<>();
    public PortalManager portalManager;
    public VoiceChatHook voiceChatHook;
    public PacketManager packetManager;

    public String defaultTitle = "&5&lEventos";
    public boolean visible = false;
    public List<String> defaultLines = new ArrayList<>();

    public static void setEndAt(int i) {
        if (i != 0) {
            endAt = String.valueOf(i);
        } else {
            endAt = "?";
        }
    }


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

        gameManager = new GameManager(this);
        prefix = "&5&lEvento &8»";
        coreParse = new CoreParse();

        portalManager = new PortalManager(this);

        gameManager.loadGamesFromList();

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
        commandManager.registerCommand(new PutCMD());
        commandManager.registerCommand(new VisibilityCMD());

        Stick.registerStick(new PunchStick());
        Stick.registerStick(new KickStick());
        Stick.registerStick(new KillStick());
        Stick.registerStick(new ColorStick());
        Stick.registerStick(new VoiceStick());

        Weapon.register(new GameItem());

        listener(new PlayerQuitListener());
        listener(new PlayerDeathListener());
        listener(new PlayerJoinListener());
        listener(new PlayerChangeGamemodeListener());
        listener(new FreezeList());
        listener(new EntityDamagebyEntityListener());

        Glow glow = new Glow();

        commandManager.getLocales().addMessageBundle("core", Locales.SPANISH);
        commandManager.getLocales().setDefaultLocale(Locales.SPANISH);

        // # Simon Dice
        commandManager.registerCommand(new SimonDice());
        listener(new SimonDice());

        coreParse.register();
        stopGames();

        //packet register
        packetManager = new PacketManager(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "spreengames:global");
    }

    @Override
    public void onDisable() {
        commandService.unloadServices();
        stopGames();
        coreParse.unregister();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        // disable all hooks
        if (voiceChatHook != null)
            voiceChatHook.disable();
    }

    public static void info(String... s) {
        for (String e : s) {
            SenderUtil.sendMessage(Bukkit.getServer().getConsoleSender(), "&7[SpreenStudios] [Log] &r" + e);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.getName().equalsIgnoreCase("SpreenDMC")) {
                    if (onlinePlayer.isOp() && !disableLog.contains(onlinePlayer)) {
                        SenderUtil.sendMessage(onlinePlayer, "&8[Log] &r" + e);
                    }
                }
            }
        }
    }


    public static void callSetting(ToggleService.Toggleable toggleable, boolean value) {
        toggleable.set(value);
        toggleable.save();
        info(toggleable.name() + " se cambio a &e" + value + "&f.");
    }

    public void stopGames() {
        for (Game game : gameManager.games) {
            game.end();
        }
    }

    public void setVisible(boolean allow){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", allow);

        visible = allow;

        Bukkit.getOnlinePlayers().forEach(player ->{
            Core.getInstance().packetManager.sendGlobalPacket(player, "setrenderlock", jsonObject);
        });
    }

    public void setVisible(Player player){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", visible);
        Core.getInstance().packetManager.sendGlobalPacket(player, "setrenderlock", jsonObject);
    }
}