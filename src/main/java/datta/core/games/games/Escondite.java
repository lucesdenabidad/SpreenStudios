package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventUtils;
import datta.core.content.utils.build.BuildUtils;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.events.spreenstudios.StickInteractEvent;
import datta.core.games.Game;
import datta.core.services.individual.Glow;
import datta.core.services.list.TimerService;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

import static datta.core.content.builders.ColorBuilder.stringToLocation;

@CommandPermission("spreenstudios.games")
@CommandAlias("games")
public class Escondite extends Game {
    public static boolean status = false;

    @Override
    public int endAt() {
        return 160;
    }

    @Override
    public String name() {
        return "Escondite";
    }

    @Override
    public Location spawn() {
        return stringToLocation("560 63 160 180 0");
    }

    @Override
    @Subcommand("escondite start")

    public void start() {
        game(this::startGame);
    }

    @Override
    @Subcommand("escondite stop")
    public void end() {
        status = false;

        BuildUtils.set(WALLS, Material.BARRIER);
    }

    @Override
    public List<String> scoreboard() {
        return new ArrayList<>();
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return List.of();
    }

    @Override
    public Material menuItem() {
        return Material.NETHER_STAR;
    }

    // # const

    public int FIGURE_SEKEER_AT = 60;
    public int GAME_DURATION = 60 * 10;
    public String SEEKER = "SpreenDMC";

    public Cuboid WALLS = new Cuboid("556 63 155 565 76 155");
    public BukkitTask TASK;



    @Subcommand("escondite figureseeker")
    public void figureSeeker() {
        OfflinePlayer of = Bukkit.getOfflinePlayer(SEEKER);
        Player player = of.getPlayer();

        if (player == null || !of.isOnline()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 900 * 90 * 3 * 3, 255, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 900 * 90 * 3 * 3, 255, false, false, false));
        SenderUtil.sendActionbar(player, "&7(!) &fFuiste vendado...");
    }
    @Subcommand("escondite teleportseeker")
    public boolean teleportSeeker() {
        OfflinePlayer of = Bukkit.getOfflinePlayer(SEEKER);
        Player player = of.getPlayer();

        if (player == null || !of.isOnline()) {
            return false;
        }

        TimerService.removeActionbar();
        TimerService.removeBossBar();

        Glow.glowPlayer(player, true);
        EventUtils.addPlayerColor(player, "&c");


        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            SenderUtil.sendActionbar(onlinePlayer, "&cEl buscador fue liberado, ¡TEN CUIDADO!");



        player.sendTitle("", "");
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.teleport(spawn());
        player.performCommand("kickstick");

        return true;
    }

    public void startGame() {
        status = true;

        figureSeeker();
        BuildUtils.set(WALLS, Material.AIR);

        TimerService.bossBarTimer("&fEl buscador sera liberado en &e{time}&f", BarColor.PURPLE, BarStyle.SOLID, FIGURE_SEKEER_AT, () -> {

            if (teleportSeeker()) {
                TimerService.bossBarTimer("&fEl juego acaba en: &e{time}&f", BarColor.PURPLE, BarStyle.SOLID, GAME_DURATION, () -> {

                });
            }
        });
    }


    public void task(){
        if (TASK != null) TASK.cancel();
        TASK = new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskTimer(Core.getInstance(),0,20L);
    }

    // # Eventos
    @EventHandler
    public void onInteract(StickInteractEvent event) {
        Player player = event.getExecutor();

        if (!SEEKER.equalsIgnoreCase(player.getName()))
            return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 7 * 20, 1, false, false));
    }
}