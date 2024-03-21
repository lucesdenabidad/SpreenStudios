package datta.core.services.list;

import co.aikar.commands.annotation.*;
import datta.core.Core;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.configuration.Configuration;
import datta.core.content.utils.bukkit.player.EventPlayer;
import datta.core.services.Service;
import datta.core.utils.SenderUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.List;

import static datta.core.Core.menuBuilder;
import static datta.core.content.builders.MenuBuilder.slot;

@CommandPermission("|spreenstudios.*|spreenstudios.toggle")
@CommandAlias("toggle|alternar|status|stages|stage")
public class ToggleService extends Service {
    public Configuration configuration = Core.getInstance().getConfig();

    @Override
    public Core instance() {
        return Core.getInstance();
    }

    @Override
    public String name() {
        return "toggle";
    }

    @Override
    public String[] info() {
        return new String[]{"", ""};
    }

    @Override
    public void onLoad() {
        register(true, true);
    }

    @Override
    public void onUnload() {
        register(true, false);
    }


    // # Commands
    @Default
    @CommandCompletion(" true|false")
    public void setToggleable(CommandSender sender, Toggleable toggleable, boolean v) {
        toggleable.set(v);
        SenderUtil.sendMessage(sender, "%core_prefix% &fSe modifico &a" + toggleable.name().toLowerCase() + "&f a &e" + v + "&f.");
    }

