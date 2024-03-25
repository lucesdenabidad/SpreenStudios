package datta.core.content.utils;

import datta.core.Core;
import datta.core.content.builders.FireworkBuilder;
import datta.core.utils.SenderUtil;
import org.bukkit.*;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static datta.core.content.builders.ColorBuilder.color;

public class EventUtils {
    public static Map<Player, String> COLORS = new HashMap<>();

    public static void RemoveOrAddPlayerFromColor(Player player, String color) {
        if (COLORS.containsKey(player)) {
            COLORS.remove(player);
        } else {
            COLORS.put(player, color);
        }
    }

    public static void addPlayerColor(Player player, String color) {
        COLORS.put(player, color);
    }

    public static void removePlayerColor(Player player) {
        COLORS.remove(player);
    }

    public static void clearCOLORS() {
        COLORS.clear();
    }

    public static void removePlayersIsNotList(List<Player> list, boolean kick) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            if (isStaff(onlinePlayer)) return;
            if (!list.contains(onlinePlayer)) {
                eliminate(onlinePlayer, kick);
            }
        }
    }


    public static void eliminate(Player player, boolean kick) {
        World world = player.getWorld();

        if (isStaff(player)) return;

        FireworkBuilder fireworkBuilder = new FireworkBuilder()
                .colors(Color.RED, Color.WHITE)
                .power(1);

        fireworkBuilder.spawnAndDetonate(player.getLocation().clone().add(0, 1, 0));


        if (kick) {
            player.kickPlayer(color("&c¡Gracias por jugar!"));
        } else {
            player.setHealth(0);
        }

        for (Player t : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendActionbar(t, "&c(☠) " + player.getName() + "&f fue eliminado &7(%core_alive%/%core_endat%)");
        }
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

    public static void win(Player p) {
        World world = p.getWorld();

        for (Player t : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendTitle(t, "&a&l¡GANADOR!", "&8» &f¡&e" + p.getName() + " fue el ultimo en pie&f!&8«");
            SenderUtil.sendSound(t, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        }

        int launchDelay = 10;
        int time = 3;
        int[] timer = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (timer[0] == time) {
                    cancel();
                }

                timer[0]++;
                winEffect(p);
            }
        }.runTaskTimer(Core.getInstance(), launchDelay, 5L);
    }


    public static void winEffect(Player p) {
        int ending = 15;
        for (byte b = 0; ending > b; b++) {
            (new BukkitRunnable() {
                public void run() {
                    Location location = p.getLocation();
                    final Chicken chicken = spawnChicken(location, random(-0.5D, 0.5D), 1.5D, random(-0.5D, 0.5D));
                    chicken.getLocation().getWorld().playSound(chicken.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.0F, 1.0F);
                    new BukkitRunnable() {
                        public void run() {
                            if (chicken.isDead()) {
                                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1.0F);
                                chicken.getWorld().spawnParticle(Particle.REDSTONE, chicken.getLocation(), 10, 0.25F, 0.25F, 0.25F, dustOptions);
                                chicken.getLocation().getWorld().playSound(chicken.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0F, 1.0F);
                                cancel();
                            } else {
                                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1.0F);
                                chicken.getWorld().spawnParticle(Particle.REDSTONE, chicken.getLocation(), 10, 0.25F, 0.25F, 0.25F, dustOptions);
                            }
                        }
                    }.runTaskTimer(Core.getInstance(), 0L, 0L);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            chicken.remove();
                        }
                    }.runTaskLater(Core.getInstance(), 20L * 5);
                }
            }).runTaskLater(Core.getInstance(), (b * 10));
        }
    }

    public static double random(double paramDouble1, double paramDouble2) {
        return paramDouble1 + ThreadLocalRandom.current().nextDouble() * (paramDouble2 - paramDouble1);
    }


    public static Chicken spawnChicken(Location paramLocation, double paramDouble1, double paramDouble2, double paramDouble3) {
        Chicken chicken = paramLocation.getWorld().spawn(paramLocation, Chicken.class);
        chicken.setVelocity(new Vector(paramDouble1, paramDouble2, paramDouble3));
        return chicken;
    }

    public static void instantFirework(Location location){
        FireworkBuilder fireworkBuilder = new FireworkBuilder()
                .power(1)
                .colors(Color.LIME,Color.GREEN,Color.WHITE);

        fireworkBuilder.spawn(location);
    }

    public static boolean isStaff(Player t) {
        if (t.isOp()){
            return true;
        }
        return false;
    }
}