package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.google.gson.JsonObject;
import datta.core.Core;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

@CommandPermission("spreenstudios.visibility")
@CommandAlias("visible|visibility")
public class VisibilityCMD extends BaseCommand {

    @Subcommand("set")
    public void set(CommandSender sender, String arg) {
        boolean allow = arg.equalsIgnoreCase("true");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", allow);

        Bukkit.getOnlinePlayers().forEach(player ->{
            Core.getInstance().packetManager.sendGlobalPacket(player, "setrenderlock", jsonObject);
        });

        SenderUtil.sendMessage(sender, "&aSe cambio el bloqueo de visibilidad a "+arg);
    }
}