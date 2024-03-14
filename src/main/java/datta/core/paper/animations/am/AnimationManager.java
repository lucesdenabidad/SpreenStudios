package datta.core.paper.animations.am;

import co.aikar.commands.BukkitCommandManager;

import java.util.ArrayList;
import java.util.List;

public class AnimationManager {

    private static List<String> animationNames = new ArrayList<>();
    private static List<Animation> animations = new ArrayList<>();

    public AnimationManager(BukkitCommandManager manager) {
        manager.getCommandCompletions().registerCompletion("animations", c -> {
            return animationNames;
        });
    }

    public void register(Animation animation) {
        String name = animation.name();

        animationNames.add(name);
        animations.add(animation);
    }

    public static Animation getAnimationOnString(String name) {
        for (Animation animation : animations) {
            if (animation.name().equals(name)) {
                return animation;
            }
        }
        return null;
    }
}
