package datta.core.paper.utilities.services;

import datta.core.paper.Core;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static datta.core.paper.utilities.Color.format;
import static datta.core.paper.utilities.Color.formatTime;

public class TimerService {
    public static BossBar bossBar;

    public static void timer(String title, BarColor barColor, BarStyle barStyle, int seconds, Runnable runnable) {
        JavaPlugin plugin = Core.getInstance();

        bossBar = Bukkit.createBossBar(format(title), barColor, barStyle);
        final int[] totalTimeSeconds = {seconds};
        bossBar.setTitle(format(title));
        bossBar.setProgress(1.0);
        bossBar.setVisible(true);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(onlinePlayer);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                String finalTitle = title.replace("{time}", formatTime(totalTimeSeconds[0]));
                double progress = (double) totalTimeSeconds[0] / (double) seconds;

                bossBar.setProgress(Math.max(0, Math.min(progress, 1)));
                bossBar.setTitle(format(finalTitle));
                totalTimeSeconds[0]--;

                if (totalTimeSeconds[0] == 1) {
                    bossBar.setVisible(false);
                    bossBar.removeAll();
                    bossBar.setProgress(1.0);
                    bossBar.removeAll();
                    runnable.run();
                    bossBar = null;
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20L);

    }

    public static void removeBar() {
        if (bossBar == null) {
            return;
        }

        bossBar.setVisible(false);
        bossBar.removeAll();
        bossBar = null;
    }
}
