package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import datta.core.content.utils.EventUtils;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandPermission("spreenstudios.colors")
@CommandAlias("playercolor|colorplayer")
public class ColorCMD extends BaseCommand {

    @CommandCompletion(" &a|&c")
    @Subcommand("add")
    public void add(CommandSender sender, OnlinePlayer onlinePlayer, String color) {
        Player target = onlinePlayer.getPlayer();
        EventUtils.addPlayerColor(target, color);
        SenderUtil.sendMessage(sender, "%core_prefix% &eSe agrego a " + target.getName() + " la lista de colores con &7(" + color + "•&7)");
    }

    @CommandCompletion("&a|&c")
    @Subcommand("addall")
    public void addall(CommandSender sender, String color) {
        for (Player target : Bukkit.getOnlinePlayers()) {

            EventUtils.addPlayerColor(target, color);
        }
        SenderUtil.sendMessage(sender, "%core_prefix% &eSe agregaron a todos los jugadores de la lista de colores con &7(" + color + "•&7)");
    }

    @Subcommand("remove")
    public void remove(CommandSender sender, OnlinePlayer onlinePlayer) {
        Player target = onlinePlayer.getPlayer();
        EventUtils.removePlayerColor(target);
        SenderUtil.sendMessage(sender, "%core_prefix% &eSe quito a " + target.getName() + " de la lista de colores.");
    }

    @Subcommand("removeall")
    public void clear(CommandSender sender) {
        EventUtils.clearCOLORS();
        SenderUtil.sendMessage(sender, "%core_prefix% &eSe quitaron los colores de todos los jugadore.");
    }
}