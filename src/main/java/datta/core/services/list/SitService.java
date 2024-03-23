package datta.core.services.list;

import datta.core.Core;
import datta.core.services.Service;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.List;

public class SitService extends Service {

    public static Boolean status = true;

    @Override
    public Core instance() {
        return Core.getInstance();
    }
    @Override
    public String name() {
        return "sit";
    }

    @Override
    public String[] info() {
        return new String[0];
    }

    @Override
    public void onLoad() {
        register(true, true);
    }

    @Override
    public void onUnload() {
        register(true, false);
    }

    List<Material> allowedChairs = List.of(Material.LIGHTNING_ROD);
    List<Block> blockContainsPlayer = new ArrayList<>();

    @EventHandler
    public void interactAtSit(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.hasBlock()) {
            Block clickedBlock = e.getClickedBlock();
            if (allowedChairs.contains(clickedBlock.getType())) {
                if (!player.isInsideVehicle()) {
                    if (!player.isSneaking()) {
                        if (!status) return;
                        if (e.getHand() != EquipmentSlot.HAND) return;
                        if (blockContainsPlayer.contains(clickedBlock)) return;

                        if (e.getAction().isRightClick()) {
                            sit(player, clickedBlock);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void leaveOnSit(EntityDismountEvent event) {
        if (event.getDismounted() instanceof ArmorStand stand && event.getEntity() instanceof Player player) {

            unsit(player, player.getLocation().getBlock(), stand);
        }
    }


    public void sit(Player p, Block block) {

        blockContainsPlayer.add(block);

        ArmorStand stand = (ArmorStand) p.getWorld().spawnEntity(block.getLocation().toCenterLocation().subtract(0, 0.8F, 0), EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setCollidable(false);
        stand.setInvulnerable(true);
        stand.setSilent(true);
        stand.setAI(false);
        stand.setGravity(false);
        stand.setSmall(true);
        stand.addPassenger(p);
    }

    public void unsit(Player p, Block block, ArmorStand stand) {
        stand.remove();

        p.teleport(p.getLocation().toCenterLocation().add(0, 1, 0));
        blockContainsPlayer.remove(block);
    }
}