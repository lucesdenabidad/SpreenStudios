package datta.core.events;

import datta.core.Core;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class PlayerChangeGamemodeListener implements Listener {

    @EventHandler
    public void change(PlayerGameModeChangeEvent event){
        Player player = event.getPlayer();
        GameMode newGameMode = event.getNewGameMode();


        Core.info(player.getName() +" cambio su modo de juego a &7" +newGameMode.name()+"&f.");
    }
}
