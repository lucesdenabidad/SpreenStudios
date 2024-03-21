package datta.core.events;

import datta.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void playerJoin(PlayerQuitEvent event){

        if (event.getPlayer().isOp()){
            Core.info(event.getPlayer().getName() + " se ha desconectado.");
        }

        event.setQuitMessage(null);
    }
}
