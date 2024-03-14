package datta.core.paper.service;

import datta.core.paper.Core;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ResetService implements Listener {

    public boolean deleteData = true;
    public boolean removePlayerDataOnLeave = true;
    public boolean removePlayerDataOnServerShutdown = true;

    public boolean UUIDS = true;

    public @NotNull String world = Bukkit.getWorlds().get(0).getName();

    public ResetService() {
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
    }


    public void hook() {
        if (this.removePlayerDataOnServerShutdown) {
            this.deleteAllData();
        }

        if (this.removePlayerDataOnServerShutdown) {
            try {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        ResetService.this.deleteAllData();
                    }
                });
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (removePlayerDataOnLeave) {
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Core.getInstance(), () -> {
                deletePlayerData(player);
            }, 10L);
        }
    }

    @EventHandler
    public void onServerShutdown(PluginDisableEvent event) {
        if (removePlayerDataOnServerShutdown) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                deletePlayerData(player);
            }
        }
    }

    private void deletePlayerData(OfflinePlayer player) {
        this.deleteFile(new File(this.world + "/stats", player.getUniqueId() + ".json"));
        this.deleteFile(new File(this.world + "/stats", player.getName() + ".json"));
    }

    private void deleteFiles(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                File[] var3 = files;
                int var4 = files.length;

                for (int var5 = 0; var5 < var4; ++var5) {
                    File file = var3[var5];
                    file.delete();
                }
            }
        }
    }
    private void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }

    }

    private void deleteAllData() {
        if (this.deleteData) {
            if (this.UUIDS) {
                this.deleteFiles(new File(this.world + "/playerdata"));
            } else {
                this.deleteFiles(new File(this.world + "/players"));
            }
        }

        if (this.deleteData) {
            this.deleteFiles(new File(this.world + "/stats"));
        }
    }
}