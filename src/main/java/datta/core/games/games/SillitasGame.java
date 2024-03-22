package datta.core.games.games;

import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.WorldEditService;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventUtils;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.content.utils.EventPlayer;
import datta.core.games.Game;
import datta.core.services.list.SitService;
import datta.core.utils.SenderUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
        return stringToLocation("552 3 449");
    }

    @Override
    public String[] gameinfo() {
        return new String[]{"sillitas hd 3d rgb", ""};
    }

    @Override
    public void start() {
        game(() -> {
        });
    }

    @Subcommand("sillas end")
    @Override
    public void end() {
        end(() -> {
            playOrStopMusic(defaultStatus);
            removeChairs();
        });
    }

    @Override
    public List<String> scoreboard() {
        return new ArrayList<>(List.of(
                "",
                "&7 Cuando suene",
                "&7 la musica debes",
                "&7 subirte a una silla",
                "&7 de lo contrario",
                "&7 seras eliminado.",
                ""
        ));
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return List.of(

                new MenuBuilder.MenuItem(new ItemBuilder(Material.WHITE_WOOL, "&aCambiar patron").build(), () -> {
                    shuffle(materials(), 2);
                }),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.LIGHTNING_ROD, "&eGenerar Silla").build(), this::spawnChair),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.LIME_DYE, "&aIniciar Ronda").build(), this::game),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.RED_DYE, "&cEliminar jugadores").build(), this::removePlayers),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.MUSIC_DISC_5, "&eMusica").build(), () -> {
                    playOrStopMusic(!defaultStatus);
                }),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.NETHER_STAR, "&eObtener items").build(), () -> {
                    getItems(player);
                })

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


    Location pos1 = stringToLocation("558 3 444");
    Location pos2 = stringToLocation("546 3 454");


    public void game() {
        for (int i = 0; i < Bukkit.getOnlinePlayers().size() - 1; i++) {
            spawnChair();
        }
    }

    @EventHandler
    public void interacte(PlayerInteractEvent event) {
        if (event.hasBlock()){
            Player player = event.getPlayer();
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock.getType().equals(Material.LIGHTNING_ROD)){
                if (!defaultStatus){
                    player.sendMessage("&cLa musica esta sonando!");
                    event.setCancelled(true);
                }
            }
        }
    }

    List<MenuBuilder.MenuItem> menuItems = new ArrayList<>(List.of(
            new MenuBuilder.MenuItem(new ItemBuilder(Material.LIME_DYE, "&aPrender Musica").build(), () -> {
                playOrStopMusic(true);
            }),

            new MenuBuilder.MenuItem(new ItemBuilder(Material.RED_DYE, "&cApagar Musica").build(), () -> {
                playOrStopMusic(false);
            }),

            new MenuBuilder.MenuItem(new ItemBuilder(Material.ARMOR_STAND, "&aColocar sillas").build(), this::game),
            new MenuBuilder.MenuItem(new ItemBuilder(Material.TNT_MINECART, "&cQuitar sillas").build(), this::removeChairs),
            new MenuBuilder.MenuItem(new ItemBuilder(Material.BARRIER, "&cEliminar jugadores").build(), this::removePlayers)
    ));

    public void getItems(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (MenuBuilder.MenuItem menuItem : menuItems) {
            inventory.addItem(menuItem.getItemStack());
        }

        ItemStack itemStack = new ItemBuilder(Material.LIGHTNING_ROD, "&eSilla").build();
        inventory.addItem(itemStack);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.hasItem()){
            ItemStack item = event.getItem();

            for (MenuBuilder.MenuItem menuItem : menuItems) {
                if (item.isSimilar(menuItem.getItemStack())){
                    menuItem.executeAction(player);
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    public void removeChairs() {
        Cuboid cuboid = new Cuboid("546 3 454 558 3 444");
        WorldEditService.replace(cuboid, Material.LIGHTNING_ROD, Material.AIR);
    }

    public void spawnChair() {
        Cuboid cuboid = new Cuboid("546 3 454 558 3 444");
        Location location = genLocationInLocations(cuboid.getPoint1(), cuboid.getPoint2());

        Block block = location.getBlock();
        block.setType(Material.LIGHTNING_ROD);

        for (Player t : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendSound(t, Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 2);
        }
    }

    public void removePlayers() {
        SitService sitService = (SitService) Core.getInstance().commandService.serviceFromName("sit");
        for (Player p : Bukkit.getOnlinePlayers()) {
            EventPlayer e = new EventPlayer(p);
            if (e.isStaff()) return;;

            if (!p.isInsideVehicle()) {
                EventUtils.eliminate(p, true);
            } else {
                Block block = p.getLocation().getBlock();
                block.setType(Material.AIR);
                p.leaveVehicle();
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
        return block.getType() == Material.AIR || block.getType() == Material.LIGHT;
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

    boolean defaultStatus = false;
    public void playOrStopMusic(boolean isPlayingSound) {
        Sound sound = Sound.MUSIC_DISC_WAIT;

        if (isPlayingSound) {
            for (Player player : Bukkit.getOnlinePlayers())
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);

        } else {
            for (Player player : Bukkit.getOnlinePlayers())
                player.stopSound(sound);
        }

        defaultStatus = isPlayingSound;
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
