package datta.core.menus;

import datta.core.content.builders.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static datta.core.Core.menuBuilder;
import static datta.core.content.builders.MenuBuilder.slot;

public class GiveMenu {

    public static void open(Player player, int page, ItemStack item) {
        menuBuilder.createMenu(player, "Palos > Dar u Obtener (" + page + ")", 9 * 5, false);
        menuBuilder.setContents(player, () -> {
            int[] allowedSlots = {10, 11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
            int indexSlot = allowedSlots[0];
            int pageSize = 21;
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, Bukkit.getOnlinePlayers().size());

            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (int i = startIndex; i < endIndex; i++) {
                Player onlinePlayer = onlinePlayers.get(i);
                menuBuilder.setItem(player, indexSlot, new ItemBuilder(Material.PLAYER_HEAD, "&e" + onlinePlayer.getName())
                        .addLore("&8Jugador", "",
                                "&fAgrega el item al",
                                "&finventario de " + onlinePlayer.getName() + ".",
                                "", "&aClic para agregar.")
                        .setHeadPlayer(onlinePlayer.getName()).build(), () -> {
                    onlinePlayer.getInventory().addItem(item);
                });

                indexSlot++;
            }

            if (page > 1) {
                menuBuilder.setItem(player, slot(1, 5), new ItemBuilder(Material.ARROW, "&ePagina anterior")
                        .hideAll(true).build(), () -> {
                    open(player, page - 1, item);
                });
            }

            menuBuilder.setItem(player, slot(9, 5), new ItemBuilder(Material.ARROW, "&eSiguiente pagina").hideAll(true).build(), () -> {
                open(player, page + 1, item);
            });


            menuBuilder.setItem(player, slot(4, 5), new ItemBuilder(Material.PLAYER_HEAD, "&aObtener en tu inventario")
                    .addLore("",
                            "&fObten el item en tu inventario",
                            "",
                            "&aClic para obtener.")
                    .setHeadPlayer(player.getName()).build(), () -> {
                player.getInventory().addItem(item);
            });

            menuBuilder.setItem(player, slot(6, 5), new ItemBuilder(item.getType(), "&aDar a todos")
                    .addLore("", "&fDa el item en el inventario de", "&ftodos los jugadores en línea", "", "&aClic para dar a todos.").build(), () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {

                    p.getInventory().addItem(item);
                }
            });

            menuBuilder.setItem(player, slot(5, 5), new ItemBuilder(Material.BARRIER, "&cVolver atras").build(), () -> {
                GameMenu.open(player);
            });
        });
    }
}