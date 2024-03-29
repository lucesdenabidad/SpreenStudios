package datta.core.services.list;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import datta.core.Core;
import datta.core.content.score.ScoreHolder;
import datta.core.services.Service;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@CommandPermission("spreenstudios.scoreboard")
@CommandAlias("scoreboardservice|score|scoreboard|sb")
public class ScoreboardService extends Service {

    public String title;
    public List<String> lines;
    public ScoreHolder scoreHolder;


    public ScoreboardService(String title, List<String> lines) {
        this.title = title;
        this.lines = lines;
        scoreHolder = new ScoreHolder(instance(), title, lines);

    }

    @Override
    public Core instance() {
        return Core.getInstance();
    }

    @Override
    public String name() {
        return "scoreboard";
    }

    @Override
    public String[] info() {
        return new String[0];
    }

    @Override
    public void onLoad() {
        register(true, true);

        changeScore(title, lines);
    }

    @Override
    public void onUnload() {
        if (scoreHolder.task != null) scoreHolder.task.cancel();

        scoreHolder.hideScoreboard();
    }


    public void changeScore(String title, List<String> lines) {
        if (scoreHolder.task != null) scoreHolder.task.cancel();

        for (Player t : Bukkit.getOnlinePlayers()) {
            scoreHolder.removePlayer(t);
        }

        this.title = title;
        this.lines = lines;

        if (lines != null && !lines.isEmpty()) {
            scoreHolder = new ScoreHolder(instance(), title, lines);
            scoreHolder.start(0, 5L);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                scoreHolder.removePlayer(onlinePlayer);
            }
        }
    }
}