package datta.core.weapons;

import co.aikar.commands.BaseCommand;
import datta.core.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Weapon extends BaseCommand implements Listener {
    public abstract String name();

    public abstract ItemStack item();

    public abstract void getItem(Player player);

    public abstract void interactAtEntity(PlayerInteractAtEntityEvent event);

    public abstract void interactAtEntity(PlayerInteractEvent event);

    public static void register(Weapon weapon) {
        Core.getInstance().commandManager.registerCommand(weapon);
        Core.getInstance().getServer().getPluginManager().registerEvents(weapon, Core.getInstance());
    }
}
