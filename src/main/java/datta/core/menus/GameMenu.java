package datta.core.menus;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import datta.core.Core;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.games.CommandGame;
import datta.core.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static datta.core.Core.menuBuilder;
import static datta.core.content.builders.ColorBuilder.locationToString;
import static datta.core.content.builders.MenuBuilder.slot;

public class GameMenu extends BaseCommand {

    @CommandPermission("spreenstudios.gamemenu")
    @CommandAlias("gamemenu")
    public static void open(Player player) {
        CommandGame commandGame = Core.getInstance().commandGame;

        menuBuilder.createMenu(player, "Menu de juegos", 9 * 5, false);
        menuBuilder.setContents(player, () -> {

            for (Game game : commandGame.gameList) {
                ItemStack itemStack = game.menuItem();
                int i = game.menuSlot();

                menuBuilder.setItem(player, i, itemStack, () -> {
                    subMenu(player, game);
                });
            }

            menuBuilder.setItem(player, slot(5, 5), new ItemBuilder(Material.BARRIER, "&cCerrar menú").build(), player::closeInventory);

        });
    }


    public static void subMenu(Player player, Game game) {
        menuBuilder.createMenu(player, "Menu de " + game.name(), 9 * 5, false);
        menuBuilder.setContents(player, () -> {
                    menuBuilder.setItem(player, slot(4, 2), new ItemBuilder(Material.PLAYER_HEAD, "&aIniciar juego")
                            .addLore("", "&bInformación del juego:")
                            .addLore(game.gameinfo())
                            .addLore("", "&e› Clic para iniciar juego.")
                            .setHeadUrl("67ca9d16aeceb729c139daa563d098724a8e8bfad4473518bc48647ea02d2476")
                            .build(), game::start);

                    menuBuilder.setItem(player, slot(5, 2), new ItemBuilder(Material.PLAYER_HEAD, "&6Pausar juego")
                            .setLore("", "&fPausa el juego si esta iniciado.",
                                    "",
                                    "&c&l(!) &cPara esta accion el juego debe",
                                    "&ctener configurado las acciones al pausar",
                                    "",
                                    "&e› Clic para pausar")
                            .setHeadUrl("49c590dd3bec7c75f7bf46965c800baded138edf74837120472ee65d96b31688")
                            .build(), () -> {

                    });

                    menuBuilder.setItem(player, slot(6, 2), new ItemBuilder(Material.PLAYER_HEAD, "&cTerminar juego")
                            .setLore("", "&fFinaliza '" + game.name() + "' si esta iniciado.", "", "&e› Clic para finalizar")
                            .setHeadUrl("ccc82dd91281a14a615d9b05bd5f97a9eea9266b9a1349e6b9fafb0ded318ff5")
                            .build(), game::end);

                    menuBuilder.setItem(player, slot(2, 3), new ItemBuilder(Material.ENDER_PEARL, "&bIr al juego")
                            .setLore("", "&fTeletransportate a ti al juego", " ", "&e› Clic para ir a " + locationToString(game.spawn()))
                            .build(), () -> {
                        Location spawn = game.spawn();
                        player.teleport(spawn.toCenterLocation());
                        subMenu(player, game);
                    });

                    menuBuilder.setItem(player, slot(3, 3), new ItemBuilder(Material.ENDER_EYE, "&bIr todos al juego")
                            .setLore("", "&fTeltransporta a todos los jugadores al juego.", " ", "&e› Clic para ir a " + locationToString(game.spawn()))
                            .build(), () -> {
                        Location spawn = game.spawn();
                        for (Player t : Bukkit.getOnlinePlayers()) {
                            t.teleport(spawn.toCenterLocation());
                        }

                        subMenu(player, game);
                    });

            loadItems(player, game);

            menuBuilder.setItem(player, slot(5, 6), new ItemBuilder(Material.BARRIER, "&cVolver atras").build(), () -> {
                open(player);
            });
        });
    }

    public static void loadItems(Player player, Game game) {
        int[] availableSlots = {28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int index = 0;

        List<MenuBuilder.MenuItem> menu = game.menuItems(player);
        if (menu == null || menu.isEmpty()){
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