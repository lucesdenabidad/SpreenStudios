package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import datta.core.Core;
import datta.core.utils.SenderUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static datta.core.content.utils.EventUtils.formatBoolean;


@CommandPermission("spreenstudios.info")
@CommandAlias("info|sc|oplog")
public class InfoCMD extends BaseCommand {


    @Default
    public void sendInfo(CommandSender sender, String info) {
        Core.info("&e" + sender.getName() + "&7: &r" + info);
    }


    @Subcommand("toggle")
    public static void toggle(Player player) {
        if (Core.disableLog.contains(player)) {
            Core.disableLog.remove(player);
        } else {
            Core.disableLog.add(player);
        }


        String actual = formatBoolean(!Core.disableLog.contains(player), "Activado", "Deshabilitado");
        SenderUtil.sendMessage(player, "%core_prefix% &eSe ha alternado el buzón de información de administración para ti. Ahora está " + actual + ".");
    }

    @Subcommand("toggle")
    public static void toggle(CommandSender sender, OnlinePlayer onlinePlayer) {
        Player player = onlinePlayer.getPlayer();

        if (Core.disableLog.contains(player)) {
            Core.disableLog.remove(player);
        } else {
            Core.disableLog.add(player);
        }

        String actual = formatBoolean(!Core.disableLog.contains(player), "Activado", "Deshabilitado");

        SenderUtil.sendMessage(player, "%core_prefix% &e"+sender.getName()+" te ha alternado el registro de operador, Ahora el estado está en " + actual + ".");
        SenderUtil.sendMessage(sender, "%core_prefix% &eSe ha alternado el buzón de información de administración para " + player.getName() + ". Ahora está " + actual + ".");
    }
}
