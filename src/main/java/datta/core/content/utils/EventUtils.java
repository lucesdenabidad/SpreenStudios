package datta.core.content.utils;

import datta.core.content.CoreTask;
import datta.core.content.builders.FireworkBuilder;
import datta.core.utils.SenderUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

import static datta.core.content.builders.ColorBuilder.color;

public class EventUtils {

    public static void eliminate(Player player, boolean kick) {
        World world = player.getWorld();


        world.spawnParticle(Particle.SMALL_FLAME, player.getLocation(), 10, 0, 3, 0, 1);

        FireworkBuilder fireworkBuilder = new FireworkBuilder()
                .colors(Color.RED, Color.WHITE)
                .power(1);

        fireworkBuilder.spawn(player.getLocation(), player);


        CoreTask.runTask(() -> {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 1);

            if (kick) {
                player.kickPlayer(color("&c¡Gracias por jugar!"));
            } else {
                player.setHealth(0);
            }


            for (Player t : Bukkit.getOnlinePlayers()) {
                SenderUtil.sendMessage(t, "%core_prefix% &c" + player.getName() + "&f fue eliminado del evento.");
            }
        }, 25L);
    }


    public static String formatBoolean(boolean value, String isTrue, String isFalse) {
        return value ? color(isTrue) : color(isFalse);
    }

    public static Location genLocationInLocations(Location pos1, Location pos2) {
        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        double randomX = ThreadLocalRandom.current().nextDouble(minX, maxX + 1);
        double randomY = ThreadLocalRandom.current().nextDouble(minY, maxY + 1);
        double randomZ = ThreadLocalRandom.current().nextDouble(minZ, maxZ + 1);

        return new Location(world, randomX, randomY, randomZ).toCenterLocation();
    }

    public static String fix(String text) {
        if (text == null || text.isEmpty()) {
            return "La cadena está vacía.";
        }

        text = text.toLowerCase();

        char firstChar = Character.toUpperCase(text.charAt(0));
        return firstChar + text.substring(1);
    }

    public static void heal(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
    }
}