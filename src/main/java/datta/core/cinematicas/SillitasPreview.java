package datta.core.cinematicas;

import datta.core.Core;
import datta.core.content.CoreTask;
import datta.core.services.list.CinemaService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SillitasPreview {
    public static void startAll() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            start(onlinePlayer);
        }
    }

    public static void start(Player player) {
        CinemaService cinemaService = (CinemaService) Core.getInstance().commandService.serviceFromName("cinema");
        int delay = 30;

        cinemaService.play(player, "simon-1");
        CoreTask.runTask(() -> {
            cinemaService.play(player, "simon-2");
            CoreTask.runTask(() -> {
                cinemaService.play(player, "simon-3");

            }, delay);

        }, delay);
    }
}