package datta.core.weapons.sticks;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.utils.EventUtils;
import datta.core.games.games.Escondite;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


@CommandPermission("spreenstudios.sticks")
@CommandAlias("kickstick")
public class KickStick extends Stick {


    @Override
    public String name() {
        return "kickstick";
    }

    @Override
    public ItemStack item() {
        return new ItemBuilder(Material.STICK, "&dPalito pal lobby")
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


                if (Escondite.status) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 7 * 20, 1, false, false));
                }

                EventUtils.eliminate(target, true);
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
                if (Escondite.status) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 7 * 20, 1, false, false));
                }

                EventUtils.eliminate(target, true);
            }
        }
    }

    @Override
    @EventHandler
    public void interactAtEntity(PlayerInteractEvent event) {

    }
}