package datta.core.weapons.sticks;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import datta.core.Core;
import datta.core.content.builders.ItemBuilder;
import datta.core.utils.SenderUtil;
import datta.core.weapons.Stick;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;


@CommandPermission("spreenstudios.sticks")
@CommandAlias("voicestick")
public class VoiceStick extends Stick {

    @Override
    public String name() {
        return "voicestick";
    }

    @Override
    public ItemStack item() {
        return new ItemBuilder(Material.STICK, "&cPalito de voz")
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

        if(!(rightClicked instanceof Player target)) return;
        if(event.getHand() != EquipmentSlot.HAND) return;
        if(!player.getInventory().getItemInMainHand().isSimilar(item())) return;

        boolean muted = false;
        if(target.hasMetadata(name()))
            muted = target.getMetadata(name()).get(0).asBoolean();

        target.setMetadata(name(), new FixedMetadataValue(Core.getInstance(), !muted));
        SenderUtil.sendMessage(player, "%core_prefix% &aEl jugador "+target.getName()+" fue "+(muted ? "&adesmuteado!" : "&cmuteado!"));

        if(muted){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+target.getName()+" permission set voicechat.speak true");
        }else{
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+target.getName()+" permission set voicechat.speak false");
        }
    }

    @Override
    @EventHandler
    public void interactAtEntity(PlayerInteractEvent event) {}
}