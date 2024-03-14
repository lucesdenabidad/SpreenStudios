package datta.core.paper.utilities;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import static datta.core.paper.utilities.Color.format;

public class BukkitIteratorPlayers {

    private static final float DEFAULT_PITCH = 1.0f;
    private static final float DEFAULT_VOLUME = 1.0f;

    public static void sendTitle(String title, String subtitle) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(format(title), format(subtitle));
        }
        sendSound(Sound.ENTITY_ITEM_PICKUP);
    }

    public static void sendTitle(String title, String subtitle, int fade, int duration) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(format(title), format(subtitle),fade,duration,fade);
        }
    }

    public static void sendSound(Sound sound) {
        sendSound(sound, DEFAULT_PITCH, DEFAULT_VOLUME);
    }

    public static void sendSound(Sound sound, float volume, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public static void teleportAll(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(location);
        }
    }

    public static void sendMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(format(player, message));
        }
    }

    public static void sendActionBar(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(format(player, message));
        }
    }

    public static void clearInventory() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
        }
    }

    public static void giveItem(ItemStack itemStack) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(itemStack);
        }
    }

    public static void heal() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
        }
    }

    public static void setFly(boolean enable) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setAllowFlight(enable);
            player.setFlying(enable);
        }
    }

    public static void kick(String reason) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(format(reason));
        }
    }

    public static void strikeLightning() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getWorld().strikeLightning(player.getLocation());
        }
    }


    public static void giveEffect(PotionEffectType type, int duration, int amplifier) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(type, duration, amplifier));
        }
    }

    public static void setGamemode(GameMode g) {
         for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(g);
        }
    }
}