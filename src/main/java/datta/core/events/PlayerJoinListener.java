package datta.core.events;

import datta.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent event){

        if (event.getPlayer().isOp()){
            Core.info(event.getPlayer().getName() + " se ha conectado.");
        }

        event.setJoinMessage(null);
    }
}
