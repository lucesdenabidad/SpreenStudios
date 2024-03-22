package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventUtils;
import datta.core.games.Game;
import datta.core.services.list.TimerService;
import datta.core.services.list.ToggleService;
import datta.core.utils.SenderUtil;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
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

import static datta.core.Core.info;
import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.builders.MenuBuilder.slot;
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
        return stringToLocation("552 14 538 -180 0");
    }

    @Override
    public String[] gameinfo() {
        return new String[0];
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

    @Subcommand("mobs start")
    @Override
    public void start() {
        game(() -> {
            ToggleService.Toggleable.KICK_ON_DEATH.set(true);
            ToggleService.Toggleable.KICK_ON_DEATH.save();

            TimerService.bossBarTimer("&a(!) &fTeletransportando en &e{time} ⌚", BarColor.PURPLE, BarStyle.SOLID, 10, () -> {

                for (Player t : Bukkit.getOnlinePlayers()) {
                    t.teleport(stringToLocation("552 3 449"));
                    loadLoot(t);

                    SenderUtil.sendTitle(t, "&e&lArmado!", "&8» &f¡Buena suerte! &8«");
                    SenderUtil.sendSound(t, Sound.ITEM_ARMOR_EQUIP_CHAIN, 1, 1);
                    SenderUtil.sendSound(t, Sound.AMBIENT_NETHER_WASTES_MOOD, 1, 1);
                }

                genMobs(10, EntityType.ZOMBIE);
                task();
            });
        });
    }

    @Subcommand("mobs end")
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
        return new ArrayList<>(List.of(
                "",
                "&f Mobs: &a%core_zombies%",
                "&f Vivos: &a%core_alive%",
                ""
        ));
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return List.of(
                new MenuBuilder.MenuItem(new ItemBuilder(Material.ZOMBIE_SPAWN_EGG, "&eGenerar 10 Zombies").build(), () ->{
                    genMobs(10, EntityType.ZOMBIE);
                }),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.VINDICATOR_SPAWN_EGG, "&eGenerar 5 Vindicator").build(), () ->{
                    genMobs(5, EntityType.VINDICATOR);
                })

        );
    }

    @Override
    public ItemStack menuItem() {
        return new ItemBuilder(Material.IRON_SWORD, "&eOrdas de mobs")
                .addLore("")
                .addLore(gameinfo())
                .addLore("", "&aClic para ver.")
                .build();
    }

    @Subcommand("mobs genmobs")
    public void genMobs(int count, EntityType type) {
        Location pos1 = stringToLocation("518 3 367");
        Location pos2 = stringToLocation("586 3 531");

        for (int i = 0; i < count; i++) {
            Location location = EventUtils.genLocationInLocations(pos1, pos2);
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
                    t.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 150, 255, false, false, false));
                }
            }
        }.runTaskTimer(Core.getInstance(), 0, 20L);
    }
    @Override
    public int menuSlot() {
        return slot(5,2);
    }

    @EventHandler
    public void zombieDeath(EntityDeathEvent event){
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer != null && entity instanceof Zombie zombie) {
            SenderUtil.sendSound(killer, Sound.ENTITY_ITEM_PICKUP,1,1);
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100,0,false,false,false));
        }
    }
}
