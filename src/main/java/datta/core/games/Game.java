package datta.core.games;

import co.aikar.commands.BaseCommand;
import datta.core.Core;
import datta.core.content.CoreTask;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventPlayer;
import datta.core.services.list.ScoreboardService;
import datta.core.services.list.ScreenColorService;
import datta.core.services.list.ToggleService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public abstract class Game extends BaseCommand implements Listener {

    public abstract String name();

    public abstract Location spawn();

    public abstract String[] gameinfo();

    public abstract void start();

    public abstract void end();

    public abstract List<String> scoreboard();

    public abstract List<MenuBuilder.MenuItem> menuItems(Player player);

    public abstract ItemStack menuItem();

    public abstract int menuSlot();

    public void loadScoreboard() {
        ScoreboardService service = (ScoreboardService) Core.getInstance().commandService.serviceFromName("scoreboard");

        String defaultTitle = Core.getInstance().defaultTitle;
        String ip = Core.getInstance().defaultLines.get(Core.getInstance().defaultLines.size() - 1);

        List<String> lines = new ArrayList<>(scoreboard());
        lines.add(ip);

        service.changeScore(defaultTitle, lines);
    }

    public void teleportSpawn() {
        ScreenColorService service = (ScreenColorService) Core.getInstance().commandService.serviceFromName("screencolor");
        int fade = 50;

        service.showAll(ChatColor.BLACK, fade, fade);
        CoreTask.runTask(() -> {
            for (Player t : Bukkit.getOnlinePlayers()) {
                t.teleport(spawn().toCenterLocation());
            }
        }, 20L * 2);
    }

    public void teleportSpawn(Runnable runnable) {
        ScreenColorService service = (ScreenColorService) Core.getInstance().commandService.serviceFromName("screencolor");
        int fade = 50;

        service.showAll(ChatColor.BLACK, fade, fade);
        CoreTask.runTask(() -> {
            for (Player t : Bukkit.getOnlinePlayers()) {
                t.teleport(spawn().toCenterLocation());
            }

            CoreTask.runTask(runnable, 70L);
        }, 20L * 2);
    }

    public void game(Runnable run) {

        for (Player t : Bukkit.getOnlinePlayers()) {
            EventPlayer e = new EventPlayer(t);
            if (!e.isStaff()) {
                t.getInventory().clear();
            }
            for (PotionEffect activePotionEffect : t.getActivePotionEffects()) {
                t.removePotionEffect(activePotionEffect.getType());
            }
        }

        teleportSpawn(() -> {
            registerGameEvents(this);
            loadScoreboard();
            run.run();
        });
    }

    public void end(Runnable runnable) {
        ToggleService.Toggleable.PVP.set(false);
        ToggleService.Toggleable.PVP.save();

        ToggleService.Toggleable.KICK_ON_DEATH.set(false);
        ToggleService.Toggleable.KICK_ON_DEATH.save();

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
        }
        ScoreboardService service = (ScoreboardService) Core.getInstance().commandService.serviceFromName("scoreboard");
        service.changeScore(Core.getInstance().defaultTitle, Core.getInstance().defaultLines);

        for (Player t : Bukkit.getOnlinePlayers()) {
            EventPlayer e = new EventPlayer(t);
            if (!e.isStaff()) {
                t.getInventory().clear();
            }

            for (PotionEffect activePotionEffect : t.getActivePotionEffects()) {
                t.removePotionEffect(activePotionEffect.getType());
            }
        }

        unregisterGameEvents(this);
        runnable.run();

    }
    private void registerGameEvents(Game game) {
        Core.getInstance().getServer().getPluginManager().registerEvents(game, Core.getInstance());
    }

    private void unregisterGameEvents(Game game) {
        HandlerList.unregisterAll(game);
    }

}