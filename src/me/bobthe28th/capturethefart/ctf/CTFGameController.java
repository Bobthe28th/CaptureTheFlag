package me.bobthe28th.capturethefart.ctf;

import me.bobthe28th.capturethefart.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class CTFGameController implements Listener {

    static Main plugin;

    Location teamStart;
    Location team0Select; //blue
    Location team1Select; //red
    double teamSelectRadius = 1.5;
    boolean selectingTeam = false;
    int teamSelectTimeMax = 5;
    int teamSelectTime = teamSelectTimeMax;
    BukkitTask teamSelectTimer = null;

    boolean selectingClass = false;
    Location classStart0;
    Location classStart1;

    public CTFGameController(Main plugin_, World w) {
        plugin = plugin_;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        teamStart = new Location(w, 68.5, 81, -226.5);
        teamStart.setYaw(-90F);
        team0Select = new Location(w, 73.5, 81, -231.5);
        team1Select = new Location(w, 73.5, 81, -221.5);
    }

    public void selectTeam() {
        selectingTeam = true;
        for (CTFTeam team : Main.CTFTeams) {
            team.setNameTagVisiblity(true);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Main.CTFPlayers.containsKey(p)) {
                Main.CTFPlayers.get(p).remove();
            }
            p.teleport(teamStart);
            Main.CTFPlayers.put(p, new CTFPlayer(plugin, p));
            Main.fakeClass(p, UUID.fromString("00000000-0000-0000-0000-000000000000"),70, team0Select.clone().add(0.0,3.0,0.0), 0F, 90F, null,Main.CTFTeams[0],plugin);
            Main.fakeClass(p, UUID.fromString("00000000-0000-0000-0000-000000000001"),71, team1Select.clone().add(0.0,3.0,0.0), 0F, 90F, null,Main.CTFTeams[1],plugin);
        }
    }

    void selectTeamJoin(CTFPlayer player, CTFTeam team) {
        player.setTeam(team);
        for (CTFPlayer p : Main.CTFPlayers.values()) {
            if (p.getTeam() == null) {
                stopTeamSelectTimer();
                return;
            }
        }
        startTeamSelectTimer();
    }

    void stopTeamSelectTimer() {
        if (teamSelectTimer != null) {
            teamSelectTimer.cancel();
            for (Player p : Main.CTFPlayers.keySet()) {
                p.sendTitle(" ", "", 0, 0, 0);
            }
        }
    }

    void startTeamSelectTimer() {
        if (teamSelectTimer == null || teamSelectTimer.isCancelled()) {
            teamSelectTime = teamSelectTimeMax;
            teamSelectTimer = new BukkitRunnable() {
                @Override
                public void run() {
                    if (this.isCancelled()) {
                        this.cancel();
                    }

                    if (teamSelectTime <= 0) {
                        for (Player p : Main.CTFPlayers.keySet()) {
                            p.sendTitle(" ", "", 0, 0, 0);
                        }
                        selectingTeam = false;
                        selectClass();
                        this.cancel();
                    }

                    if (!this.isCancelled()) {
                        ChatColor numColor;
                        switch (teamSelectTime) {
                            case 2:
                                numColor = ChatColor.of("#FF4E11");
                                break;
                            case 3:
                                numColor = ChatColor.of("#FF8E15");
                                break;
                            case 4:
                                numColor = ChatColor.YELLOW;
                                break;
                            default:
                                if (teamSelectTime >= 5) {
                                    numColor = ChatColor.GREEN;
                                } else {
                                    numColor = ChatColor.DARK_RED;
                                }
                        }

                        for (Player p : Main.CTFPlayers.keySet()) {
                            p.sendTitle(" ", "Starting in: " + numColor + teamSelectTime + ChatColor.RESET, 0, 30, 0);
                        }
                        teamSelectTime -= 1;
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }

    void selectClass() {
        selectingClass = true;
        for (Player p : Bukkit.getOnlinePlayers()) {
//            p.teleport(classStart);
            Main.despawnFake(p, UUID.fromString("00000000-0000-0000-0000-000000000000"),70);
            Main.despawnFake(p, UUID.fromString("00000000-0000-0000-0000-000000000001"),71);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) { return; }
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            return;
        }
        if (!Main.CTFPlayers.containsKey(event.getPlayer())) { return; }
        CTFPlayer player = Main.CTFPlayers.get(event.getPlayer());
        if (selectingTeam) {
            Location blockPosNoY = event.getTo().getBlock().getLocation().clone().add(0.5,0,0.5);
            blockPosNoY.setY(0);
            Location team0SelectNoY = team0Select;
            team0SelectNoY.setY(0);
            Location team1SelectNoY = team1Select;
            team1SelectNoY.setY(0);
            if (blockPosNoY.distance(team0SelectNoY) < teamSelectRadius) {
                selectTeamJoin(player,Main.CTFTeams[0]);
            } else if (blockPosNoY.distance(team1SelectNoY) < teamSelectRadius) {
                selectTeamJoin(player,Main.CTFTeams[1]);
            } else {
                player.leaveTeam();
                stopTeamSelectTimer();
            }

        }
    }
}
