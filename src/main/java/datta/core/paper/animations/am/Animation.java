package datta.core.paper.animations.am;

import org.bukkit.entity.Player;

public abstract class Animation {
    public abstract String name();


    public abstract void play(Player... players);
}
