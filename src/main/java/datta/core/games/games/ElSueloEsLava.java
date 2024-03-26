package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.commands.CallCMD;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventUtils;
import datta.core.content.utils.build.BuildUtils;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.games.Game;
import datta.core.services.list.TimerService;
import datta.core.services.list.ToggleService;
import datta.core.utils.SenderUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.utils.build.BuildUtils.replace;

@CommandPermission("spreenstudios.games")
@CommandAlias("games")
public class ElSueloEsLava extends Game {
    @Override
    public int endAt() {
        return 120;
    }

    @Override
    public String name() {
        return "El Suelo es Lava";
    }

    @Override
    public Location spawn() {
        return stringToLocation("328 4 504");
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
                moreLava(2);

                startLavaTask();
            });
        });
    }

    @Subcommand("lava end")
    @Override
    public void end() {
        end(() -> {
            cancelLavaTask();
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

                new MenuBuilder.MenuItem(new ItemBuilder(Material.LIME_DYE, "&aAumentar 1 nivel de lava").build(), () -> {
                    moreLava(1);
                }),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.ORANGE_DYE, "&6Disminuir 1 nivel de lava").build(), this::lessLava),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.SLIME_BALL, "&aIniciar 'LavaTask'").build(), this::startLavaTask),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.REDSTONE, "&cCancelar 'LavaTask'").build(), this::cancelLavaTask)
        );
    }

    @Override
    public Material menuItem() {
        return Material.LAVA_BUCKET;
    }

    public Location pos1 = stringToLocation("388 3 632");
    public static Location pos2 = stringToLocation("267 3 511");

    public static Location updatedLocation = pos2.clone();
    public static int level = updatedLocation.getBlockY();
    private BukkitTask task;
    private final int later = 7;
    private final int levelPerLater = 1;

    public void startLavaTask() {
        cancelLavaTask();

        task = new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                if (time == later) {
                    moreLava(levelPerLater);

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


    @Subcommand("lava reset")
    public void resetMap() {
        Cuboid cuboid = new Cuboid("388 47 632 267 2 511");
        replace(cuboid, Material.LAVA, Material.AIR);

        level = pos2.clone().getBlockY();
    }

    @Subcommand("lava subir")
    public void moreLava(int aumentar) {
        if (level > 42) {
            cancelLavaTask();
            return;
        }

        updatedLocation.setY(level);

        Cuboid cuboid = new Cuboid(pos1, updatedLocation);
        replace(cuboid, Material.AIR, Material.LAVA);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            SenderUtil.sendActionbar(onlinePlayer, "&#cf1020La lava a aumentado de nivel...");

        for (int i = 0; i < aumentar; i++) {
            level++;
        }
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


    public boolean lavaDeath = true;
    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Block block = location.getBlock();

        if (!player.isOp()) {

            if (lavaDeath){

            if (block.getType() == Material.LAVA) {
                EventUtils.eliminate(player, true);
            }
            }
        }
    }

    @EventHandler
    public void playerLeave(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) {
            int alive = 0;

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getGameMode() != GameMode.SURVIVAL) {
                    alive++;
                }
            }

            if (alive <= endAt()) {

                cancelLavaTask();
            }
        }
    }

    @Subcommand("lava death")
    public void death(boolean value){
        lavaDeath = value;
        Core.info("");
    }

    @Subcommand("lava door")
    public void door(boolean value) {
        Cuboid cuboid = new Cuboid("332 3 510 323 13 510");

        if (value) {
            BuildUtils.set(cuboid, Material.BARRIER);
        } else {
            BuildUtils.set(cuboid, Material.AIR);
        }
    }
}