package datta.core.content.utils.build;

import datta.core.content.utils.build.consts.Cuboid;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public class BuildUtils {

    public static void set(Cuboid cuboid, Material material){
        cuboid.blockList().forEach(block -> block.setType(material));
    }

    public static void walls(Cuboid cuboid, Material material) {
        List<Block> wallBlocks = cuboid.getWallBlocks();
        wallBlocks.forEach(block -> block.setType(material));
    }

    public static void replace(Cuboid cuboid, Material replaceMaterial, Material newMaterial) {
        for (Block block : cuboid.blockList()) {
            if (block.getType() == replaceMaterial) {
                block.setType(newMaterial);
            }
        }
    }
}