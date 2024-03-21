package datta.core.content.builders;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class FireworkBuilder {
    private final List<Color> colors = new ArrayList<>();
    private int power = 1;

    public FireworkBuilder colors(Color... colors) {
        for (Color color : colors) {
            this.colors.add(color);
        }
        return this;
    }

    public FireworkBuilder power(int power) {
        this.power = power;
        return this;
    }

    public void spawn(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(power);
        FireworkEffect.Builder builder = FireworkEffect.builder();
        for (Color color : colors) {
            builder.withColor(color);
        }
        builder.with(FireworkEffect.Type.BALL);
        meta.addEffect(builder.build());
        firework.setFireworkMeta(meta);
    }

        public void spawn(Location location, Player player) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(power);
        FireworkEffect.Builder builder = FireworkEffect.builder();
        for (Color color : colors) {
            builder.withColor(color);
        }
        builder.with(FireworkEffect.Type.BALL);
        meta.addEffect(builder.build());
        firework.addPassenger(player);
        firework.setFireworkMeta(meta);
    }

    public void spawnAndDetonate(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(power);
        FireworkEffect.Builder builder = FireworkEffect.builder();
        for (Color color : colors) {
            builder.withColor(color);
        }
        builder.with(FireworkEffect.Type.BALL);
        meta.addEffect(builder.build());
        firework.setFireworkMeta(meta);
        firework.detonate();
    }
}