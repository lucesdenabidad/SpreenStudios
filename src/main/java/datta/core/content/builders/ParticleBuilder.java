package datta.core.content.builders;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class ParticleBuilder {


    public static void sendParticleLines(Location point1, Location point2) {
        double space = 0.1;
        World world = point1.getWorld();
        double distance = point1.distance(point2);
        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
        double length = 0;
        for (; length < distance; p1.add(vector)) {
            Location location = new Location(world,p1.getX(),p1.getY(),p1.getZ());

            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1f);
            world.spawnParticle(Particle.REDSTONE,location, 1,0,0,0, dustOptions);
            length += space;
        }
    }


}