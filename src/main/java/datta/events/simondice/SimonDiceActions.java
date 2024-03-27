package datta.events.simondice;

import datta.core.content.utils.build.BuildUtils;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static datta.core.Core.menuBuilder;
import static datta.core.content.builders.ColorBuilder.stringToLocation;

public class SimonDiceActions {


    // # const
    private static Cuboid map = new Cuboid("35 100 59 -33 117 216");

    private static Location center = stringToLocation("1 101 134");

    public static void clearMap(Player player) {
        BuildUtils.set(map, Material.AIR);
        SenderUtil.sendActionbar(player, "&a(!) &fLimpiando mapa de Simón Dice", Sound.BLOCK_NOTE_BLOCK_BANJO);
        SenderUtil.sendMessage(player, "%core_prefix% &fLimpiaste el mapa de Simón Dice con éxito");
    }

    public static void minigames(Player player){
        menuBuilder.createMenu(player, "Simón Dice > Minijuegos", 9 * 5, false);
        menuBuilder.setContents(player, () ->{

        });
    }

    public static void playersToLine(Player player) {
        Cuboid cuboid = new Cuboid("1 99 85 1 99 183");
        Location point1 = cuboid.getPoint1();
        Location point2 = cuboid.getPoint2();
        Location center = cuboid.getCenter();

        int z = center.add(0,1,0).getBlockY();
        int x1 = point1.getBlockX();
        int z1 = point1.getBlockZ();
        int x2 = point2.getBlockX();
        int z2 = point2.getBlockZ();

        int minX = Math.min(x1, x2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2);
        int maxZ = Math.max(z1, z2);

        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();

        int distance = Math.max(maxX - minX, maxZ - minZ);

        int teleportX = minX + (maxX - minX) / 2;
        int teleportZ = minZ + (maxZ - minZ) / 2;

        for (Player target : Bukkit.getOnlinePlayers()) {
            Location targetLocation = new Location(player.getWorld(), teleportX, z, teleportZ);
            targetLocation = targetLocation.toCenterLocation();

            target.teleport(targetLocation);
            teleportX += 2; // Incrementa la coordenada X para mover al siguiente jugador a la derecha
            if (teleportX > maxX) {
                // Cuando alcanzamos el borde derecho, reiniciamos X y movemos hacia abajo
                teleportX = minX;
                teleportZ += 2; // Incrementa la coordenada Z para mover al siguiente jugador hacia abajo
            }
        }
    }

    public static void sticks(Player player) {
        player.performCommand("kickstick");
        player.performCommand("punchstick");
        player.performCommand("voicestick");
        player.performCommand("colorstick");
    }
}