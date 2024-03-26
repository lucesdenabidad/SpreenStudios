package datta.core.events.spreenstudios;

import datta.core.weapons.Stick;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class StickInteractEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public Stick stick;
    public Player executor;
    public Player target;

    public StickInteractEvent(Stick stick, Player executor, Player target){
        this.stick = stick;
        this.executor = executor;
        this.target = target;
    }

    public ItemStack getItem(){
        return stick.item();
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}