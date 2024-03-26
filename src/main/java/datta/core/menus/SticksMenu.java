package datta.core.menus;

import datta.core.content.builders.ItemBuilder;
import datta.core.weapons.Stick;
import datta.core.weapons.sticks.KickStick;
import datta.core.weapons.sticks.KillStick;
import datta.core.weapons.sticks.PunchStick;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static datta.core.Core.menuBuilder;
import static datta.core.content.builders.MenuBuilder.slot;

public class SticksMenu {
    public static void open(Player player) {
        menuBuilder.createMenu(player, "Menú > Palos", 9 * 4, false);
        menuBuilder.setContents(player, () -> {

            int index = 4;
            List<Stick> stickList = new ArrayList<>(List.of(new PunchStick(), new KillStick(), new KickStick()));

            for (Stick stick : stickList) {
                menuBuilder.setItem(player, slot(index, 2), stick.item(), () -> {
                    GiveMenu.open(player, 1, stick.item());
                });

                index++;
            }

            menuBuilder.setItem(player, slot(5, 4), new ItemBuilder(Material.BARRIER, "&cVolver atras").build(), () -> {
                GameMenu.open(player);
            });

        });
    }
}