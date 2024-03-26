package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.CoreTask;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventUtils;
import datta.core.content.utils.build.BuildUtils;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.games.Game;
import datta.core.services.list.CinemaService;
import datta.core.services.list.SitService;
import datta.core.utils.SenderUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.utils.EventUtils.isStaff;
import static datta.core.content.utils.build.BuildUtils.replace;

@CommandPermission("spreenstudios.games")
@CommandAlias("games")
public class SillasMusicales extends Game {

    @Override
    public int endAt() {
        return 0;
    }

    @Override
    public String name() {
        return "Sillitas Musicales";
    }

    @Override
    public Location spawn() {
        return stringToLocation("654 3 431 0 0");
    }

    @Override
    public void start() {
        game(() -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (isStaff(onlinePlayer)) {
                    getItems(onlinePlayer);
                }
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

                new MenuBuilder.MenuItem(new ItemBuilder(Material.LIGHTNING_ROD, "&eGenerar Sillas").build(), this::spawnChairs),
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


    List<MenuBuilder.MenuItem> menuItems = new ArrayList<>(List.of(
            new MenuBuilder.MenuItem(new ItemBuilder(Material.LIME_DYE, "&aPrender Musica").build(), () -> {
                playOrStopMusic(true);
            }),

            new MenuBuilder.MenuItem(new ItemBuilder(Material.RED_DYE, "&cApagar Musica").build(), () -> {
                playOrStopMusic(false);
            }),

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
        Cuboid cuboid = new Cuboid("659 5 454 650 4 445");
        replace(cuboid, Material.LIGHTNING_ROD, Material.AIR);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            onlinePlayer.leaveVehicle();
    }


    public void spawnChairs() {
        List<String> allowedLocations = List.of("652 5 451", "654 5 452", "656 5 451", "657 5 449", "656 5 447", "654 5 446", "652 5 447", "651 5 449");

        for (String allowedLocation : allowedLocations) {
            Location location = stringToLocation(allowedLocation);

            Block block = location.getBlock();
            block.setType(Material.LIGHTNING_ROD);
        }
    }


    public void removePlayers() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isOp()) {

                if (!p.isInsideVehicle()) {
                    EventUtils.eliminate(p, true);
                } else {
                    p.leaveVehicle();
                }
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


    @Subcommand("sillas test")
    public void test(Player player) {
        forAllPlayers();
    }
    @Subcommand("sillas paste")
    public void paste(Player player) {
        BuildUtils.schematic(stringToLocation("1 99 134"), "sillitas.schem");
    }
    @Subcommand("sillas clean")
    public void clean(Player player) {
        BuildUtils.schematic(stringToLocation("1 99 134"), "sillitas.schem");
    }


    public void forAllPlayers() {

        List<Player> filteredPlayers = Bukkit.getOnlinePlayers().stream()
                .filter(onlinePlayer -> !onlinePlayer.isOp())
                .collect(Collectors.toList());

        int delay = 0;
        for (Player onlinePlayer : filteredPlayers) {
            CoreTask.runTask(() -> {
                for (Player target : Bukkit.getOnlinePlayers()) {
                    playCinema(target, onlinePlayer.getName());
                }
            }, delay);

            delay += 120;
        }
    }

    public void playCinema(Player play, String name) {
        String black = ChatColor.BLACK + "㐀";
        Block block = stringToLocation("1 102 134").getBlock();
        block.setType(Material.AIR);

        play.sendTitle(black, "", 0, 20, 20);

        CinemaService cinemaService = (CinemaService) Core.getInstance().commandService.serviceFromName("cinema");
        cinemaService.playCinema(play, cinemaService.getCinema("sillitas-present"));

        NPC npc = CitizensAPI.getNPCRegistry().getById(5);
        npc.setName(name);

        CoreTask.runTask(() -> {
            SenderUtil.sendSound(play, "qsm:light", 1, 1);
            block.setType(Material.LIGHT);

            CoreTask.runTask(() -> {
                play.sendTitle(black, "", 20, 50, 0);
                SenderUtil.sendSound(play, "qsm:light", 1, 1);
                block.setType(Material.AIR);
            }, 40L);

        }, 50L);
    }

}