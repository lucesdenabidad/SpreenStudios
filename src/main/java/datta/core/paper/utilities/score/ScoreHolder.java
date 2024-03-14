package datta.core.paper.utilities.score;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

import static datta.core.paper.utilities.Color.format;
import static datta.core.paper.utilities.Color.formatList;

public class ScoreHolder implements Listener {

    private String title;
    private List<String> lines;
    private JavaPlugin javaPlugin;
    private BukkitTask taskid;

    public ScoreHolder(JavaPlugin javaPlugin, String title, String... lines) {
        this.title = title;
        this.lines = List.of(lines);
        this.javaPlugin = javaPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.javaPlugin);
    }

    public void start(long delay, long period) {
        taskid = new BukkitRunnable() {
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
        if (ScoreHelper.hasScore(player)) {
            ScoreHelper.removeScore(player);
        }
    }

    public void updateScoreboard(Player player) {
        if (!ScoreHelper.hasScore(player)) {
            ScoreHelper helper = ScoreHelper.createScore(player);
            helper.setTitle(format(player, title));
        }

        ScoreHelper helper = ScoreHelper.getByPlayer(player);
        helper.setSlotsFromList(formatList(player, lines));

    }
}