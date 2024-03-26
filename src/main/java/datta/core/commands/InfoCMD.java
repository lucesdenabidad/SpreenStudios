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
@CommandAlias("info|sc|oplog|log")
public class InfoCMD extends BaseCommand {


    @Default
    public void sendInfo(CommandSender sender, String info) {
        Core.info("&e" + sender.getName() + "&7: &r" + info);
    }


    public static boolean get(Player player) {
        return Core.disableLog.contains(player);
    }

    public static void set(Player player, boolean enabled) {
        if (enabled) {
            Core.disableLog.remove(player);
        } else {
            if (!Core.disableLog.contains(player)) {
                Core.disableLog.add(player);
            }
        }
    }

    @Subcommand("toggle")
    public static void toggle(Player player, boolean value) {
        set(player, value);
        String actual = formatBoolean(value, "Activado", "Deshabilitado");
        SenderUtil.sendMessage(player, "%core_prefix% &eSe ha alternado el buzón de información de administración para ti. Ahora está " + actual + ".");
    }

    @Subcommand("toggle")
    public static void toggle(CommandSender sender, OnlinePlayer onlinePlayer, boolean value) {
        Player player = onlinePlayer.getPlayer();

        set(player, value);
        String actual = formatBoolean(value, "Activado", "Deshabilitado");

        SenderUtil.sendMessage(player, "%core_prefix% &e" + sender.getName() + " te ha alternado el registro de operador. Ahora el estado está en " + actual + ".");
        SenderUtil.sendMessage(sender, "%core_prefix% &eSe ha alternado el buzón de información de administración para " + player.getName() + ". Ahora está " + actual + ".");
    }
}
