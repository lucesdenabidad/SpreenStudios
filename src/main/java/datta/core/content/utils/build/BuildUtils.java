package datta.core.content.utils.build;

import com.fastasyncworldedit.bukkit.util.BukkitTaskManager;
import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import datta.core.content.utils.build.consts.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
                throw new RuntimeException(e);
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

    public static void schematic(Location pasteAt, String schematicName) {
        BukkitTaskManager.taskManager().async(() -> {
            File schematicFile = new File(Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit").getDataFolder(), "schematics/" + schematicName);

            if (!schematicFile.exists()) {
                System.err.println("El archivo " + schematicName + " no existe en la carpeta de esquemas del plugin.");
                return;
            }

            World world = BukkitAdapter.adapt(pasteAt.getWorld());
            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            if (format == null) {
                System.err.println("Formato de archivo no compatible para el archivo " + schematicName);
                return;
            }

            try (FileInputStream fis = new FileInputStream(schematicFile)) {
                ClipboardReader reader = format.getReader(fis);
                Clipboard clipboard = reader.read();

                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BukkitAdapter.asBlockVector(pasteAt))
                            .ignoreAirBlocks(true)
                            .build();

                    Operations.complete(operation);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
