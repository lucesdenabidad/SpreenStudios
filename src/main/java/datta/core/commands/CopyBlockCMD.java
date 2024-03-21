package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import static datta.core.content.builders.ColorBuilder.color;

@CommandPermission("spreenstudios.copy")
@CommandAlias("cp")
public class CopyBlockCMD extends BaseCommand {

    @CommandCompletion("")
    @Subcommand("targetblock")
    public void copyTargetBlock(Player player) {
        Block targetBlock = player.getTargetBlock(5);
        Location location = targetBlock.getLocation();

        int X = location.getBlockX();
        int Y = location.getBlockY();
        int Z = location.getBlockZ();
        String copy = X + " " + Y + " " + Z;

        player.sendMessage(copy);
    }


    /**
     * Sends a clickable message to a player that runs a command when clicked.
     * @param message The clickable message!
     * @param command The command without the slash to make the user perform.
     * @param player player to send to.
     */
    public void sendClickableCommand(Player player, String message, String command) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(color(message)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, command));
        player.spigot().sendMessage(component);
    }
}