package datta.core.games;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import datta.core.menus.GameMenu;
import org.bukkit.entity.Player;


@CommandPermission("spreenstudios.games")
@CommandAlias("games")
public class GamesCommand extends BaseCommand {

    @HelpCommand
    public void helpCmd(Player sender){
        GameMenu.open(sender);
    }
}
