package datta.core.content.score;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

import static datta.core.content.builders.ColorBuilder.color;
import static datta.core.content.builders.ColorBuilder.colorList;

public class ScoreHolder implements Listener {

    public String title;
    public List<String> lines;
    private JavaPlugin javaPlugin;
    public BukkitTask task;

    public ScoreHolder(JavaPlugin javaPlugin, String title, List<String> lines) {
        this.title = title;
        this.lines = lines;
        this.javaPlugin = javaPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.javaPlugin);
    }

    public void start(long delay, long period) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }

        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player t : Bukkit.getOnlinePlayers()) {
                    updateScoreboard(t);
                }
            }
        }.runTaskTimer(javaPlugin, delay, period);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removePlayer(player);
    }

    public void removePlayer(Player player) {
        if (ScoreHelper.hasScore(player)) {
            ScoreHelper.removeScore(player);
        }
    }

    public void updateScoreboard(Player player) {
        if (!ScoreHelper.hasScore(player)) {
            ScoreHelper helper = ScoreHelper.createScore(player);
            helper.setTitle(color(player, title));
        }

        ScoreHelper helper = ScoreHelper.getByPlayer(player);
        helper.setSlotsFromList(colorList(player, lines));
    }
}
