package datta.core.games.games;

import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.build.BuildUtils;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.games.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static datta.core.content.builders.ColorBuilder.stringToLocation;

public class Escondite extends Game {
    @Override
    public int endAt() {
        return 0;
    }

    @Override
    public String name() {
        return "Escondite";
    }

    @Override
    public Location spawn() {
        return stringToLocation("560 63 160 180 0");
    }

    @Override
    public void start() {
        game(() ->{
            BuildUtils.set(spawnWalls, Material.AIR);
        });
    }

    @Override
    public void end() {
        BuildUtils.set(spawnWalls, Material.BARRIER);
    }

    @Override
    public List<String> scoreboard() {
        return null;
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return List.of();
    }

    @Override
    public Material menuItem() {
        return Material.NETHER_STAR;
    }

    // # Metodos
    public Cuboid spawnWalls = new Cuboid("556 63 155 565 76 155");

}
