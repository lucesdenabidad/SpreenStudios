package datta.core.games.games;

import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.games.Game;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.builders.MenuBuilder.slot;

public class SillitasGame extends Game {
    @Override
    public String name() {
        return "Sillitas Musicales";
    }

    @Override
    public Location spawn() {
        return stringToLocation("-71 100 358");
    }

    @Override
    public String[] gameinfo() {
        return new String[]{"sillitas hd 3d rgb", ""};
    }

    @Override
    public void start() {
        game(() -> {
            shuffleTask();
        });
    }

    @Subcommand("sillas end")
    @Override
    public void end() {
        end(() ->{

        if (task != null){
        task.cancel();
        }
        });
    }

    @Override
    public List<String> scoreboard() {
        return null;
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return List.of(

                new MenuBuilder.MenuItem(new ItemBuilder(Material.WHITE_WOOL, "&aCambiar patron").build(), () -> {
                    shuffle(materials(), 2);
                }),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.OAK_STAIRS, "&eGenerar Silla").build(), this::spawnChair),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.CLOCK, "&aIniciar Ronda").build(), this::game),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.CLOCK, "&cEliminar jugadores").build(), this::removePlayers)
        );
    }

    @Override
    public ItemStack menuItem() {
        return new ItemBuilder(Material.OAK_STAIRS, "&bSillitas Musicales")
                .addLore("", "")
                .addLore("", "&a• Clic para ver")
                .build();
    }

    @Override
    public int menuSlot() {
        return slot(2, 2);
    }

    BukkitTask task;

    Location pos1 = stringToLocation("-77 98 362");
    Location pos2 = stringToLocation("-92 98 354");


    public void game() {

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            spawnChair();
        }
    }

    public void spawnChair() {
        Location location = genLocationInLocations(pos1.clone().add(0,1,0), pos2.clone().add(0,1,0));

        Block block = location.getBlock();
        block.setType(Material.DRIED_KELP_BLOCK);
    }

    public void removePlayers(){
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isInsideVehicle()){
                SenderUtil.sendMessage(p, "&c• &fSin silla!");
            } else {
                SenderUtil.sendMessage(p, "&a• &fCon silla!");
            }
        }
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
        Location randomLocation = new Location(world, randomX, randomY, randomZ);

        if (isEmptyLocation(randomLocation)) {
            return randomLocation;
        } else {
            return null;
        }
    }

    private static boolean isEmptyLocation(Location location) {
        Block block = location.getBlock();
        return block.getType() == Material.AIR;
    }


    public void shuffleTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                shuffle(materials(), 2);
            }
        }.runTaskTimer(Core.getInstance(), 0, 20L);
    }


    public void shuffle(List<Material> materials, int pattern) {

        int y = pos1.getBlockY();

        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        List<Location> locations = new ArrayList<>();
        for (int x = minX; x <= maxX; x += pattern) {
            for (int z = minZ; z <= maxZ; z += pattern) {
                locations.add(new Location(world, x, y, z));
            }
        }

        Collections.shuffle(locations);

        for (Location location : locations) {
            Material material = materials.get((int) (Math.random() * materials.size()));
            assignMaterial(location, material, pattern);
        }
    }

    public List<Material> materials() {

        List<Material> list = List.of(
                Material.WHITE_WOOL,
                Material.ORANGE_WOOL,
                Material.MAGENTA_WOOL,
                Material.LIGHT_BLUE_WOOL,
                Material.YELLOW_WOOL,
                Material.LIME_WOOL,
                Material.PINK_WOOL,
                Material.GRAY_WOOL,
                Material.LIGHT_GRAY_WOOL,
                Material.CYAN_WOOL,
                Material.PURPLE_WOOL,
                Material.BLUE_WOOL,
                Material.BROWN_WOOL,
                Material.GREEN_WOOL,
                Material.RED_WOOL,
                Material.BLACK_WOOL
        );
        return list;
    }


    private void assignMaterial(Location location, Material material, int pattern) {
        World world = location.getWorld();
        for (int x = 0; x < pattern; x++) {
            for (int z = 0; z < pattern; z++) {
                world.getBlockAt(location.clone().add(x, 0, z)).setType(material);
            }
        }
    }


}
