package datta.core.paper.events;

import datta.core.paper.Core;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static datta.core.paper.utilities.Color.format;

public class MessagesEvent implements Listener {

    public static String JOIN_MSG = "§a{0} se unió al servidor";
    public static String QUIT_MSG = "§c{0} abandonó el servidor";

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(Core.spawn);
        event.joinMessage(Component.text(format(JOIN_MSG, event.getPlayer().getName())));

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(format(QUIT_MSG, event.getPlayer().getName()));
    }
}
