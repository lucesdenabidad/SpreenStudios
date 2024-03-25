package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import datta.core.content.utils.EventUtils;
import org.bukkit.entity.Player;

public class EliminateCMD extends BaseCommand {
    @CommandCompletion(" true|false")
    @CommandPermission("spreenstudios.eliminate")
    @CommandAlias("eliminate|removeplayer")
    public void eliminate(Player player, OnlinePlayer onlinePlayer, boolean kick) {
        EventUtils.eliminate(onlinePlayer.getPlayer(), kick);
    }
}
