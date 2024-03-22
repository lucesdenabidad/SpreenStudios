package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventUtils;
import datta.core.content.utils.build.BuildUtils;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.games.Game;
import datta.core.services.list.TimerService;
import datta.core.utils.SenderUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.builders.MenuBuilder.slot;
import static datta.core.utils.SenderUtil.sendBroadcast;


@CommandAlias("games")
public class ElSueloEsLavaGame extends Game {
    @Override
    public String name() {
        return "El Suelo es Lava";
    }

    @Override
    public Location spawn() {
        return stringToLocation("401 3 507");
    }

    @Override
    public String[] gameinfo() {
        return new String[]{
                "&fCada cierto tiempo la lava sube de manera",
                "&ffrenetica y sin esperar a ningun participante",
                "&faumenta sin previo aviso."};
    }

    @Override
    public void start() {
        game(() -> {
            TimerService.bossBarTimer("&e(!) El cristal se destruye en &e{time} ⌚", BarColor.PURPLE, BarStyle.SOLID, 5, () -> {
                door(false);

                for (Player t : Bukkit.getOnlinePlayers()) {
                    SenderUtil.sendSound(t, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2);
                }

                timerLava(30);
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
        return new ArrayList<>(List.of(
                "",
                "&fNivel de lava: &e%core_lavastatus%",
                ""
        ));
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return List.of(

                new MenuBuilder.MenuItem(new ItemBuilder(Material.WOODEN_AXE, "&aResetear mapa").build(), this::resetMap),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.LIME_DYE, "&aAumentar 1 nivel de lava").build(), this::moreLava),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.ORANGE_DYE, "&6Disminuir 1 nivel de lava").build(), this::lessLava),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.LAVA_BUCKET, "&e10 segundos para aumentar lava").build(), () -> {
                    timerLava(10);
                })
        );
    }

    @Override
    public ItemStack menuItem() {
        return new ItemBuilder(Material.LAVA_BUCKET, "&6El suelo es lava")
                .addLore("", "&bInformación:")
                .addLore(gameinfo())
                .addLore("", "&aClic derecho para manejar juego.")
                .build();
    }

    @Override
    public int menuSlot() {
        return slot(4, 2);
    }

    public Location pos1 = stringToLocation("440 3 510");
    public static Location pos2 = stringToLocation("363 3 587");

    public static Location updatedLocation = pos2.clone();
    public static int level = updatedLocation.getBlockY();


    private BukkitTask task;
    private int later = 10;

    public void lavaTask() {
        task = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            TimerService.actionbarTimer(later, this::moreLava);
        }, 0, 20 * later);
    }


    @Subcommand("lava timerlava")
    public void timerLava(int later) {
        TimerService.actionbarTimer(later, this::moreLava);
    }

    public void cancelLavaTask() {
        if (task != null) {
            task.cancel();
        }
    }

    @Subcommand("lava mapreset")
    public void resetMap() {
        BuildUtils.replace(stringToLocation("363 48 587"), stringToLocation("440 2 510"), Material.LAVA, Material.AIR);
    }

    @Subcommand("lava subir")
    public void moreLava() {
        updatedLocation.setY(level);
        optimizedReplace(new Cuboid(pos1, updatedLocation), Material.AIR, Material.LAVA);
        sendBroadcast("&6&lLava &8» &fEl nivel de la lava &caumento&f un bloque.");
        level++;
    }

    @Subcommand("lava bajar")
    public void lessLava() {
        if (level < pos1.getBlockY()) {
            return;
        }

        level--;
        sendBroadcast("&6&lLava &8» &fEl nivel de la lava &adisminuyo&f un bloque.");
        updatedLocation.setY(level);
        Location clone = pos1.clone();
        clone.setY(level);

        optimizedReplace(new Cuboid(pos1, updatedLocation), Material.LAVA, Material.AIR);
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Block block = location.getBlock();
        if (block.getType() == Material.LAVA) {
            EventUtils.eliminate(player, true);
        }
    }

    public void door(boolean value) {
        Cuboid cuboid = new Cuboid("397 13 509 406 3 509");

        if (value) {
            optimizedReplace(cuboid, Material.AIR, Material.RED_STAINED_GLASS);
        } else {
            optimizedReplace(cuboid, Material.RED_STAINED_GLASS, Material.AIR);
        }
    }
    public void optimizedReplace(Cuboid cuboid, Material targetMaterial, Material newMaterial) {
        Location point1 = cuboid.getPoint1();
        Location point2 = cuboid.getPoint2();

        World world = point1.getWorld();

        int minX = Math.min(point1.getBlockX(), point2.getBlockX());
        int minY = Math.min(point1.getBlockY(), point2.getBlockY());
        int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
        int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        int maxY = Math.max(point1.getBlockY(), point2.getBlockY());
        int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == targetMaterial && block.getType() != newMaterial) {
                        block.setType(newMaterial);
                    }
                }
            }
        }
    }
}
