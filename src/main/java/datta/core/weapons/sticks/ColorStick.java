package datta.core.weapons.sticks;

import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.utils.EventUtils;
import datta.core.utils.SenderUtil;
import datta.core.weapons.Stick;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@CommandPermission("spreenstudios.sticks")
@CommandAlias("colorstick")
public class ColorStick extends Stick {

    public String ASSIGN_COLOR = "&7";

    @Override
    public String name() {
        return "colorstick";
    }

    @Override
    public ItemStack item() {
        return new ItemBuilder(Material.STICK, "&ePalito asignador de color")
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

    @Default
    public void getItem(CommandSender sender, OnlinePlayer onlinePlayer) {
        Player player = onlinePlayer.getPlayer();
        player.getInventory().addItem(item());

        SenderUtil.sendMessage(sender, "%core_prefix% &aLe has dado el " + name() + " a " + player.getName());
    }


    @CommandCompletion("&a|&c")
    @Subcommand("setcolor")
    public void setcolor(CommandSender sender, String color) {
        this.ASSIGN_COLOR = color;
        SenderUtil.sendMessage(sender, "%core_prefix% &eEl color asignado ahora de " + name() + " se modifico a " + color + "&l*&f.");
    }


    @Override
    @EventHandler
    public void interactAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();
        if (rightClicked != null && rightClicked instanceof Player target) {
            PlayerInventory inventory = player.getInventory();
            ItemStack itemInMainHand = inventory.getItemInMainHand();

            if (event.getHand() != EquipmentSlot.HAND) return;

            if (itemInMainHand.isSimilar(item())) {
                function(player, target);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player && damaged instanceof Player) {
            Player player = (Player) damager;
            Player target = (Player) damaged;

            PlayerInventory inventory = player.getInventory();
            ItemStack itemInMainHand = inventory.getItemInMainHand();

            if (itemInMainHand.isSimilar(item())) {

                function(player, target);
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void interactAtEntity(PlayerInteractEvent event) {

    }


    public void function(Player operator, Player target) {
        if (!EventUtils.hasColor(target)) {
            EventUtils.addPlayerColor(target, ASSIGN_COLOR);
            SenderUtil.sendMessage(operator, "%core_prefix% &fSe le asigno el color " + ASSIGN_COLOR + "(*) &fa " + target.getName() + ".");
        } else {
            EventUtils.removePlayerColor(target);
            SenderUtil.sendMessage(operator, "%core_prefix% &fSe le quito el color " + ASSIGN_COLOR + "(*) &fa " + target.getName() + ".");

        }
    }
}