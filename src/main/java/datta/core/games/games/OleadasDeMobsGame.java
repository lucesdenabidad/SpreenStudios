package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.commands.CallCMD;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.games.Game;
import datta.core.services.list.ToggleService;
import datta.core.utils.SenderUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static datta.core.Core.info;
import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.utils.EventUtils.fix;
import static datta.core.content.utils.EventUtils.heal;


@CommandAlias("games")
public class OleadasDeMobsGame extends Game {

    @Override
    public String name() {
        return "Ordas";
    }

    @Override
    public Location spawn() {
        return stringToLocation("552 3 449");
    }

    @Override
    public void start() {
        game(() -> {

            CallCMD.callToggleable(ToggleService.Toggleable.PVP, false);
            CallCMD.callToggleable(ToggleService.Toggleable.FALL_DAMAGE, false);
            CallCMD.callToggleable(ToggleService.Toggleable.FOOD, false);
            CallCMD.callToggleable(ToggleService.Toggleable.PLACE, false);
            CallCMD.callToggleable(ToggleService.Toggleable.BREAK, false);
            CallCMD.callToggleable(ToggleService.Toggleable.DAMAGE, true);
            CallCMD.callToggleable(ToggleService.Toggleable.INTERACTIONS, true);
            CallCMD.callToggleable(ToggleService.Toggleable.SPAWNING_MOBS, true);
            CallCMD.callToggleable(ToggleService.Toggleable.KICK_ON_DEATH, true);
            CallCMD.callToggleable(ToggleService.Toggleable.TELEPORT_SPAWN_ON_JOIN, true);


            for (Player t : Bukkit.getOnlinePlayers()) {
                loadLoot(t);

                SenderUtil.sendActionbar(t, "&a¡Fuiste equipado!");
                SenderUtil.sendSound(t, Sound.ITEM_ARMOR_EQUIP_CHAIN, 1, 1);
                SenderUtil.sendSound(t, Sound.AMBIENT_NETHER_WASTES_MOOD, 1, 1);
            }

            genMobs(10, EntityType.ZOMBIE);
            task();
        });
    }

    @Override
    public void end() {
        end(() -> {
            if (task != null) {
                task.cancel();
            }

            removemobs();
        });
    }


    @Override
    public List<String> scoreboard() {
        return new ArrayList<>();
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return List.of(
                new MenuBuilder.MenuItem(new ItemBuilder(Material.ZOMBIE_SPAWN_EGG, "&eGenerar 10 Zombies").build(), () -> {
                    genMobs(10, EntityType.ZOMBIE);
                }),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.VINDICATOR_SPAWN_EGG, "&eGenerar 5 Vindicator").build(), () -> {
                    genMobs(5, EntityType.VINDICATOR);
                })

        );
    }

    @Override
    public Material menuItem() {
        return Material.ZOMBIE_SPAWN_EGG;
    }

    public void loadLoot(Player player) {
        PlayerInventory inventory = player.getInventory();
        heal(player);

        ItemStack sword = new ItemBuilder(Material.IRON_SWORD, "&eEspada")
                .addEnchant(Enchantment.DURABILITY, 5)
                .hideAll(true)
                .build();

        ItemStack shield = new ItemBuilder(Material.SHIELD, "&eEscudo")
                .hideAll(true)
                .build();

        inventory.setItemInMainHand(sword);
        inventory.setItemInOffHand(shield);
        inventory.addItem(new ItemStack(Material.GOLDEN_APPLE, 5));

        inventory.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        inventory.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        inventory.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        inventory.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));

    }

    Random random = new Random();

    @Subcommand("mobs genmobs")
    public void genMobs(int count, EntityType type) {
        Location pos1 = stringToLocation("518 3 367");
        Location pos2 = stringToLocation("586 3 531");

        int number = random.nextInt(2);

        for (int i = 0; i < count; i++) {
            Location location;
            if (number == 1) {
                location = stringToLocation("587 3 449");
            } else {
                location = stringToLocation("517 3 449");
            }

            Location centerLocation = location.toCenterLocation();
            World world = location.getWorld();

            world.spawnEntity(centerLocation, type);
            world.spawnParticle(Particle.CLOUD, centerLocation, 10, 1, 1, 1, 0);
        }

        info("Generando " + count + " entidades tipo " + fix(type.name()) + ".");
    }




    @Subcommand("mobs removemobs")
    public void removemobs() {
        Location pos1 = stringToLocation("518 3 367");
        World world = pos1.getWorld();

        for (Entity entity : world.getEntities()) {
            if (entity != null && entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.DROPPED_ITEM) {
                entity.remove();
            }
        }
    }



    BukkitTask task;
    public void task() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player t : Bukkit.getOnlinePlayers()) {

                    if (!t.getName().equalsIgnoreCase("SpreenDMC")) {
                        t.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 150, 255, false, false, false));
                    } else {
                        t.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN, 1));
                    }
                }
            }
        }.runTaskTimer(Core.getInstance(), 0, 20L);
    }

    @EventHandler
    public void zombieDeath(EntityDeathEvent event){
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer != null) {

            SenderUtil.sendSound(killer, Sound.ENTITY_ITEM_PICKUP,1,1);
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100,0,false,false,false));
        }
    }
}
