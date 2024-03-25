package datta.core.content.utils.build;

import com.fastasyncworldedit.bukkit.util.BukkitTaskManager;
import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BaseBlock;
import datta.core.content.utils.build.consts.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public class BuildUtils {

    public static void set(Cuboid cuboid, Material material) {
        BukkitTaskManager.taskManager().async(() -> {
            Location min = cuboid.getPoint1();
            Location max = cuboid.getPoint2();

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(min.getWorld().getName()));) {
                Region region = new CuboidRegion(BlockVector3.at(min.getX(), min.getY(), min.getZ()), BlockVector3.at(max.getX(), max.getY(), max.getZ()));
                Pattern pattern = new BaseBlock(BukkitAdapter.adapt(material.createBlockData()));
                editSession.setBlocks(region, pattern);
            } catch (Exception e) {

            }
        });
    }

    public static void replace(Cuboid cuboid, Material replaceMaterial, Material newMaterial) {
        BukkitTaskManager.taskManager().async(() -> {
            Location min = cuboid.getPoint1();
            Location max = cuboid.getPoint2();

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(min.getWorld().getName()));) {
                Region region = new CuboidRegion(BlockVector3.at(min.getX(), min.getY(), min.getZ()), BlockVector3.at(max.getX(), max.getY(), max.getZ()));
                Pattern pattern = new BaseBlock(BukkitAdapter.adapt(newMaterial.createBlockData()));
                Mask mask = new BlockMask(editSession.getExtent(), new BaseBlock(BukkitAdapter.adapt(replaceMaterial.createBlockData())));
                editSession.replaceBlocks(region, mask, pattern);
            } catch (Exception e) {

            }
        });
    }

    public static void walls(Cuboid cuboid, Material material) {
        List<Block> walls = cuboid.getWallBlocks();
        for (Block wall : walls) {
            wall.setType(material);
        }
    }
}

