package datta.core.games.games.disable;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.commands.CallCMD;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.games.Game;
import datta.core.services.list.LightService;
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
import static datta.core.content.utils.EventUtils.*;


@CommandPermission("spreenstudios.games")
@CommandAlias("games")
public class OleadasDeMobsGame extends Game {

    @Override
    public int endAt() {
        return 60;
    }

    @Override
    public String name() {
        return "Ordas";
    }

    @Override
    public Location spawn() {
        return stringToLocation("1 100 134");
    }

    int taskid;

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


            LightService lightService = (LightService) Core.getInstance().commandService.serviceFromName("light");
            lightService.setStatus(false);

            taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), new Runnable() {
                int secondsPassed = 0;

                @Override
                public void run() {
                    if (secondsPassed < 5) {

                        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                            onlinePlayer.sendTitle(ChatColor.BLACK + "㐀", "", 0, 50, 0);

                        secondsPassed++;
                    } else {
                        Bukkit.getScheduler().cancelTask(taskid);

                        for (Player t : Bukkit.getOnlinePlayers()) {
                            t.sendTitle(ChatColor.BLACK + " ", "", 0, 1, 0);
                            loadLoot(t);

                            SenderUtil.sendActionbar(t, "&a¡Fuiste equipado!");
                            SenderUtil.sendSound(t, Sound.ITEM_ARMOR_EQUIP_CHAIN, 1, 1);
                            SenderUtil.sendSound(t, Sound.AMBIENT_NETHER_WASTES_MOOD, 1, 1);
                        }

                        genMobs(40, EntityType.ZOMBIE);
                        task();
                    }
                }
            }, 0L, 20L);
        });
    }

    @Override
    public void end() {
        end(() -> {
            if (task != null) {
                task.cancel();
            }

            for (Player t : Bukkit.getOnlinePlayers()) {
                PlayerInventory inventory = t.getInventory();
                ItemStack helmet = inventory.getHelmet();
                if (helmet != null && helmet.getType() == Material.CARVED_PUMPKIN){
                    inventory.setHelmet(new ItemStack(Material.AIR));
                }
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
                new MenuBuilder.MenuItem(new ItemBuilder(Material.BARRIER, "&cEliminar mobs").build(), this::removemobs),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.ZOMBIE_SPAWN_EGG, "&eGenerar 10 Zombies").build(), () -> {
                    genMobs(10, EntityType.ZOMBIE);
                }),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.SKELETON_SPAWN_EGG, "&eGenerar 10 Esqueletos").build(), () -> {
                    genMobs(10, EntityType.SKELETON);
                }),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.HUSK_SPAWN_EGG, "&6Generar 10 Husk").build(), () -> {
                    genMobs(10, EntityType.HUSK);
                }),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.PIGLIN_BRUTE_SPAWN_EGG, "&6Generar 10 Brutos").build(), () -> {
                    genMobs(10, EntityType.PIGLIN_BRUTE);
                })

        );
    }

    @Override
    public Material menuItem() {
        return Material.ZOMBIE_SPAWN_EGG;
    }

    public void loadLoot(Player player) {
        if (!player.isOp()) {

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
    }

    Random random = new Random();

    @Subcommand("mobs genmobs")
    public void genMobs(int count, EntityType type) {

        List<Location> locations = new ArrayList<>(List.of(
                stringToLocation("33 100 214"),
                stringToLocation("-31 100 214"),
                stringToLocation("1 100 189"),
                stringToLocation("-31 100 134"),
                stringToLocation("33 100 134"),
                stringToLocation("1 100 134"),
                stringToLocation("1 100 80"),
                stringToLocation("-30 100 55"),
                stringToLocation("33 100 54"),
                stringToLocation("22 100 103"),
                stringToLocation("-18 100 108"),
                stringToLocation("-16 100 162"),
                stringToLocation("18 100 160"),
                stringToLocation("1 100 157")
        ));


        for (int i = 0; i < count; i++) {

            Location location = locations.get(random.nextInt(locations.size()));
            World world = location.getWorld();

            world.spawnEntity(location, type);
            world.spawnParticle(Particle.CLOUD, location, 5, 1, 1, 1, 0);
        }

        for (Player t : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendActionbar(t, "&8(!) &fGenerando una orda de &7"+fix(type.name()+"&f."));
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
                    if (!isStaff(t)) {
                        t.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 150, 255, false, false, false));
                    } else {
                        t.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN,1));
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
