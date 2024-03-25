package datta.core.games.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.menus.GameMenu;
import datta.core.utils.SenderUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandPermission("spreenstudios.games")
@CommandAlias("games")
public class GamesCommand extends BaseCommand {

    @HelpCommand
    public void helpCmd(Player sender){
        GameMenu.open(sender);
    }

    @Subcommand("stop")
    public void stop(CommandSender sender){
        Core.getInstance().stopGames();
        SenderUtil.sendMessage(sender, "%core_prefix% &fSe cancelaron todos los juegos.");
    }
}
