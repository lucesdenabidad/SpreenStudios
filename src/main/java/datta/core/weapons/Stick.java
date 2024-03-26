package datta.core.weapons;

import co.aikar.commands.BaseCommand;
import datta.core.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Stick extends BaseCommand implements Listener {

    public static List<Stick> list = new ArrayList<>();

    public abstract String name();

    public abstract ItemStack item();

    public abstract void getItem(Player player);


    public abstract void interactAtEntity(PlayerInteractAtEntityEvent event);

    public abstract void interactAtEntity(PlayerInteractEvent event);

    public static void registerStick(Stick stick) {
        list.add(stick);

        Core.getInstance().commandManager.registerCommand(stick);
        Core.getInstance().getServer().getPluginManager().registerEvents(stick, Core.getInstance());
    }
}
