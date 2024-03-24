package datta.core.services.list;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import datta.core.Core;
import datta.core.services.Service;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("vanish|v")
public class VanishService extends Service implements Listener {

    private final List<Player> vanishList = new ArrayList<>();
    private BukkitTask task;

    @Override
    public Core instance() {
        return Core.getInstance();
    }

    @Override
    public String name() {
        return "vanish";
    }

    @Override
    public String[] info() {
        return new String[0];
    }

    @Override
    public void onLoad() {
        register(true, true);
        checkerTask();
    }

    @Override
    public void onUnload() {

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            showPlayer(onlinePlayer);
        }
    }

    @CommandPermission("spreenstudios.vanish")
        @CommandAlias("vanish|v")
    public void vanishCMD(Player player) {
        boolean inVanish = isInVanish(player);
        setVanish(player, !inVanish);

        SenderUtil.sendMessage(player, "%core_prefix% &eTu visibilidad hacia los demas fue modificada a " + isInVanish(player) + ".");
    }

    public void setVanish(Player player, boolean value) {
        if (value) {
            vanishList.add(player);
        } else {
            vanishList.remove(player);
        }
    }

    public boolean isInVanish(Player player) {
        return vanishList.contains(player);
    }

    public void showPlayer(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(instance(), player);
        }
    }

    public void hidePlayer(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hidePlayer(instance(), player);
        }

        SenderUtil.sendActionbar(player, "&b¡Estás oculto!");
    }

    public void checkerTask() {
        if (task != null) {
            task.cancel();
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (isInVanish(onlinePlayer)) { // esta en vanish
                        hidePlayer(onlinePlayer);
                    } else { // no esta en vanish
                        showPlayer(onlinePlayer);
                    }
                }
            }
        }.runTaskTimer(instance(), 0, 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        vanishList.remove(player);

        if (isInVanish(player)) {
            hidePlayer(player);
        } else {
            showPlayer(player);
        }
    }
}