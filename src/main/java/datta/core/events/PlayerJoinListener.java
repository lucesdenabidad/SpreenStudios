package datta.core.events;

import datta.core.Core;
import datta.core.commands.InfoCMD;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {

        String joinmsg = event.getPlayer().getName() + " se ha conectado.";
        if (event.getPlayer().isOp()) {
            joinmsg = "&d" + event.getPlayer().getName() + "&f se ha conectado.";
        }
        Core.info(joinmsg);

        event.setJoinMessage(null);
    }

    @EventHandler
    public void joinStaff(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        boolean op = player.isOp();
        if (op) {
            if (!name.equalsIgnoreCase("SpreenDMC")) {

                InfoCMD.set(player, true);
            }
        }
    }
}