    @Subcommand("menu")
    public void openMenu(Player player) {
        menuBuilder.createMenu(player, "Menú de alternables", 9 * 6, false);
        menuBuilder.setContents(player, () -> {


            int[] availableSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39};
            int index = 0;

            for (Toggleable toggleable : Toggleable.values()) {
                if (index >= availableSlots.length) {
                    break;
                }

                int availableSlot = availableSlots[index];
                ItemStack itemStack = itemFromToggleable(toggleable);
                menuBuilder.setItem(player, availableSlot, itemStack, () -> {
                    toggleable.set(!toggleable.isStatus());
                });
                index++;
            }

            menuBuilder.setItem(player, slot(5, 6), new ItemBuilder(Material.BARRIER, "&cCerrar menú").build(), () -> {
                player.closeInventory();
            });
        });
    }


    public ItemStack itemFromToggleable(Toggleable toggleable) {

        boolean status = toggleable.isStatus();
        String color = status ? "&a" : "&c";
        String translate = status ? "Activado" : "Desactivado";

        final String TEXTURE_TRUE = "67ca9d16aeceb729c139daa563d098724a8e8bfad4473518bc48647ea02d2476";
        final String TEXTURE_FALSE = "ccc82dd91281a14a615d9b05bd5f97a9eea9266b9a1349e6b9fafb0ded318ff5";

        final List<String> LORE_TRUE = List.of(" ", "&fEstado: " + color + translate, " ", "&eClic para desactivar!");
        final List<String> LORE_FALSE = List.of(" ", "&fEstado: " + color + translate, " ", "&eClic para activar!");


        String texture = status ? TEXTURE_TRUE : TEXTURE_FALSE;
        List<String> lore = status ? LORE_TRUE : LORE_FALSE;

        return new ItemBuilder(Material.PLAYER_HEAD, color + toggleable.name())
                .setHeadUrl(texture)
                .setLore(lore)
                .build();
    }

    // # Events
    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p && e.getEntity() instanceof Player p2) {
        EventPlayer eventPlayer = new EventPlayer(p);
        if (eventPlayer.isStaff()) return;

            Toggleable category = Toggleable.PVP;
            if (!category.isStatus()) {
                e.setCancelled(true);
                SenderUtil.sendMessage(p, "%core_prefix% &7El PvP está desactivado");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        EventPlayer eventPlayer = new EventPlayer(p);

        if (eventPlayer.isStaff()) return;

        Toggleable category = Toggleable.BREAK;
        if (!category.isStatus()) {
            e.setCancelled(true);
            SenderUtil.sendMessage(p, "%core_prefix% &7No puedes romper bloques");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        EventPlayer eventPlayer = new EventPlayer(p);

        if (eventPlayer.isStaff()) return;

        Toggleable category = Toggleable.PLACE;
        if (!category.isStatus()) {
            e.setCancelled(true);
            SenderUtil.sendMessage(p, "%core_prefix% &7No puedes colocar bloques");
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        EventPlayer eventPlayer = new EventPlayer(p);

        if (eventPlayer.isStaff()) return;

        Toggleable category = Toggleable.DROP;
        if (!category.isStatus()) {
            e.setCancelled(true);
            SenderUtil.sendMessage(p, "%core_prefix% &7No puedes tirar items");
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player p) {

            EventPlayer eventPlayer = new EventPlayer(p);

            if (eventPlayer.isStaff()) return;

            Toggleable category = Toggleable.PICKUP;
            if (!category.isStatus()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        EventPlayer eventPlayer = new EventPlayer(p);

        if (eventPlayer.isStaff()) return;

        Toggleable category = Toggleable.CHAT;
        if (!category.isStatus()) {
            e.setCancelled(true);
            SenderUtil.sendMessage(p, "%core_prefix% &7El chat está desactivado");
        }
    }

    @EventHandler
    public void onExecuteCMD(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        EventPlayer eventPlayer = new EventPlayer(p);

        if (eventPlayer.isStaff()) return;


        Toggleable category = Toggleable.COMMANDS;
        if (!category.isStatus()) {
            e.setCancelled(true);
            SenderUtil.sendMessage(p, "%core_prefix% &7Los comandos están desactivados");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        EventPlayer eventPlayer = new EventPlayer(p);

        if (eventPlayer.isStaff()) return;

        if (e.getAction().name().contains("AIR")) return;
        Toggleable category = Toggleable.INTERACTIONS;
        if (!category.isStatus()) {
            e.setCancelled(true);
            if (e.getAction().name().contains("BLOCK")) {
                if (e.getHand() != EquipmentSlot.HAND) return; // Only main hand (right hand
                SenderUtil.sendMessage(p, "&c%core_prefix% &7Las interacciones están desactivadas");
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player p) {
            Toggleable category = Toggleable.FOOD;
            if (!category.isStatus()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player p) {
            Toggleable category = Toggleable.DAMAGE_ENTITIES;
            if (!category.isStatus()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByFall(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            Toggleable category = Toggleable.DAMAGE_FALL;
            if (!category.isStatus() && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByVoid(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            Toggleable category = Toggleable.VOID_DAMAGE;
            if (!category.isStatus() && e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onXPChange(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();
        Toggleable category = Toggleable.EXP;
        if (!category.isStatus()) {
            e.setAmount(0);
        }
    }

    @EventHandler
    public void onHealthChange(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player p) {
            Toggleable category = Toggleable.HEALTH;
            if (!category.isStatus()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            Toggleable category = Toggleable.DAMAGE;
            if (!category.isStatus()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent e) {
        Toggleable category = Toggleable.SPAWN;
        if (category.isStatus()) {
            e.setSpawnLocation(e.getPlayer().getWorld().getSpawnLocation());
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        Toggleable category = Toggleable.MOBS;
        if (!category.isStatus()) {
            e.setCancelled(true);
        }
    }

    @Getter
    public enum Toggleable {
        CHAT,
        COMMANDS,
        PVP,
        DAMAGE,
        INTERACTIONS,
        BREAK,
        PLACE,
        DROP,
        PICKUP,
        INVENTORY,
        FOOD,
        HEALTH,
        EXP,
        DAMAGE_ENTITIES,
        DAMAGE_FALL,
        SPAWN,
        MOBS,
        VOID_DAMAGE;

        private boolean status;

        Toggleable() {
            this.status = isStatusFromConfig();
        }

        public boolean isStatusFromConfig() {
            return Core.getInstance().getConfig().getBoolean("toggleable." + this.name(), true);
        }

        public void set(boolean value) {
            this.status = value;
            this.save();
        }

        public void save() {
            Core.getInstance().getConfig().set("toggleable." + this.name(), this.status);
            Core.getInstance().getConfig().safeSave();
        }
    }
}