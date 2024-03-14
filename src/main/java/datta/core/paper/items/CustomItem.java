package datta.core.paper.items;

import co.aikar.commands.BaseCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class CustomItem extends BaseCommand implements Listener {


    public abstract ItemStack itemStack();
    public abstract void handleCommand(Player player, String[] args);
}