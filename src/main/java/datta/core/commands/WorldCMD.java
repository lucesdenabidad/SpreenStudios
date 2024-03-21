package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class WorldCMD extends BaseCommand {

    @CommandPermission("spreenstudios.world")
    @CommandAlias("day")
    public void dayCMD(CommandSender sender) {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(1000);
            world.setStorm(false);
        }

        SenderUtil.sendMessage(sender, "&aHiciste de dia en todos los mundos");
    }

    @CommandPermission("spreenstudios.world")
    @CommandAlias("night")
    public void nightCMD(CommandSender sender) {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(13000);
        }

        SenderUtil.sendMessage(sender, "&aHiciste de dia en todos los mundos");
    }
}