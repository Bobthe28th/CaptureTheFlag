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

import java.lang.reflect.Constructor;
import java.util.UUID;

public class CTFGameController implements Listener {

    static Main plugin;

    Location teamStart;
    Location[] teamSelect;
    double teamSelectRadius = 1.5;
    boolean selectingTeam = false;
    int teamSelectTimeMax = 5;
    int teamSelectTime = teamSelectTimeMax;
    double teamSelectY;
    BukkitTask teamSelectTimer = null;

    boolean selectingClass = false;
    Location[] classStart;
    Location[][] classSelect;
    double classSelectY;
    double classSelectRadius = 1.5;
    BukkitTask classSelectTimer = null;
    int classSelectTimeMax = 5;
    int classSelectTime = teamSelectTimeMax;

    public CTFGameController(Main plugin_, World w) {
        plugin = plugin_;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        teamStart = new Location(w, 68.5, 81, -226.5);
        teamStart.setYaw(-90F);
        teamSelect = new Location[Main.CTFTeams.length];
        classStart = new Location[Main.CTFClasses.length];
        classSelect = new Location[Main.CTFTeams.length][Main.CTFClasses.length];
        teamSelectY = 81;
        teamSelect[0] = new Location(w, 73.5, 81, -231.5);
        teamSelect[1] = new Location(w, 73.5, 81, -221.5);
        classSelectY = 81;
        classStart[0] = new Location(w, 57.5, 81, -261.5);
        classStart[1] = new Location(w, 57.5, 81, -244.5);

        //"WizardFire","WizardIce","WizardWind","Paladin","Demo","Builder","Archer","Assassin","Alchemist"
        classSelect[0] = new Location[]{
                new Location(w,63.5, 81, -266.5),
                new Location(w,68.5, 81, -266.5),
                new Location(w,73.5, 81, -266.5),
                new Location(w,73.5, 81, -261.5),
                new Location(w,73.5, 81, -256.5),
                new Location(w,68.5, 81, -256.5),
                new Location(w,63.5, 81, -256.5)
        };
        classSelect[1] = new Location[]{
                new Location(w,63.5, 81, -249.5),
                new Location(w,68.5, 81, -249.5),
                new Location(w,73.5, 81, -249.5),
                new Location(w,73.5, 81, -244.5),
                new Location(w,73.5, 81, -239.5),
                new Location(w,68.5, 81, -239.5),
                new Location(w,63.5, 81, -239.5)
        };
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
            Main.fakeClass(p, UUID.fromString("00000000-0000-0000-0000-000000000000"),70, teamSelect[0].clone().add(0.0,3.0,0.0), 0F, 90F, null,Main.CTFTeams[0],plugin);
            Main.fakeClass(p, UUID.fromString("00000000-0000-0000-0000-000000000001"),71, teamSelect[1].clone().add(0.0,3.0,0.0), 0F, 90F, null,Main.CTFTeams[1],plugin);
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

    void selectClassJoin(CTFPlayer player, Class<?> ctfclass) {
        if (ctfclass == null) {
            player.setSelectingWizard(true);
        } else {
            if (player.getpClass() == null) {
                try {
                    Constructor<?> constructor = ctfclass.getConstructor(CTFPlayer.class, Main.class);
                    CTFClass c = (CTFClass) constructor.newInstance(player, plugin);
                    player.setClass(c);
                } catch (Exception e) {
                    return;
                }
            }
        }
        player.setCanUse(false);
        for (CTFPlayer p : Main.CTFPlayers.values()) {
            if (p.getpClass() == null) {
                stopClassSelectTimer();
                return;
            }
        }
        startClassSelectTimer();
    }

    void stopTeamSelectTimer() {
        if (teamSelectTimer != null) {
            teamSelectTimer.cancel();
            for (Player p : Main.CTFPlayers.keySet()) {
                p.sendTitle(" ", "", 0, 0, 0);
            }
        }
    }

    void stopClassSelectTimer() {
        if (classSelectTimer != null) {
            classSelectTimer.cancel();
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

    void startClassSelectTimer() {
        if (classSelectTimer == null || classSelectTimer.isCancelled()) {
            classSelectTime = classSelectTimeMax;
            classSelectTimer = new BukkitRunnable() {
                @Override
                public void run() {
                    if (this.isCancelled()) {
                        this.cancel();
                    }

                    if (classSelectTime <= 0) {
                        for (Player p : Main.CTFPlayers.keySet()) {
                            p.sendTitle(" ", "", 0, 0, 0);
                        }
                        selectingClass = false;
                        //TODO
                        this.cancel();
                    }

                    if (!this.isCancelled()) {
                        ChatColor numColor;
                        switch (classSelectTime) {
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
                                if (classSelectTime >= 5) {
                                    numColor = ChatColor.GREEN;
                                } else {
                                    numColor = ChatColor.DARK_RED;
                                }
                        }

                        for (Player p : Main.CTFPlayers.keySet()) {
                            p.sendTitle(" ", "Starting in: " + numColor + classSelectTime + ChatColor.RESET, 0, 30, 0);
                        }
                        classSelectTime -= 1;
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }

    void selectClass() {
        selectingClass = true;
        for (Player p : Bukkit.getOnlinePlayers()) {
            Main.despawnFake(p, UUID.fromString("00000000-0000-0000-0000-000000000000"),70);
            Main.despawnFake(p, UUID.fromString("00000000-0000-0000-0000-000000000001"),71);
        }

        for (CTFPlayer p : Main.CTFPlayers.values()) {
            if (p.getTeam() != null) {
                p.getPlayer().teleport(classStart[p.getTeam().getId()]);
            }
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
        Location blockPos = event.getTo().getBlock().getLocation().clone().add(0.5,0,0.5);

        if (selectingTeam) {
            blockPos.setY(teamSelectY);

            boolean inSelect = false;
            for (int i = 0; i < teamSelect.length; i++) {
                if (blockPos.distance(teamSelect[i]) < teamSelectRadius) {
                    selectTeamJoin(player,Main.CTFTeams[i]);
                    inSelect = true;
                }
            }
            if (!inSelect) {
                player.leaveTeam();
                stopTeamSelectTimer();
            }

        } else if (selectingClass) {
            blockPos.setY(classSelectY);
            int teamid = player.getTeam().getId();

            boolean inSelect = false;
            for (int i = 0; i < classSelect[teamid].length; i++) {
                if (blockPos.distance(classSelect[teamid][i]) < classSelectRadius) {
                    if (i == 0) {
                        selectClassJoin(player,null);
                    } else {
                        selectClassJoin(player,Main.CTFClasses[i + 3]); //+3 because wizards
                    }
                    inSelect = true;
                }
            }
            if (!inSelect) {
                player.setSelectingWizard(false);
                player.leaveClass();
                stopClassSelectTimer();
            }
        }
    }
}
