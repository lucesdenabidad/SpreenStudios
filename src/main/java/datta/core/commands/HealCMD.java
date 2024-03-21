package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.entity.Player;

import static datta.core.content.utils.EventUtils.heal;

public class HealCMD extends BaseCommand {

    @CommandPermission("spreenstudios.heal")
    @CommandAlias("heal")
    public void healCMD(Player player, @Optional OnlinePlayer onlinePlayer){
        if (onlinePlayer == null){
            heal(player);
        }else{
            Player target = onlinePlayer.getPlayer();
            heal(target);
        }
    }
}
