package datta.core.menus;

import datta.core.Core;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.consts.MenuConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static datta.core.Core.menuBuilder;
import static datta.core.content.builders.MenuBuilder.slot;

public class OptionsMenu {
    public static void open(Player player) {
        new MenuConstructor(player, "Menú > Opciones", 9 * 5, () -> {

            menuBuilder.setItem(player, slot(2, 2), new ItemBuilder(Material.REDSTONE_BLOCK, "&cCancelar juegos")
                    .addLore("",
                            "&7 Haz click para cancelar todos los juegos",
                            "")
                    .build(), () -> {
                Core.getInstance().stopGames();
            });

            menuBuilder.setItem(player, slot(3, 2), new ItemBuilder(Material.LIGHT, "&6Alternar luces")
                    .addLore("",
                            "&7 Haz click para alternar las luces",
                            "")
                    .build(), () -> {
                player.performCommand("lights");
            });

            menuBuilder.setItem(player, slot(4, 2), new ItemBuilder(Material.ENDER_PEARL, "&eMover a todos hacia ti")
                    .addLore("",
                            "&7 Haz click para teletransportar",
                            "&7 a todos los jugadores hacia ti.",
                            "")
                    .build(), () -> {
                player.performCommand("tpall");
            });

            menuBuilder.setItem(player, slot(5, 2), new ItemBuilder(Material.ENDER_EYE, "&eMoverte al 'SPAWN'")
                    .addLore("",
                            "&7 Haz click para ir hacia el spawn",
                            "")
                    .build(), () -> {
                World world = player.getWorld();
                player.teleport(world.getSpawnLocation().toCenterLocation());
            });
            menuBuilder.setItem(player, slot(6, 2), new ItemBuilder(Material.ENDER_CHEST, "&eMover a todos al 'SPAWN'")
                    .addLore("",
                            "&7 Haz click para llevar a todos hacia el spawn",
                            "")
                    .build(), () -> {
                World world = player.getWorld();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.teleport(world.getSpawnLocation().toCenterLocation());
                }
            });


            menuBuilder.setItem(player, slot(5, 5), new ItemBuilder(Material.BARRIER, "&cVolver atras").build(), () -> {
                GameMenu.open(player);
            });

        });
    }
}