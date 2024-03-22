package datta.core.content.weapons.sticks;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import datta.core.content.utils.EventUtils;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.weapons.Stick;
import datta.core.utils.SenderUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


@CommandPermission("spreenstudios.sticks")
@CommandAlias("killstick")
public class KillStick extends Stick {

    @Override
    public String name() {
        return "killstick";
    }

    @Override
    public ItemStack item() {
        return new ItemBuilder(Material.STICK, "&cPalito asesino")
                .addEnchant(Enchantment.LUCK, 1)
                .hideAll(true)
                .build();
    }
    @Default
    @Override
    public void getItem(Player player) {
        player.getInventory().addItem(item());
        SenderUtil.sendMessage(player, "%core_prefix% &aRecibiste el " + name() + " en tu inventario.");
    }

    @Override
    @EventHandler
    public void interactAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();
        if (rightClicked != null && rightClicked instanceof Player target) {
            PlayerInventory inventory = player.getInventory();
            ItemStack itemInMainHand = inventory.getItemInMainHand();
            if (itemInMainHand.isSimilar(item())) {
                EventUtils.eliminate(target, false);
            }
        }
    }

    @Override
    @EventHandler
    public void interactAtEntity(PlayerInteractEvent event) {

    }
}