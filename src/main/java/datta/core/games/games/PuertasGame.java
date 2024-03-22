package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.WorldEditService;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.portals.PlayerEntryPortalEvent;
import datta.core.content.portals.Portal;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.games.Game;
import datta.core.services.list.TimerService;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static datta.core.Core.info;
import static datta.core.content.builders.ColorBuilder.stringToLocation;


@CommandAlias("games")
public class PuertasGame extends Game {
    public static String TEXT = "&f¡Elige un portal!\n&f¡RAPIDO!";
    private final List<Cuboid> DOORS = Arrays.asList(
            new Cuboid(stringToLocation("19 109 271"), stringToLocation("19 114 269")),
            new Cuboid(stringToLocation("19 114 283"), stringToLocation("19 109 285")),
            new Cuboid(stringToLocation("-17 109 285"), stringToLocation("-17 114 283")),
            new Cuboid(stringToLocation("-17 114 271"), stringToLocation("-17 109 269"))
    );

    private final List<Cuboid> GLASSES = Arrays.asList(
            new Cuboid(stringToLocation("374 24 495"), stringToLocation("370 20 495")),
            new Cuboid(stringToLocation("399 10 488"), stringToLocation("417 8 488")),
            new Cuboid(stringToLocation("442 8 494"), stringToLocation("431 6 494")),
            new Cuboid(stringToLocation("477 7 494"), stringToLocation("482 4 494"))
    );

    private final List<Cuboid> WIN_SECTORS = Arrays.asList(
            new Cuboid("406 25 358 411 21 351"),
            new Cuboid("367 24 362 377 17 351"),
            new Cuboid("446 31 360 441 28 350"),
            new Cuboid("482 48 359 476 44 351")

    );

    public static final List<Player> WINNER_LIST = new ArrayList<>();
    private final Location WAITING_LOCATION = stringToLocation("1 100 295");

    private BukkitTask task;
    private final Random random = new Random();

    private final Map<Integer, Location> PARKOURS = Map.of(
            1, stringToLocation("479 4 497 180 1"), // bueno
            2, stringToLocation("436 6 497 180 1"), // malo
            3, stringToLocation("408 8 495 180 1"), // bueno
            4, stringToLocation("372 20 497 180 1") // malo
    );

    private final Map<Player, Integer> playerParkourStorage = new HashMap<>();

    private final Integer startAt = 10;
    private final Integer breakAt = 10;

    @Override
    public String name() {
        return "Puertas";
    }

    @Override
    public Location spawn() {
        return stringToLocation("1 101 235");
    }

    @Override
    public String[] gameinfo() {
        return new String[]{
                "&fHay 4 puertas, en dos de ellas te podrás sentir seguro",
                "&fen las restantes no tendrás la misma suerte..."
        };
    }

    @Subcommand("puertas start")
    @Override
    public void start() {
        game(() -> {
            setPortalStatus(true);
            TimerService.bossBarTimer("&d(!) &f¡Tienes &e{time}&f para elegir un portal!", BarColor.PURPLE, BarStyle.SOLID, startAt, () -> {

                for (Player t : Bukkit.getOnlinePlayers()) {
                    joinPlayerToRandomParkour(t);
                    teleportPlayerToParkour(t);
                }

                TimerService.actionbarTimer(breakAt, this::breakAllGlass);
            });
        });
    }

    @Subcommand("puertas end")
    @Override
    public void end() {
        end(() -> {
            setPortalStatus(false);

            if (task != null)
                task.cancel();

            buildAllGlass();
        });
    }

