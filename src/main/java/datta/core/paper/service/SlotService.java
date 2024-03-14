package datta.core.paper.service;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import datta.core.paper.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import static datta.core.paper.utilities.Utils.send;

public class SlotService extends BaseCommand implements Listener {
    public static int slots = Core.getInstance().getServer().getMaxPlayers();

    @CommandPermission("core.admin")
    @CommandAlias("setslots")
    public void setslots(CommandSender sender, int slot) {
        slots = slot;
        Bukkit.getServer().setMaxPlayers(slot);

        send(sender, "&a&lEvento &8> &fSlots cambiados a "+slot+".");
    }

    @EventHandler
    public void list(ServerListPingEvent e){
        e.setMaxPlayers(slots);
    }
}
