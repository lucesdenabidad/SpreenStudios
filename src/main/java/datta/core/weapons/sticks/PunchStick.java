package datta.core.weapons.sticks;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import datta.core.content.builders.ItemBuilder;
import datta.core.weapons.Stick;
import datta.core.utils.SenderUtil;
import org.bukkit.*;
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
import org.bukkit.util.Vector;


@CommandPermission("spreenstudios.sticks")
@CommandAlias("punchstick")
public class PunchStick extends Stick {

    int power = 1;

    @Override
    public String name() {
        return "punchstick";
    }

    @Override
    public ItemStack item() {
        return new ItemBuilder(Material.STICK, "&9Palito de pedos")
                .addEnchant(Enchantment.LUCK, 1)
                .hideAll(true)
                .build();
    }

    @Subcommand("setpower")
    public void setPower(CommandSender sender, int power) {
        this.power = power;
        SenderUtil.sendMessage(sender, "%core_prefix% &eEl poder de " + name() + " se modifico a " + power);
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

    @Override
    @EventHandler
    public void interactAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();
        if (rightClicked != null && rightClicked instanceof Player target) {
            PlayerInventory inventory = player.getInventory();
            ItemStack itemInMainHand = inventory.getItemInMainHand();
            if (itemInMainHand.isSimilar(item())) {


                if (event.getHand() != EquipmentSlot.HAND) return;

                World world = player.getWorld();
                Location tloc = target.getLocation();
                Location ploc = player.getLocation();

                world.playSound(tloc, Sound.ENTITY_GENERIC_EXPLODE, 0.3F, 1);
                world.spawnParticle(Particle.EXPLOSION_LARGE, tloc, 10, 1, 1, 1, 0);

                target.setVelocity(player.getLocation().getDirection().multiply(power).add(new Vector(0, 0.3f, 0)));
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
            double power = 1.5; // Definir la potencia del golpe

            World world = player.getWorld();
            Location tloc = target.getLocation();
            Location ploc = player.getLocation();

            world.playSound(tloc, Sound.ENTITY_GENERIC_EXPLODE, 0.3F, 1);
            world.spawnParticle(Particle.EXPLOSION_LARGE, tloc, 10, 1, 1, 1, 0);

            target.setVelocity(player.getLocation().getDirection().multiply(power).add(new Vector(0, 0.3f, 0)));
        }
    }
}


    @Override
    @EventHandler
    public void interactAtEntity(PlayerInteractEvent event) {

    }
}