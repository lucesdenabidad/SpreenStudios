package datta.core.content.weapons;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import datta.core.content.builders.ItemBuilder;
import datta.core.menus.GameMenu;
import datta.core.utils.SenderUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandPermission("spreenstudios.gameitem")
@CommandAlias("gameitem")
public class GameItem extends Weapon {
    @Override
    public String name() {
        return "GameItem";
    }

    @Override
    public ItemStack item() {
        return new ItemBuilder(Material.PLAYER_HEAD, "&eHerramienta de Evento &7(Click derecho)")
                .setHeadPlayer("SpreenDMC")
                .hideAll(true)
                .build();
    }

    @Default
    @Override
    public void getItem(Player player) {
        player.getInventory().addItem(item());
        SenderUtil.sendMessage(player, "%core_prefix% &aObtuviste la herramienta de evento en tu inventario.");
    }

    @Default
    public void getItem(CommandSender sender, OnlinePlayer onlinePlayer) {
        Player player = onlinePlayer.getPlayer();
        player.getInventory().addItem(item());

        SenderUtil.sendMessage(sender, "%core_prefix% &aLe has dado el " + name() + " a " + player.getName());
    }

    @Override
    public void interactAtEntity(PlayerInteractAtEntityEvent event) {
    }

    @Override
    @EventHandler
    public void interactAtEntity(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.hasItem()) {
            ItemStack itemstack = event.getItem();

            if (isItem(item(), itemstack)) {
                GameMenu.open(player);
                event.setCancelled(true);
            }
        }
    }

    public boolean isItem(ItemStack itemStack, ItemStack target) {
        // Compara los tipos de los elementos
        if (itemStack.getType() != target.getType()) {
            return false;
        }

        // Compara los nombres mostrados de los elementos
        ItemMeta itemMeta = itemStack.getItemMeta();
        ItemMeta targetMeta = target.getItemMeta();

        // Si alguno de los dos elementos no tiene metadata, no se puede comparar el nombre mostrado
        if (itemMeta == null || targetMeta == null) {
            return false;
        }

        // Compara los nombres mostrados
        String displayName = itemMeta.getDisplayName();
        String targetDisplayName = targetMeta.getDisplayName();

        // Si uno de los nombres mostrados es nulo o no son iguales, retorna falso
        if (displayName == null || targetDisplayName == null || !displayName.equals(targetDisplayName)) {
            return false;
        }

        // Ambos tipos y nombres mostrados son iguales, retorna verdadero
        return true;
    }
}
