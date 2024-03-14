package datta.core.paper.items.list;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import datta.core.paper.animations.am.Animation;
import datta.core.paper.animations.am.AnimationManager;
import datta.core.paper.items.CustomItem;
import datta.core.paper.utilities.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static datta.core.paper.utilities.Utils.send;

public class KickStick extends CustomItem {

    @Override
    public ItemStack itemStack() {
        return new ItemBuilder(Material.STICK,
                "&c(!) &fExpulsar a un jugador &7(Click derecho)",
                "&fInteractua con un jugador con este item ",
                "&fy sera expulsado del servidor.").build();
    }

    @CommandPermission("core.kickstick")
    @CommandAlias("kickstick")
    @Override
    public void handleCommand(Player player, String[] args) {
        player.getInventory().addItem(itemStack());
        send(player, "&a&lEvento &8> &fRecibiste el &e'Kickstick'&f en tu inventario.");
    }

    @EventHandler
    public void handleEvent(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();
        if (event.getHand().equals(EquipmentSlot.HAND)) {

            if (rightClicked != null && rightClicked instanceof Player target) {
                if (player.getInventory().getItemInMainHand().isSimilar(itemStack())) {


                    Animation expulse = AnimationManager.getAnimationOnString("expulse");
                    expulse.play(player, target);

                    event.setCancelled(true);
                }
            }
        }
    }
}