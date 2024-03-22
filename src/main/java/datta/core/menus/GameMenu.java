package datta.core.menus;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import datta.core.Core;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.weapons.GameItem;
import datta.core.games.CommandGame;
import datta.core.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static datta.core.Core.menuBuilder;
import static datta.core.content.builders.MenuBuilder.slot;

public class GameMenu extends BaseCommand {

    @CommandPermission("spreenstudios.gamemenu")
    @CommandAlias("gamemenu")
    public static void open(Player player) {
        CommandGame commandGame = Core.getInstance().commandGame;

        menuBuilder.createMenu(player, "Menu de juegos", 9 * 5, false);
        menuBuilder.setContents(player, () -> {

            int[] gameSlots = {12, 13, 14, 15, 21, 22, 23, 24, 29, 30, 31, 32, 33};
            int index = 0;

            for (Game game : commandGame.gameList) {
                String name = game.name();
                ItemStack itemStack = game.menuItem();
                Material type = itemStack.getType();

                ItemStack build = new ItemBuilder(type, "&b" + name)
                        .setLore("",
                                "&7 Haz clic para abrir menú de juego",
                                "&7 puedes Iniciar, Frenar, Pausar el juego",
                                "&7 entre otras acciones.",
                                "")
                        .build();

                int i = game.menuSlot();

                menuBuilder.setItem(player, gameSlots[index], build, () -> {
                    subMenu(player, game);
                });

                index++;
            }

            int[] slots = {0, 1, 10, 19, 28, 36, 37};

            for (int slot : slots) {
                menuBuilder.setItem(player, slot, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, "").build(), () -> {
                });
            }

            menuBuilder.setItem(player, slot(1, 2), new ItemBuilder(Material.STICK, "&6→ &ePalos").addLore(
                            "",
                            "&7Haz clic para ver los palos creados.",
                            "")
                    .build(), () -> {
                SticksMenu.open(player);
            });

            menuBuilder.setItem(player, slot(1, 3), new ItemBuilder(Material.COMPASS, "&5→ &dItem de Evento").addLore(
                            "",
                            "&7Haz click para obtener",
                            "&7la herramienta de evento.",
                            "")
                    .build(), () -> {
                new GameItem().getItem(player);
            });

            menuBuilder.setItem(player, slot(1, 4), new ItemBuilder(Material.PLAYER_HEAD, "&2→ &aElegir ganador").addLore(
                            "",
                            "&7Haz click para elegir",
                            "&7el ganador del evento.",
                            "")

                    .setHeadUrl("61e974a2608bd6ee57f33485645dd922d16b4a39744ebab4753f4deb4ef782")
                    .build(), () -> {
                WinnerMenu.open(player, 1);
            });

            menuBuilder.setItem(player, slot(9, 5), new ItemBuilder(Material.PLAYER_HEAD, "&3→ &bOpciones").addLore(
                            "",
                            "&7Haz clic para ver opciones generales.",
                            "")
                            .setHeadUrl("933742118647add7a11e1379b8627bedc7548e2b5e44a19a729cc41a9d265ef")
                    .build(), () -> {
                OptionsMenu.open(player);
            });

        });
    }

    public static void subMenu(Player player, Game game) {
        menuBuilder.createMenu(player, "Menu de " + game.name(), 9 * 5, false);
        menuBuilder.setContents(player, () -> {

            int[] slots = {0, 1, 10, 19, 28, 36, 37};

            for (int slot : slots) {
                menuBuilder.setItem(player, slot, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, "").build(), () -> {
                });
            }

            menuBuilder.setItem(player, slot(1, 2), new ItemBuilder(Material.PLAYER_HEAD, "&aIniciar juego")
                    .addLore("",
                            "&7 Haz click para iniciar el juego",
                            "&7 ya configurado.",
                            "")
                    .setHeadUrl("67ca9d16aeceb729c139daa563d098724a8e8bfad4473518bc48647ea02d2476")
                    .build(), game::start);

            menuBuilder.setItem(player, slot(1, 3), new ItemBuilder(Material.PLAYER_HEAD, "&cTerminar juego")
                    .setLore("",
                            "&7 Haz click para finalizar el juego",
                            "")
                    .setHeadUrl("ccc82dd91281a14a615d9b05bd5f97a9eea9266b9a1349e6b9fafb0ded318ff5")
                    .build(), game::end);

            menuBuilder.setItem(player, slot(1, 4), new ItemBuilder(Material.ENDER_EYE, "&bIr todos al juego")
                    .setLore("",
                            "&7 Haz click para teletransportar a",
                            "&7 todos los jugadores al juego",
                            "&7 ideal para una explicacion previa.",
                            " ")
                    .build(), () -> {
                Location spawn = game.spawn();
                for (Player t : Bukkit.getOnlinePlayers()) {
                    t.teleport(spawn.toCenterLocation());
                }

                subMenu(player, game);
            });

            menuBuilder.setItem(player, slot(4, 2), new ItemBuilder(Material.ENDER_PEARL, "&bIr al juego")
                    .setLore("",
                            "&7 Teletransportate solamente a ti",
                            "&7 ala ubicacion del juego para verlo.",
                            "")
                    .build(), () -> {
                Location spawn = game.spawn();
                player.teleport(spawn.toCenterLocation());
                subMenu(player, game);
            });

            loadItems(player, game);

            menuBuilder.setItem(player, slot(5, 6), new ItemBuilder(Material.BARRIER, "&cVolver atras").build(), () -> {
                open(player);
            });
        });
    }

    public static void loadItems(Player player, Game game) {
        int[] availableSlots = {13, 14, 15, 16, 21, 22, 23, 24, 25, 30, 31, 32, 33, 34};
        int index = 0;

        List<MenuBuilder.MenuItem> menu = game.menuItems(player);
        if (menu == null || menu.isEmpty()) {
            return;
        }

        for (MenuBuilder.MenuItem menuItem : menu) {
            if (index >= availableSlots.length) {
                break;
            }
            int availableSlot = availableSlots[index];
            ItemStack itemStack = menuItem.getItemStack();
            Runnable action = menuItem.getAction();

            menuBuilder.setItem(player, availableSlot, itemStack, action);
            index++;
        }
    }
}