package me.bobthe28th.capturethefart.ctf.items.wizard;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class WizBookFire extends CTFDoubleCooldownItem {



    public WizBookFire(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Fire Tome", Material.BOOK, 3, "Fire Wall", 13, "Attack Boost", 23, player_, plugin_, defaultSlot_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();

        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (getCooldown(0) == 0) {
                    startAction(0);
                    new BukkitRunnable() {
                        int t = 0;
                        final int s = 2;
                        final Location origin = p.getLocation();
                        final Vector direction = p.getLocation().getDirection().setY(0).normalize();
                        int lastBlockY = origin.getBlockY();

                        public void run() {
                            t++;
                            if (t > 60) {
                                startCooldown(0);
                                this.cancel();
                            }

                            if (t % s == 0) {

                                BlockIterator blocksToAdd = new BlockIterator(origin.clone().add(direction.clone().multiply((t+s) / s)).setDirection(new Vector(0.0, -1.0, 0.0)).add(new Vector(0.0, lastBlockY + 3.0, 0.0)),0,100);

                                Location blockToAdd;
                                while(blocksToAdd.hasNext()) {
                                    blockToAdd = blocksToAdd.next().getLocation();
                                    Location fire = blockToAdd.clone().add(new Vector(0.0,1.0,0.0));
                                    if (blockToAdd.getBlock().getType().isSolid() && fire.getBlock().isEmpty()) {
                                        if (fire.getBlockY() < lastBlockY + 3 && fire.getBlockY() > lastBlockY - 3) {
                                            lastBlockY = fire.getBlockY();
                                            fire.getBlock().setType(Material.FIRE);
                                        } else {
                                            startCooldown(0);
                                            this.cancel();
                                        }
                                        return;
                                    }
                                }
                            }


                        }
                    }.runTaskTimer(plugin,0,1);
                }
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (getCooldown(1) == 0) {
                    startCooldown(1);
                    double radius = 5.0;
                    for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(), radius, radius, radius)) {
                        if (e instanceof Player a) {
                            if (a.getLocation().distance(p.getLocation()) <= radius) {
                                if (Main.CTFPlayers.containsKey(a)) {
                                    if (Main.CTFPlayers.get(a).getTeam() == player.getTeam()) {
                                        attackBoost(Main.CTFPlayers.get(a));
                                    }
                                }
                            }
                        }
                    }

                }
                break;
            default:
                break;

        }
    }

    void attackBoost(CTFPlayer p) {

        p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE,0,false,false,true));

        new BukkitRunnable() {
            int t = 0;
            final int maxTime = 60;
            final int loops = 2;
            final double ringSize = 0.75;
            public void run() {
                if (t > maxTime) {
                    this.cancel();
                } else {
                    Location loc = p.getPlayer().getLocation().clone();
                    double angle = ((t % (((double)maxTime)/loops)) / (((double)maxTime)/loops)) * 2 * Math.PI;
                    loc.add(new Vector(Math.cos(angle) * ringSize, 2 * (((double)t)/maxTime), Math.sin(angle) * ringSize));

                    Location loc2 = p.getPlayer().getLocation().clone();
                    loc2.add(new Vector(Math.sin(angle) * ringSize,2 * ((double)(maxTime - t)/maxTime),Math.cos(angle) * ringSize));

                    for (Player a : Bukkit.getOnlinePlayers()) {
                        a.spawnParticle(Particle.FLAME,loc,0,0.0,0.0,0.0);
                        a.spawnParticle(Particle.FLAME,loc2,0,0.0,0.0,0.0);
                    }
                    t++;
                }
            }
        }.runTaskTimer(plugin,0,1);

//        int points = 20;
//        double ringSize = 0.75;
//        for (int i = 0; i < points; i ++) {
//            Location loc = p.getPlayer().getLocation().clone();
//            double angle = (double)i/points * 2 * Math.PI;
//            loc.add(new Vector(Math.cos(angle) * ringSize, 0.0, Math.sin(angle) * ringSize));
//            for (Player a : Bukkit.getOnlinePlayers()) {
//                a.spawnParticle(Particle.FLAME,loc,0,0.0,0.4,0.0);
//            }
//        }
    }

}
