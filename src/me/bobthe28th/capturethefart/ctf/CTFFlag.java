package me.bobthe28th.capturethefart.ctf;

import me.bobthe28th.capturethefart.Main;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class CTFFlag implements Listener {

    Main plugin;
    CTFTeam team;
    Location pos;
    Location home;
    CTFPlayer carriedPlayer = null;
    int nearby;

    HashMap<CTFPlayer, Double> pickUpTimer = new HashMap<>();
    double pickUpTime = 3.0;
    double returnTime = 5.0;

    public CTFFlag(CTFTeam team_, Main plugin_, Location home_) {
        team = team_;
        plugin = plugin_;
        home = home_;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        setPos(home);

        nearby = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            ArrayList<CTFPlayer> nearCPlayers = new ArrayList<>();
            if (pos != null && pos.getWorld() != null && carriedPlayer == null) {
                for (Entity ps : pos.getWorld().getNearbyEntities(pos, 1.5, 1.5, 1.5)) {
                    if (ps instanceof Player p && p.getGameMode() != GameMode.SPECTATOR) {
                        if (Main.CTFPlayers.containsKey(p)) {
                            CTFPlayer cp = Main.CTFPlayers.get(p);
                            if (cp.getTeam() != null) {

                                nearCPlayers.add(cp);

                                if (cp.getTeam() != team) {

                                    if (pickUpTimer.containsKey(cp)) {

                                        pickUpTimer.put(cp,pickUpTimer.get(cp) - 0.1);

                                        if (pickUpTimer.get(cp) <= 0.0) {

                                            for (Player player : Bukkit.getOnlinePlayers()) {
                                                player.sendTitle(" ", cp.getFormattedName() + " picked up the " + team.getFormattedName() + team.getChatColor() + "'s " + "Flag" + ChatColor.RESET, 10, 20, 5);
                                            }
                                            p.setLevel(0);
                                            p.setExp(0.0F);
                                            pickUpTimer.remove(cp);
                                            carriedPlayer = cp;
                                            cp.pickupFlag(this);
                                            pickUpTimer.clear();
                                            pos.getBlock().setType(Material.AIR);
                                        } else {
                                            p.setLevel((int)Math.ceil(pickUpTimer.get(cp)));
                                            p.setExp((float)(pickUpTimer.get(cp) % 1.0));
                                        }

                                    } else {
                                        pickUpTimer.put(cp,pickUpTime);
                                        p.setLevel((int)Math.ceil(pickUpTime));
                                        p.setExp(1.0F);
                                    }
                                } else {
                                    if (!isHome()) {
                                        if (pickUpTimer.containsKey(cp)) {
                                            pickUpTimer.put(cp, pickUpTimer.get(cp) - 0.1);

                                            if (pickUpTimer.get(cp) <= 0.0) {

                                                for (Player player : Bukkit.getOnlinePlayers()) {
                                                    player.sendTitle(" ", cp.getFormattedName() + " returned the " + team.getFormattedName() + team.getChatColor() + "'s " + "Flag" + ChatColor.RESET, 10, 20, 5);
                                                }
                                                p.setLevel(0);
                                                p.setExp(0.0F);
                                                pickUpTimer.remove(cp);
                                                setPos(home);
                                                pickUpTimer.clear();
                                            } else {
                                                p.setLevel((int) Math.ceil(pickUpTimer.get(cp)));
                                                p.setExp((float) (pickUpTimer.get(cp) % 1.0));
                                            }
                                        } else {
                                            pickUpTimer.put(cp, returnTime);
                                            p.setLevel((int) Math.ceil(returnTime));
                                            p.setExp(1.0F);
                                        }
                                    } else {
                                        nearCPlayers.remove(cp);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (carriedPlayer != null) {
                for (CTFFlag f : Main.CTFFlags) {
                    if (carriedPlayer != null) {
                        if (f.getTeam() == carriedPlayer.getTeam()) {
                            if (f.atHome(carriedPlayer) && carriedPlayer.isCarringFlag()) {
                                carriedPlayer.captureFlag();
                            }
                        }
                    }
                }
            }

            ArrayList<CTFPlayer> toRemove = new ArrayList<>();
            pickUpTimer.forEach((k, v) -> {
                if (!nearCPlayers.contains(k)) {
                    k.getPlayer().setLevel(0);
                    k.getPlayer().setExp(0.0F);
                    toRemove.add(k);
                }
            });

            for (CTFPlayer k : toRemove) {
                pickUpTimer.remove(k);
            }

        }, 0, 2);
    }

    public CTFTeam getTeam() {
        return team;
    }

    public void setPos(Location loc) {
        carriedPlayer = null;
        if (pos != null) {
            pos.getBlock().setType(Material.AIR);
        }
        loc.getBlock().setType(team.getBanner());
        loc.setX(Math.floor(loc.getX()));
        loc.setY(Math.floor(loc.getY()));
        loc.setZ(Math.floor(loc.getZ()));
        pos = loc.clone().add(new Vector(0.5,0,0.5));
    }

    public void capture(CTFPlayer cp) {
        carriedPlayer = null;
        setPos(home);
        team.scorePoint();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(" ", cp.getFormattedName() + " captured the " + team.getFormattedName() + team.getChatColor() + "'s " + "Flag" + ChatColor.RESET, 10, 20, 5);
        }
    }

    public boolean atHome(CTFPlayer cp) {
        Location lHome = home.clone();
        lHome.setX(Math.floor(lHome.getX()));
        lHome.setY(Math.floor(lHome.getY()));
        lHome.setZ(Math.floor(lHome.getZ()));
        lHome.add(new Vector(0.5,0,0.5));
        if (lHome.getWorld() != null) {
            return lHome.getWorld().getNearbyEntities(lHome, 1.5, 1.5, 1.5).contains(cp.getPlayer());
        }
        return false;
    }

    public boolean isHome() {
        Location lHome = home.clone();
        lHome.setX(Math.floor(lHome.getX()));
        lHome.setY(Math.floor(lHome.getY()));
        lHome.setZ(Math.floor(lHome.getZ()));
        lHome.add(new Vector(0.5,0,0.5));
        return carriedPlayer == null && pos.equals(lHome);
    }

    public void fall(Location loc) {
        loc.setDirection(new Vector(0.0,-1.0,0.0));
        carriedPlayer = null;
        BlockIterator blocksToAdd = new BlockIterator(loc,0,200);
        Location blockToAdd = null;
        while(blocksToAdd.hasNext()) {
            blockToAdd = blocksToAdd.next().getLocation();
            if (blockToAdd.getBlock().getType().isSolid()) {
                setPos(blockToAdd.add(new Vector(0.0,1.0,0.0)));
                return;
            }
        }
        if (blockToAdd != null) {
            setPos(blockToAdd.add(new Vector(0.0,1.0,0.0)));
        }
    }
}
