package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpallCMD extends BaseCommand {

    @CommandPermission("spreenstudios.tpall")
    @CommandAlias("tpall")
    public void tpall(Player player) {
        for (Player t : Bukkit.getOnlinePlayers()) {
            if (t != player) {
                t.teleport(player);
            }
        }

        SenderUtil.sendMessage(player, "%core_prefix% &eTodos los jugadores fueron teletransportados hacia ti.");
    }
}
