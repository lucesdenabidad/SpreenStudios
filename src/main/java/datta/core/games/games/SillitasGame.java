package datta.core.games.games;

import co.aikar.commands.annotation.Subcommand;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventPlayer;
import datta.core.content.utils.EventUtils;
import datta.core.content.utils.build.consts.Cuboid;
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

import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.utils.build.BuildUtils.replace;

public class SillitasGame extends Game {
    @Override
    public String name() {
        return "Sillitas Musicales";
    }

    @Override
    public Location spawn() {
        return stringToLocation("1 100 134");
    }

    @Override
    public void start() {
        game(() -> {
            for (Player t : Bukkit.getOnlinePlayers()) {
                SenderUtil.sendTitle(t, "&e&l¡ATENTO AL PRESENTADOR!", "&8» &f¡Toma atención a &eSpreen&f!&8«");
                SenderUtil.sendSound(t, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            }
        });
    }

    @Subcommand("sillas end")
    @Override
    public void end() {
        end(() -> {
            for (Player t : Bukkit.getOnlinePlayers()) {
                t.stopAllSounds();
            }
            removeChairs();
        });
    }

    @Override
    public List<String> scoreboard() {
        return new ArrayList<>();
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return List.of(

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
    public Material menuItem() {
        return Material.OAK_STAIRS;
    }

    public void game() {
        for (int i = 0; i < Bukkit.getOnlinePlayers().size() - 1; i++) {
            spawnChair();
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
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.hasItem()) {
            ItemStack item = event.getItem();

            for (MenuBuilder.MenuItem menuItem : menuItems) {
                if (item.isSimilar(menuItem.getItemStack())) {
                    menuItem.executeAction(player);
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    public void removeChairs() {
        Cuboid cuboid = new Cuboid("7 100 128 -5 100 140");
        replace(cuboid, Material.LIGHTNING_ROD, Material.AIR);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            onlinePlayer.leaveVehicle();
    }


    public void spawnChair() {
        List<String> allowedLocations = List.of("-5 100 140", "-2 100 140", "1 100 140", "4 100 140", "7 100 140", "7 100 137", "4 100 137", "1 100 137", "-2 100 137", "-5 100 137", "-5 100 134", "-2 100 134", "1 100 134", "4 100 134", "4 100 134", "7 100 131", "4 100 131", "1 100 131", "-2 100 131", "-5 100 131", "-5 100 128", "-2 100 128", "1 100 128", "4 100 128", "7 100 128");
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(onlinePlayers);

        for (int i = 0; i < onlinePlayers.size() && i < allowedLocations.size(); i++) {
            Player player = onlinePlayers.get(i);
            Location location = stringToLocation(allowedLocations.get(i));

            Block block = location.getBlock();
            block.setType(Material.LIGHTNING_ROD);

            for (Player t : Bukkit.getOnlinePlayers()) {
                SenderUtil.sendSound(t, Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 2);
            }
        }
    }

    public void removePlayers() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            EventPlayer e = new EventPlayer(p);
            if (e.isStaff()) return;


            if (!p.isInsideVehicle()) {
                EventUtils.eliminate(p, true);
            } else {
                Block block = p.getLocation().getBlock();
                block.setType(Material.AIR);
                p.leaveVehicle();
            }
        }
    }

    boolean defaultStatus = false;

    public void playOrStopMusic(boolean isPlayingSound) {
        Sound sound = Sound.MUSIC_DISC_WAIT;

        if (isPlayingSound) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.stopAllSounds();

                player.playSound(player, sound, SoundCategory.MASTER, 1.0f, 1.0f);
                player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 0.0f);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.stopSound(sound);
                player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 0.0f);
            }
        }

        SitService.status = !isPlayingSound;
        defaultStatus = isPlayingSound;
    }
}