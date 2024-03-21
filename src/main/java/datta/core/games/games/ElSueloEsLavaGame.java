package datta.core.games.games;

import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.utils.build.BuildUtils;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.games.Game;
import datta.core.services.list.TimerService;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.builders.MenuBuilder.slot;
import static datta.core.utils.SenderUtil.sendBroadcast;


public class ElSueloEsLavaGame extends Game {
    @Override
    public String name() {
        return "El Suelo es Lava";
    }

    @Override
    public Location spawn() {
        return stringToLocation("-486 111 100 90 0");
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
        teleportSpawn();

        TimerService.bossBarTimer("Seras teletransportado en {time}", BarColor.WHITE, BarStyle.SOLID, 5, () -> {
            for (Player t : Bukkit.getOnlinePlayers()) {
                t.teleport(stringToLocation("-501 110 97"));
                SenderUtil.sendTitle(t, " ", "&a¡Cuidado con la lava!", 15, 50);
                SenderUtil.sendSound(t, Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 1, 1);
            }

            moreLava();
            lavaTask();
        });
    }

    @Subcommand("lava end")
    @Override
    public void end() {
        resetMap();
        Bukkit.getScheduler().cancelTasks(Core.getInstance());
    }

    @Override
    public List<String> scoreboard() {
        return null;
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return List.of(

                new MenuBuilder.MenuItem(new ItemBuilder(Material.LIME_DYE, "&aAumentar nivel de lava").build(), this::moreLava),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.ORANGE_DYE, "&6Disminuir nivel de lava").build(), this::lessLava)
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


    public Location pos1 = stringToLocation("-490 101 110");
    public Location pos2 = stringToLocation("-510 101 90");


    public Location updatedLocation = pos2.clone();
    public int level = updatedLocation.getBlockY();


    private BukkitTask task;
    private int later = 3;
    public void lavaTask() {
        task = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            TimerService.actionbarTimer(later, this::moreLava);
        }, 0, 20 * later);
    }

    public void cancelLavaTask() {
        if (task != null) {
            task.cancel();
        }
    }

    public void resetMap(){
        BuildUtils.replace(stringToLocation("-490 101 90"), stringToLocation("-511 125 111"), Material.LAVA, Material.AIR);
    }

    public void moreLava() {
        updatedLocation.setY(level);
        BuildUtils.replace(pos1, updatedLocation, Material.AIR, Material.LAVA);
        sendBroadcast("&6&lLava &8» &fEl nivel de la lava &caumento&f un bloque.");
        level++;
    }

    public void lessLava() {
        if (level < pos1.getBlockY()) {
            return;
        }

        level--;
        sendBroadcast("&6&lLava &8» &fEl nivel de la lava &adisminuyo&f un bloque.");
        updatedLocation.setY(level);
        Location clone = pos1.clone();
        clone.setY(level);

        BuildUtils.replace(clone, updatedLocation, Material.LAVA, Material.AIR);
    }
}