package datta.core.services.list;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.CoreTask;
import datta.core.services.Service;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import static datta.core.content.builders.ColorBuilder.color;
import static datta.core.content.builders.ColorBuilder.formatTime;

@CommandPermission("|spreenstudios.*|spreenstudios.timer")
@CommandAlias("timer|temporizador")
public class TimerService extends Service {
    private static BossBar bossBar;
    private static BukkitTask bossbarTask;
    private static BukkitTask actionbartask;

    @Override
    public Core instance() {
        return Core.getInstance();
    }

    @Override
    public String name() {
        return "Timer";
    }

    @Override
    public String[] info() {
        return new String[0];
    }

    @Override
    public void onLoad() {
        register(true, false);
    }

    @Override
    public void onUnload() {
        removeBossBar();
        removeActionbar();
    }


    @Subcommand("actionbar start")
    public void actionbarStart(CommandSender sender, int seconds) {
        actionbarTimer(seconds, () -> {
        });
        SenderUtil.sendMessage(sender, "%core_prefix% &aIniciaste un temporizador tipo ACTIONBAR con éxito.");
    }

    @Subcommand("actionbar end")
    public void actionbarEnd(CommandSender sender) {
        removeActionbar();

        SenderUtil.sendMessage(sender, "%core_prefix% &cEl temporizador tipo ACTIONBAR se ha cancelado y dejó de ser visible.");
    }

    @Subcommand("bossbar start")
    public void timerCMD(CommandSender sender, int seconds, BarColor barColor, BarStyle style, String title) {
        bossBarTimer(title, barColor, style, seconds, () -> {
        });

        SenderUtil.sendMessage(sender, "%core_prefix% &aIniciaste un temporizador tipo BOSSBAR con éxito.");

    }

    @Subcommand("bossbar end")
    public void endCMD(CommandSender sender) {
        removeBossBar();
        SenderUtil.sendMessage(sender, "%core_prefix% &cEl temporizador se ha cancelado y dejó de ser visible.");
    }


    public static void actionbarTimer(int seconds, Runnable runnable) {

        Core.info("Un temporizador tipo ACTIONBAR fue iniciado con éxito.");

        JavaPlugin plugin = Core.getInstance();
        actionbartask = new BukkitRunnable() {
            int totalTimeSeconds = seconds;

            @Override
            public void run() {

                for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                    onlinePlayer.sendActionBar(formatTime(totalTimeSeconds));

                if (totalTimeSeconds == 0) {
                    CoreTask.runTask(plugin, runnable, 20L);
                    cancel();
                }

                totalTimeSeconds--;
            }
        }.runTaskTimer(Core.getInstance(), 0, 20L);
    }

    public static void bossBarTimer(String title, BarColor barColor, BarStyle barStyle, int seconds, Runnable runnable) {

        Core.info("Un temporizador tipo BOSSBAR fue iniciado con éxito.");
        JavaPlugin plugin = Core.getInstance();

        bossBar = Bukkit.createBossBar(color(title), barColor, barStyle);
        bossBar.setTitle(color(title));
        bossBar.setProgress(1.0);
        bossBar.setVisible(true);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(onlinePlayer);
            SenderUtil.sendSound(onlinePlayer, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
        }

        bossbarTask = new BukkitRunnable() {
            int totalTimeSeconds = seconds;

            @Override
            public void run() {
                String finalTitle = title.replace("{time}", formatTime(totalTimeSeconds));
                double progress = (double) totalTimeSeconds / (double) seconds;

                bossBar.setProgress(Math.max(0, Math.min(progress, 1)));
                bossBar.setTitle(color(finalTitle));

                if (totalTimeSeconds == 0) {
                    CoreTask.runTask(plugin, runnable, 20L);


                    bossBar.setVisible(false);
                    bossBar.removeAll();
                    cancel();
                }

                totalTimeSeconds--;
            }
        }.runTaskTimer(Core.getInstance(), 0, 20L);
    }

    public static void removeBossBar() {
        if (bossbarTask != null) {
            bossbarTask.cancel();
        }

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar.setVisible(false);
        }
    }

    public static void removeActionbar() {
        if (actionbartask != null) {
            actionbartask.cancel();
        }

        Core.info("El temporizador activo tipo ACTIONBAR se elimino con éxito.");
    }
}