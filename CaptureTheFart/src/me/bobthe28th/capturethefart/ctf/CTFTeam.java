package me.bobthe28th.capturethefart.ctf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class CTFTeam {

    int id;
    String name;
    ChatColor color;
    Team team;
    Material banner;

    public CTFTeam(Integer id_, String name_, ChatColor color_, Material banner_) {
        id = id_;
        name = name_;
        color = color_;
        banner = banner_;

        Scoreboard s = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        Team t = s.registerNewTeam("ctf" + id);
        t.setDisplayName(name);
        t.setColor(color);
        t.setAllowFriendlyFire(false);
        t.setCanSeeFriendlyInvisibles(true);
        t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        team = t;
    }

    public Material getBanner() {
        return banner;
    }

    public String getFormattedName() {
        return color + name + ChatColor.RESET;
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

    public ChatColor getColor() { return color; }
}
