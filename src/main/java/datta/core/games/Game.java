package datta.core.games;

import co.aikar.commands.BaseCommand;
import datta.core.Core;
import datta.core.content.CoreTask;
import datta.core.content.builders.MenuBuilder;
import datta.core.services.list.ScoreboardService;
import datta.core.services.list.ScreenColorService;
import datta.core.services.list.TimerService;
import datta.core.services.list.ToggleService;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static datta.core.content.utils.EventUtils.heal;
import static datta.core.content.utils.EventUtils.isStaff;

public abstract class Game extends BaseCommand implements Listener {

    public abstract int endAt();
    public abstract String name();
    public abstract Location spawn();
    public abstract void start();
    public abstract void end();
    public abstract List<String> scoreboard();
    public abstract List<MenuBuilder.MenuItem> menuItems(Player player);
    public abstract Material menuItem();


    public void loadScoreboard() {
        ScoreboardService service = (ScoreboardService) Core.getInstance().commandService.serviceFromName("scoreboard");

        String defaultTitle = Core.getInstance().defaultTitle;

        List<String> lines = new ArrayList<>(scoreboard());

        if (!Core.getInstance().defaultLines.isEmpty() && Core.getInstance().defaultLines.size() > 0) {
            String ip = Core.getInstance().defaultLines.get(Core.getInstance().defaultLines.size() - 1);
            lines.add(ip);
        }

        service.changeScore(defaultTitle, lines);
    }
    public void teleportSpawn() {
        ScreenColorService service = (ScreenColorService) Core.getInstance().commandService.serviceFromName("screencolor");
        int fade = 25;

        service.showAll(ChatColor.BLACK, fade, fade);

        CoreTask.runTask(() -> {
            for (Player t : Bukkit.getOnlinePlayers()) {
                t.teleport(spawn().toCenterLocation());
            }
        }, 45L);
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
        }, 50L);
    }

    public void game(Runnable run) {

        for (Player t : Bukkit.getOnlinePlayers()) {
            heal(t);

            if (!isStaff(t)) {
                t.getInventory().clear();
            }

            for (PotionEffect activePotionEffect : t.getActivePotionEffects()) {
                t.removePotionEffect(activePotionEffect.getType());
            }
        }


        Core.setEndAt(this.endAt());
        registerGameEvents(this);
        loadScoreboard();
        run.run();
    }

    public void end(Runnable runnable) {
        ToggleService.Toggleable.PVP.set(false, true);
        ToggleService.Toggleable.KICK_ON_DEATH.set(false, true);

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
        }

        TimerService.removeBossBar();
        TimerService.removeActionbar();

        ScoreboardService service = (ScoreboardService) Core.getInstance().commandService.serviceFromName("scoreboard");
        service.changeScore(Core.getInstance().defaultTitle, Core.getInstance().defaultLines);

        for (Player t : Bukkit.getOnlinePlayers()) {
            if (!isStaff(t)) {
                t.getInventory().clear();
            }

            for (PotionEffect activePotionEffect : t.getActivePotionEffects()) {
                if (activePotionEffect.getType() != PotionEffectType.NIGHT_VISION) {
                    t.removePotionEffect(activePotionEffect.getType());
                }
            }

            t.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        Core.setEndAt(0);
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