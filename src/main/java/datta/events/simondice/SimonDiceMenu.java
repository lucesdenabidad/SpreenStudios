package datta.events.simondice;

import datta.core.Core;
import datta.core.content.CoreTask;
import datta.core.content.builders.ItemBuilder;
import datta.core.services.list.ScreenColorService;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static datta.core.Core.menuBuilder;
import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.builders.MenuBuilder.slot;

public class SimonDiceMenu {

    public static void open(Player player) {
        menuBuilder.createMenu(player, "Menú > Simón Dice", 9 * 5, false);
        menuBuilder.setContents(player, () -> {

            menuBuilder.setItem(player, slot(2, 2), new ItemBuilder(Material.ENDER_PEARL, "&aTeletransportar a estadio").build(), () -> {
                teleport(player);
            });
            menuBuilder.setItem(player, slot(3, 2), new ItemBuilder(Material.IRON_PICKAXE, "&aLimpiar mapa").build(), () -> {
                SimonDiceActions.clearMap(player);
            });

/*            menuBuilder.setItem(player, slot(4, 2), new ItemBuilder(Material.PAPER, "&eJugadores en linea blanca").build(), () ->{
                SimonDiceActions.playersToLine(player);
            });*/

            menuBuilder.setItem(player, slot(4, 2), new ItemBuilder(Material.STICK, "&eObtener palos").build(), () -> {
                SimonDiceActions.sticks(player);
            });


            menuBuilder.setItem(player, slot(5, 5), new ItemBuilder(Material.BARRIER, "&cCerrar").build(), player::closeInventory);
        });
    }



    public static void teleport(Player player) {
        ScreenColorService service = (ScreenColorService) Core.getInstance().commandService.serviceFromName("screencolor");
        int fade = 25;

        service.showAll(ChatColor.BLACK, fade, fade);

        CoreTask.runTask(() -> {
            for (Player t : Bukkit.getOnlinePlayers()) {
                t.teleport(stringToLocation("1 101 134").toCenterLocation());
                SenderUtil.sendActionbar(t, "&7(!) &fEstás siendo teletransportado!");
            }
        }, 45L);

        SenderUtil.sendMessage(player, "%core_prefix% &fTodos los jugadores fueron teletransportados.");
    }
}