    @Override
    public List<String> scoreboard() {
        return new ArrayList<>(List.of(
                "",
                "&7 ¡Elige un portal y",
                "&7 completa el desafio!",
                "",
                "&f Pasantes: &b%core_pasantes%",
                ""));
    }


    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return Arrays.asList(
                new MenuBuilder.MenuItem(new ItemBuilder(Material.LIME_DYE, "&aAbrir puertas").build(), () -> setPortalStatus(true)),
                new MenuBuilder.MenuItem(new ItemBuilder(Material.RED_DYE, "&cCerrar puertas").build(), () -> setPortalStatus(false))
        );
    }

    @Override
    public ItemStack menuItem() {
        return new ItemBuilder(Material.OAK_DOOR, "&6Puertas")
                .addLore("")
                .addLore(gameinfo())
                .addLore("", "&aClic para ver.")
                .build();
    }

    @Override
    public int menuSlot() {
        return MenuBuilder.slot(3, 2);
    }

    public void setPortalStatus(boolean open) {
        Material material = open ? Material.AIR : Material.BARRIER;
        for (Cuboid door : DOORS) {
            WorldEditService.fill(door.getPoint2(), door.getPoint1(), material);
        }
        info("Las puertas de 'doors' cambiaron a " + (open ? "abiertas" : "cerradas") + ".");
    }


    public void teleportPlayerToParkour(Player player) {
        if (!playerParkourStorage.containsKey(player)) {

            return;
        }

        Integer playerParkour = getPlayerParkour(player);
        Location location = PARKOURS.get(playerParkour).toCenterLocation();

        player.teleport(location);
        SenderUtil.sendSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 2);
        SenderUtil.sendActionbar(player, "&a[✔] Teletransportación completada!");
        SenderUtil.sendMessage(player, "%core_prefix% &eFuiste teletransportado a tu parkour.");
    }

    public void joinPlayerToRandomParkour(Player player) {
        if (playerParkourStorage.containsKey(player)) {
            return;
        }


        int i = random.nextInt(4);
        joinPlayerToParkour(player, i);
    }

    public void joinPlayerToParkour(Player player, Integer id) {
        playerParkourStorage.put(player, id);
        player.teleport(WAITING_LOCATION);

        SenderUtil.sendMessage(player, "%core_prefix% &aEntraste a un parkour correctamente.");
        sendBar("&a[✔] " + player.getName() + "&f entro a un portal");
    }


    public void quitPlayerFromParkour(Player player) {
        playerParkourStorage.remove(player);
    }

    public Integer getPlayerParkour(Player player) {
        return playerParkourStorage.getOrDefault(player, 1);
    }

    public void sendBar(String message) {
        for (Player t : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendActionbar(t, message);
            SenderUtil.sendSound(t, Sound.ENTITY_ITEM_PICKUP, 0.4F, 2);
        }
    }

    public boolean isInParkour(Player player) {
        return playerParkourStorage.containsKey(player);
    }

    @Subcommand("puertas break")
    public void breakAllGlass() {
        for (Cuboid glass : GLASSES) {
            breakGlass(glass);
        }
    }

    @Subcommand("puertas build")
    public void buildAllGlass() {
        for (Cuboid glass : GLASSES) {
            buildGlass(glass);
        }
    }

    public void buildGlass(Cuboid cuboid) {
        Location point1 = cuboid.getPoint1();
        Location point2 = cuboid.getPoint2();

        WorldEditService.fill(point1, point2, Material.RED_STAINED_GLASS);
    }

    public void breakGlass(Cuboid cuboid) {
        Location point1 = cuboid.getPoint1();
        Location point2 = cuboid.getPoint2();

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        String[] materials = {"RED_STAINED_GLASS", "ORANGE_STAINED_GLASS", "YELLOW_STAINED_GLASS", "GREEN_STAINED_GLASS", "LIME_STAINED_GLASS", "AIR"};

        for (int i = 0; i < materials.length; i++) {
            int ticks = i * 15;
            final Material material = Material.valueOf(materials[i]);
            scheduler.runTaskLater(Core.getInstance(), () -> {

                WorldEditService.fill(point1, point2, material);
                Location center = cuboid.getCenter();
                center.getWorld().playSound(center, Sound.BLOCK_GLASS_BREAK, 1F, 1F);
                if (material.toString().contains("AIR")) {
                    center.getWorld().playSound(center, Sound.ENTITY_WITHER_DEATH, 1F, 1F);
                    for (Player t : Bukkit.getOnlinePlayers()) {
                        SenderUtil.sendTitle(t, "&e&l¡CRISTALES ROTOS!", "&8» &a¡BUENA SUERTE! &8«");
                    }
                }

            }, ticks);
        }
    }

    public void playerWin(Player player) {
        boolean contains = WINNER_LIST.contains(player);
        if (contains) {
            return;
        }

        WINNER_LIST.add(player);

        sendBar("&a[✔] " + player.getName() + "&f completo el &eparkour&f con éxito.");
        quitPlayerFromParkour(player);
    }


    public Cuboid cuboid = new Cuboid("355 -1 500 500 2 350");

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (cuboid.isIn(player)) {
            teleportPlayerToParkour(player);
        }

        for (Cuboid winSector : WIN_SECTORS) {
            if (winSector.isIn(player)) {
                playerWin(player);
                break;
            }
        }
    }

    @EventHandler
    public void moveInPortals(PlayerEntryPortalEvent event) {
        Player player = event.getPlayer();
        Portal portal = event.getPortal();
        String id = portal.getId();
        id = id.replace("parkour-", "");

        Integer i = Integer.valueOf(id);
        joinPlayerToParkour(player, i);
    }
}