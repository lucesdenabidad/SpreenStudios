package datta.events.simondice;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import datta.core.content.builders.ItemBuilder;
import datta.core.utils.SenderUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


@CommandPermission("spreenstudios.simondice")
@CommandAlias("simondice|simon|saymonsay")
public class SimonDice extends BaseCommand implements Listener {

    ItemStack itemStack = new ItemBuilder(Material.PLAYER_HEAD, "&eSimón Dice")

            .setHeadUrl("a8dfe8d0fd4553ffed0344daf72d88deae85408ebc184eac2a1245aeeb3a0913")
            .addLore("",
                    "&7 Haz click en el item para abrir el",
                    "&7 panel de control del 'Simón dice'",
                    "")
            .hideAll(true)
            .build();

    @Subcommand("item")
    public void item(Player player, @Optional OnlinePlayer onlinePlayer) {
        if (onlinePlayer == null) {
            player.getInventory().setItem(8, itemStack);
            SenderUtil.sendMessage(player, "%core_prefix% &eRecibiste el panel de control en el slot 9 de tu inventario.");
        } else {
            Player target = onlinePlayer.getPlayer();
            target.getInventory().setItem(8, itemStack);


            SenderUtil.sendMessage(player, "%core_prefix% &eLe diste el panel de control a " + target.getName() + ".");
            SenderUtil.sendMessage(target, "%core_prefix% &eObtuviste el panel de control en el slot 9 de tu inventario.");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.hasItem()) {
            ItemStack item = event.getItem();
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                if (item.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
                    SimonDiceMenu.open(player);
                    event.setCancelled(true);
                }
            }
        }
    }
}