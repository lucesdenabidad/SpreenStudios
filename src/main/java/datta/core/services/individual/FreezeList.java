package datta.core.services.individual;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class FreezeList implements Listener {

    public static List<Player> freezeList = new ArrayList<>();


    @EventHandler
    public void freeze(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (freezeList.contains(player)) {
            event.setCancelled(true);
        }
    }

    public static void freezePlayer(Player player, boolean set) {
        if (set) {
            freezeList.add(player);
        } else {
            freezeList.remove(player);
        }
    }
}