package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.commands.CallCMD;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventPlayer;
import datta.core.content.utils.EventUtils;
import datta.core.content.utils.build.BuildUtils;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.games.Game;
import datta.core.services.list.TimerService;
import datta.core.services.list.ToggleService;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.utils.build.BuildUtils.replace;


@CommandAlias("games")
public class ElSueloEsLavaGame extends Game {
    @Override
    public String name() {
        return "El Suelo es Lava";
    }

    @Override
    public Location spawn() {
        return stringToLocation("401 3 549");
    }


    @Override
    public void start() {
        game(() -> {
            CallCMD.callToggleable(ToggleService.Toggleable.PVP, false);
            CallCMD.callToggleable(ToggleService.Toggleable.FALL_DAMAGE, false);
            CallCMD.callToggleable(ToggleService.Toggleable.DAMAGE, true);
            CallCMD.callToggleable(ToggleService.Toggleable.FOOD, false);
            CallCMD.callToggleable(ToggleService.Toggleable.KICK_ON_DEATH, true);
            CallCMD.callToggleable(ToggleService.Toggleable.SPAWNING_MOBS, false);
            CallCMD.callToggleable(ToggleService.Toggleable.PLACE, false);
            CallCMD.callToggleable(ToggleService.Toggleable.BREAK, false);
            CallCMD.callToggleable(ToggleService.Toggleable.INTERACTIONS, false);
            CallCMD.callToggleable(ToggleService.Toggleable.TELEPORT_SPAWN_ON_JOIN, true);

            door(false);
            for (Player t : Bukkit.getOnlinePlayers()) {
                SenderUtil.sendSound(t, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2);
            }

            TimerService.actionbarTimer(30, () -> {
                moreLava();

                startLavaTask();
            });
        });
    }

    @Subcommand("lava end")
    @Override
    public void end() {
        end(() -> {
            resetMap();
            door(true);
        });
    }

    @Override
    public List<String> scoreboard() {
        return new ArrayList<>();
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return List.of(

                new MenuBuilder.MenuItem(new ItemBuilder(Material.WOODEN_AXE, "&aResetear mapa").build(), this::resetMap),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.LIME_DYE, "&aAumentar 1 nivel de lava").build(), this::moreLava),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.ORANGE_DYE, "&6Disminuir 1 nivel de lava").build(), this::lessLava),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.SLIME_BALL, "&aIniciar 'LavaTask'").build(), this::startLavaTask),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.REDSTONE, "&cCancelar 'LavaTask'").build(), this::cancelLavaTask)
        );
    }

    @Override
    public Material menuItem() {
        return Material.LAVA_BUCKET;
    }

    public Location pos1 = stringToLocation("440 3 510");
    public static Location pos2 = stringToLocation("363 3 587");

    public static Location updatedLocation = pos2.clone();
    public static int level = updatedLocation.getBlockY();
    private BukkitTask task;
    private final int later = 5;
    private final int levelPerLater = 3;

    public void startLavaTask() {
        cancelLavaTask();

        task = new BukkitRunnable() {
            int time = 0;
            @Override
            public void run() {
                if (time == later) {
                    for (int i = 0; i < levelPerLater; i++)
                        moreLava();

                    time = 0;
                }

                time++;
            }
        }.runTaskTimer(Core.getInstance(), 0, 20L);
    }

    public void cancelLavaTask() {
        if (task != null) {
            task.cancel();
        }
    }

    @Subcommand("lava task start")
    public void startLavaTaskCMD() {
        startLavaTask();
    }

    @Subcommand("lava task stop")
    public void stopLavaTaskCMD() {
        cancelLavaTask();
    }


    @Subcommand("lava mapreset")
    public void resetMap() {
        Cuboid cuboid = new Cuboid("440 48 510 363 2 587");
        replace(cuboid, Material.LAVA, Material.AIR);

        level = pos2.clone().getBlockY();
    }

    @Subcommand("lava subir")
    public void moreLava() {
        if (level == 35){
            cancelLavaTask();
            return;
        }

        updatedLocation.setY(level);

        Cuboid cuboid = new Cuboid(pos1, updatedLocation);
        replace(cuboid, Material.AIR, Material.LAVA);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            SenderUtil.sendActionbar(onlinePlayer, "&#cf1020La lava a aumentado de nivel...");

        level++;
    }

    @Subcommand("lava bajar")
    public void lessLava() {
        if (level < pos1.getBlockY()) {
            return;
        }

        level--;
        updatedLocation.setY(level);
        Location clone = pos1.clone();
        clone.setY(level);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            SenderUtil.sendActionbar(onlinePlayer, "&#cf1020La lava a disminuido de nivel...");

        Cuboid cuboid = new Cuboid(pos1, updatedLocation);
        replace(cuboid, Material.LAVA, Material.AIR);
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Block block = location.getBlock();
        EventPlayer eventPlayer = new EventPlayer(player);
        if (eventPlayer.isStaff()) return;

        if (block.getType() == Material.LAVA) {
            EventUtils.eliminate(player, true);
        }
    }


    @Subcommand("lava door")
    public void door(boolean value) {
        Cuboid cuboid = new Cuboid("406 5 553 397 3 544");

        if (value) {
            BuildUtils.walls(cuboid, Material.BARRIER);
        } else {
            BuildUtils.walls(cuboid, Material.AIR);
        }
    }
}