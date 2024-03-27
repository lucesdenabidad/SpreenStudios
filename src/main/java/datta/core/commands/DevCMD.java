package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.cinematicas.SillitasPreview;
import datta.core.content.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


@CommandPermission("spreenstudios.dev")
@CommandAlias("dev|devtest")
public class DevCMD extends BaseCommand {

    @Subcommand("test")
    public void test(Player player) {
        ItemStack build = new ItemBuilder(Material.PLAYER_HEAD, "perugod")
                .setLore("&aHola!", "pe!")
                .setHeadPlayer("datta")
                .build();

        player.getInventory().addItem(build);
    }

    @Subcommand("testcinema")
    public void testcinema(Player player) {
        SillitasPreview.start(player);
    }

    @Subcommand("setmax")
    public void setmax(Player player, int max) {
        Core.setEndAt(max);
    }
}
