package datta.core.services.list;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import datta.core.Core;
import datta.core.services.Service;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScreenColorService extends Service {

    @Override
    public Core instance() {
        return Core.getInstance();
    }

    @Override
    public String name() {
        return "screencolor";
    }

    @Override
    public String[] info() {
        return new String[0];
    }

    @Override
    public void onLoad() {
        register(true, true);
    }

    @Override
    public void onUnload() {
        register(true, true);
    }

    String sb = "㐀";

    @CommandCompletion(" <fade> <duration> @players|all")
    @CommandPermission("spreenstudios.screencolor")
    @CommandAlias("screen|showscreen|screencolor")
    public void cmd(CommandSender sender, ChatColor color, int fade, int duration, String target) {

        if (target.equalsIgnoreCase("all")) {
            showAll(color, fade, duration);
        } else {
            Player player = Bukkit.getPlayer(target);

            if (player == null) {
                SenderUtil.sendMessage(player, "%core_prefix% &cEl jugador especificado no esta en línea.");
                return;
            }

            show(color, fade, duration, Bukkit.getPlayer(target));
        }
    }


    public boolean stringIsChatColor(String input) {
        try {
            ChatColor color = ChatColor.valueOf(input.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    public void showAll(String color, int fade, int duration) {
        if (stringIsChatColor(color)){
            color = ChatColor.valueOf(color).name();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendTitle(player, color + sb, "", fade, duration);
        }
    }

    public void showAll(ChatColor color, int fade, int duration) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendTitle(player, color + sb, "", fade, duration);
        }
    }


    public void show(String color, int fade, int duration, Player player) {
        if (stringIsChatColor(color)) {
            color = ChatColor.valueOf(color).name();
        }

        SenderUtil.sendTitle(player, color + sb, "", fade, duration);
    }

    public void show(ChatColor color, int fade, int duration, Player player) {
        SenderUtil.sendTitle(player, color + sb , "", fade, duration);
    }
}
