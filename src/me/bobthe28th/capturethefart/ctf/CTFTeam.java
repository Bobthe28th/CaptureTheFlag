package me.bobthe28th.capturethefart.ctf;

import me.bobthe28th.capturethefart.Main;
import org.bukkit.*;
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
    Location spawnLocation;

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
        t.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
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
        CTFFlag f = null;
        for (CTFFlag flag : Main.CTFFlags) {
            if (flag.getTeam() == this) {
                f = flag;
                break;
            }
        }
        if (f == null) {
            return "???";
        } else {
            return f.getStatus();
        }
    }

    public String getAlive() {
        int alive = 0;
        int total = 0;
        for (CTFPlayer p : Main.CTFPlayers.values()) {
            if (p.getTeam() == this) {
                if (p.getIsAlive()) {
                    alive ++;
                }
                total ++;
            }
        }
        return alive + "/" + total;
    }

    public void setSpawnLocation(Location loc) {
        spawnLocation = loc.clone();
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void scorePoint() {
        points ++;
        if (points >= 3) {
            Main.gameController.gameEnd(this);
        }
        Main.gameController.updateScoreboardGlobal(ScoreboardRowGlobal.POINTS,this);
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
