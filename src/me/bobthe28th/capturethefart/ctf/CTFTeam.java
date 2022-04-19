package me.bobthe28th.capturethefart.ctf;

import me.bobthe28th.capturethefart.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class CTFTeam {

    int id;
    int points = 0;
    String name;
    ChatColor chatColor;
    Color color;
    Team team;
    Material banner;

    public CTFTeam(Integer id_, String name_, ChatColor chatColor_, Color color_, Material banner_) {
        id = id_;
        name = name_;
        chatColor = chatColor_;
        color = color_;
        banner = banner_;

        Scoreboard s = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        Team t = s.registerNewTeam("ctf" + id);
        t.setDisplayName(name);
        t.setColor(chatColor);
        t.setAllowFriendlyFire(false);
        t.setCanSeeFriendlyInvisibles(true);
        t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        team = t;
    }

    public void setNameTagVisiblity(boolean visible) {
        if (visible) {
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        } else {
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        }
    }

    public String flagStatus() {
        return ""; //TODO
    }

    public void scorePoint() {
        points ++;
        Main.gameController.updateScoreBoard(this,"points");
    }

    public int getPoints() {
        return points;
    }

    public Material getBanner() {
        return banner;
    }

    public String getFormattedName() {
        return chatColor + name + ChatColor.RESET;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public Color getColor() {
        return color;
    }
}
