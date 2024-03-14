

/* Decompiler 21ms, total 169ms, lines 80 */
package datta.core.paper.utilities.services;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class WorldEditService {
   public static Region getWorldEditSelection(Player player) {
      try {
         WorldEditPlugin worldEditPlugin = getWorldEditPlugin();
         if (worldEditPlugin != null && worldEditPlugin.isEnabled()) {
            return worldEditPlugin.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
         } else {
            throw new IllegalStateException("WorldEdit no está habilitado. Instálalo y actívalo para usar esta función.");
         }
      } catch (IncompleteRegionException var2) {
         player.sendMessage("Error: Debes seleccionar dos puntos con WorldEdit para crear un área.");
         return null;
      } catch (Exception var3) {
         player.sendMessage("Error al obtener la selección del jugador. Consulta la consola para más detalles.");
         return null;
      }
   }

   private static WorldEditPlugin getWorldEditPlugin() {
      return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
   }

   public static void fill(Location pos1, Location pos2, Material material) {
      World world = pos1.getWorld();
      int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
      int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
      int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
      int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
      int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
      int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

      for (int x = minX; x <= maxX; ++x) {
         for (int y = minY; y <= maxY; ++y) {
            for (int z = minZ; z <= maxZ; ++z) {
               Location blockLocation = new Location(world, (double) x, (double) y, (double) z);
               Block block = blockLocation.getBlock();
               block.setType(material);
            }
         }
      }

   }

   public static void replace(Location pos1, Location pos2, Material material, boolean bypassAir) {
      World world = pos1.getWorld();
      int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
      int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
      int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
      int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
      int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
      int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

      for (int x = minX; x <= maxX; ++x) {
         for (int y = minY; y <= maxY; ++y) {
            for (int z = minZ; z <= maxZ; ++z) {
               Location blockLocation = new Location(world, (double) x, (double) y, (double) z);
               Block block = blockLocation.getBlock();
               if ((bypassAir || block.getType() != Material.AIR) && block.getType() != Material.TNT) {
                  block.setType(material);
               }
            }
         }
      }
   }

   public static void cylinder(Location base, int radius, int height, Material material) {
      World world = base.getWorld();
      int baseX = base.getBlockX();
      int baseY = base.getBlockY();
      int baseZ = base.getBlockZ();

      for (int y = baseY; y < baseY + height; y++) {
         for (int x = baseX - radius; x <= baseX + radius; x++) {
            for (int z = baseZ - radius; z <= baseZ + radius; z++) {
               if (Math.pow(x - baseX, 2) + Math.pow(z - baseZ, 2) <= Math.pow(radius, 2)) {
                  Location blockLocation = new Location(world, x, y, z);
                  Block block = blockLocation.getBlock();

                  block.setType(material);
               }
            }
         }
      }
   }

   public static boolean createCylinder(Location location, double radius, int height, Material block, boolean filled) {
      World w = location.getWorld();
      int x = location.getBlockX();
      int y = location.getBlockY();
      int z = location.getBlockZ();

      WorldEditPlugin we = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
      if (we == null) {
         return false;
      }

      com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(w);
      EditSession es = we.getWorldEdit().getEditSessionFactory().getEditSession(world, -1);
      try {
         es.enableQueue();
         com.sk89q.worldedit.world.block.BlockState blockState = BukkitAdapter.adapt(block.createBlockData());
         es.makeCylinder(BlockVector3.at(x, y, z), blockState, radius, height, filled);
         es.flushSession();
      } catch (MaxChangedBlocksException ignored) {
         return false;
      } finally {
         es.close();
      }
      return true;
   }

   public static void createPattern(Location pos1, Location pos2, List<Material> materialList) {
      Random random = new Random();

      int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
      int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
      int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
      int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
      int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

/*
        minX += 2 - (minX % 2);
        minZ += 2 - (minZ % 2);
*/

      for (int x = minX; x <= maxX; x += 2) {
         for (int z = minZ; z <= maxZ; z += 2) {
            Location corner = new Location(pos1.getWorld(), x, minY, z);
            genPattern(corner, materialList.get(random.nextInt(materialList.size())));
         }
      }
   }

   public static void genPattern(Location corner, Material material) {
      World world = corner.getWorld();

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 2; j++) {
            Block block = world.getBlockAt(corner.getBlockX() + i, corner.getBlockY(), corner.getBlockZ() + j);
            block.setType(material);
         }
      }
   }
}