package datta.core.paper.utilities.builders;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class MenuBuilder implements Listener {
    private final JavaPlugin plugin;
    private final Map<Player, Menu> openMenus = new HashMap<>();
    private final Map<Player, Runnable> menuRunnables = new HashMap<>();
    public static int updatedInt = 1;

    public MenuBuilder(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startMenuUpdater();
    }

    public void createMenu(Player player, String title, int size, boolean filled) {
        Menu menu = new Menu(title, size);

        if (filled) {
            for (int i = 0; i < size; i++) {
                if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                    menu.setItem(i, new MenuItem(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE, "&f", "&f").build(), () -> {
                    }));
                }
            }
        }

        player.openInventory(menu.getInventory());
        openMenus.put(player, menu);
    }

    public void setItem(Player player, int slot, ItemStack itemStack, Runnable action) {
        if (openMenus.containsKey(player)) {
            Menu menu = openMenus.get(player);
            if (slot >= 0 && slot < menu.getInventory().getSize()) {
                menu.setItem(slot, new MenuItem(itemStack, action));
            }
        }
    }

    public void setContents(Player player, Runnable runnable) {
        runnable.run();
        menuRunnables.put(player, runnable);
    }

    public void startMenuUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updatedInt++;
                for (Player player : menuRunnables.keySet()) {
                    Runnable runnable = menuRunnables.get(player);
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    public void forceUpdateMenu(Player player) {
        Menu menu = openMenus.get(player);
        if (menu != null) {
            Runnable runnable = menuRunnables.get(player);
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (openMenus.containsKey(player)) {
            Menu menu = openMenus.get(player);
            menu.handleClick(player, event.getRawSlot());
            forceUpdateMenu(player);
            event.setCancelled(true);
        }
    }

    public void closeInventoryActions(Player player) {
        openMenus.remove(player);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            closeInventoryActions(player);
        }
    }

    public void removeMenu(Player player) {
        openMenus.remove(player);
    }

    public static int slot(int x, int y) {
        return (y - 1) * 9 + (x - 1);
    }

    public static void addListToMenu(Player player, MenuBuilder menuBuilder, List<Object> list, int page, int[] allowedSlots, ItemStack itemStack, Runnable runnable) {
        for (int i = 0; i < allowedSlots.length; i++) {
            int slot = allowedSlots[i];
            int index = page * allowedSlots.length + i;
            if (index < list.size()) {
                Object altura = list.get(index);
                menuBuilder.setItem(player, slot, itemStack, runnable);
            }
        }
    }

    public static Object getFromList(List list, int index) {
        Object o = list.get(index);
        return o;
    }

    public class Menu {
        @Getter
        private final Inventory inventory;
        @Getter
        private final MenuItem[] items;
        private long lastExecutionTime;

        private static final long COOLDOWN = 20L * 5;

        public Menu(String title, int size) {
            this.inventory = Bukkit.createInventory(null, size, title);
            this.items = new MenuItem[size];
        }

        public void setItem(int slot, MenuItem menuItem) {
            inventory.setItem(slot, menuItem.getItemStack());
            items[slot] = menuItem;
        }

        public void open(Player player) {
            player.openInventory(inventory);
        }

        public void handleClick(Player player, int slot) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastExecutionTime >= COOLDOWN) {
                if (slot >= 0 && slot < items.length && items[slot] != null) {
                    items[slot].executeAction(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                    this.lastExecutionTime = currentTime;
                }
            }
        }
    }

    public class MenuItem {
        private final ItemStack itemStack;
        private final Runnable action;

        public MenuItem(ItemStack itemStack, Runnable action) {
            this.itemStack = itemStack;
            this.action = action;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public Runnable getAction() {
            return action;
        }

        public void executeAction(Player player) {
            player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
            this.action.run();
        }
    }
}