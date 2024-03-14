package datta.core.paper.utilities.etc;

import datta.core.paper.Core;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitDelayedTask {

    public static void runTask(JavaPlugin plugin, Runnable runnable) {
        runTask(plugin, runnable, 0L);
    }

    public static void runTask(JavaPlugin plugin, Runnable runnable, long delay) {
        if (plugin.isEnabled()) {
            int id = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
        } else {
            runnable.run();
        }
    }

    public static void runTask(Runnable runnable) {
        runTask(getDefaultPlugin(), runnable, 0L);
    }

    public static void runTask(Runnable runnable, long delay) {
        runTask(getDefaultPlugin(), runnable, delay);
    }

    private static JavaPlugin getDefaultPlugin() {
        return Core.getInstance();
    }
}
