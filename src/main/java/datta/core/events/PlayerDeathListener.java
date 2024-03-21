package datta.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static datta.core.content.builders.ColorBuilder.color;

public class PlayerDeathListener implements Listener {


    String MSG = " ha muerto.";
    @EventHandler
    public void death(PlayerDeathEvent e){
        Player player = e.getPlayer();
        e.setDeathMessage(color("&c"+player.getName() + MSG));
    }
}
