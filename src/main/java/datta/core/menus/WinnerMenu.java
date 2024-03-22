package datta.core.menus;

import datta.core.content.builders.ItemBuilder;
import datta.core.content.utils.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static datta.core.Core.menuBuilder;
import static datta.core.content.builders.MenuBuilder.slot;

public class WinnerMenu {
    public static void open(Player player, int page) {
        menuBuilder.createMenu(player, "Menú > Elegir ganador", 9 * 5, false);
        menuBuilder.setContents(player, () -> {
            int[] allowedSlots = {10, 11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
            int indexSlot = allowedSlots[0];
            int pageSize = 21;
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, Bukkit.getOnlinePlayers().size());

            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (int i = startIndex; i < endIndex; i++) {
                Player target = onlinePlayers.get(i);
                menuBuilder.setItem(player, indexSlot, new ItemBuilder(Material.PLAYER_HEAD, "&a" + target.getName())
                        .addLore("",
                                "&7 Haz click para eligir a este",
                                "&7 este jugador como ganador",
                                "")

                        .setHeadPlayer(target.getName()).build(), () -> {
                    EventUtils.win(target);
                });

                indexSlot++;
            }

            if (page > 1) {
                menuBuilder.setItem(player, slot(1, 5), new ItemBuilder(Material.ARROW, "&cPagina anterior")
                        .hideAll(true).build(), () -> {
                    open(player, page - 1);
                });
            }

            menuBuilder.setItem(player, slot(9, 5), new ItemBuilder(Material.ARROW, "&aSiguiente pagina").hideAll(true).build(), () -> {
                open(player, page + 1);
            });

            menuBuilder.setItem(player, slot(5, 5), new ItemBuilder(Material.BARRIER, "&cVolver atras").build(), () -> {
                GameMenu.open(player);
            });
        });
    }
}