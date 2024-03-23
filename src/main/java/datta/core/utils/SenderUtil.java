package datta.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static datta.core.content.builders.ColorBuilder.color;

public class SenderUtil {

    public static void sendMessage(CommandSender sender, String... message) {
        for (String s : message) {
            sender.sendMessage(color(sender, s));
        }
    }

    public static void sendActionbar(Player sender, String message) {
        sender.sendActionBar(color(sender, message));
    }

    public static void sendActionbar(Player sender, String message, Sound sound) {
        sendActionbar(sender.getPlayer(), message);
        sendSound(sender, sound, 1, 1);
    }

    public static void sendTitle(Player sender, String title, String subtitle) {
        sender.sendTitle(color(title), color(subtitle));
    }

    public static void sendTitle(Player sender, String title, String subtitle, int fade, int duration) {
        sender.sendTitle(color(title), color(subtitle), fade, duration, fade);
    }

    public static void sendSound(Player sender, Sound sound, float volume, float pitch) {
        sender.playSound(sender.getLocation(), sound, volume, pitch);
    }


    public static void sendBroadcast(String... message) {
        for (String s : message) {
            Bukkit.broadcastMessage(color(s));
        }
    }
}