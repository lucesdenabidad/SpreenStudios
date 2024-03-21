package datta.core.content.portals;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEntryPortalEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Portal portal;

    public PlayerEntryPortalEvent(Player player, Portal portal) {
        this.player = player;
        this.portal = portal;
    }

    public Player getPlayer() {
        return player;
    }

    public Portal getPortal() {
        return portal;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
