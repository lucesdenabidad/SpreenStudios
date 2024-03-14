package datta.core.paper.utilities.builders;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class FireworkBuilder {
    private final List<Color> colors = new ArrayList<>();
    private int power = 1;
    private long duration = 3L;

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

    public FireworkBuilder duration(long duration) {
        this.duration = duration;
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
}