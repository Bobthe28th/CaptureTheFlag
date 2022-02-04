package me.bobthe28th.capturethefart.ctf.items.archer;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ArcGhostArrow extends CTFBuildUpItem {

    ArcBow bow;

    public ArcGhostArrow(ArcBow bow_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Ghost Arrow", Material.ARROW, 7, 2, 0, player_, plugin_, defaultSlot_);
        bow = bow_;
    }

    public void shoot(Arrow arrow, Float force) {

        if (!isOnCooldown()) {
            startCooldown();
        }

        player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ARROW_SHOOT, 1, 1);

        new BukkitRunnable() {
            final Location loc = arrow.getLocation();
            final Vector dir = player.getPlayer().getEyeLocation().getDirection().normalize().multiply(force);
            int t = 0;
            public void run() {
                if (t >= 200) {
                    this.cancel();
                }

                for(Player p : Bukkit.getOnlinePlayers()){
                    p.spawnParticle(Particle.REDSTONE,loc,1,0.0,0.0,0.0,0.0, new Particle.DustOptions(Color.ORANGE,0.5F));
                    p.spawnParticle(Particle.REDSTONE,loc,1,0.0,0.0,0.0,0.0, new Particle.DustOptions(Color.BLUE,1F));
                }

                if (loc.getWorld() != null) {
                    for (Entity e : loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5)) {
                        if (e instanceof Player p) {
                            if (Main.CTFPlayers.containsKey(p)) {
                                if (Main.CTFPlayers.get(p).getTeam() != player.getTeam()) {
                                    Main.customDamageCause.put(p,new Object[]{"ghostarrow",player.getPlayer()});
                                    p.damage(3.0,player.getPlayer());
                                    Main.CTFPlayers.get(p).addGlow("ghost");
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                        final Player pg = p;
                                        @Override
                                        public void run() {
                                            if (Main.CTFPlayers.containsKey(pg)) {
                                                Main.CTFPlayers.get(pg).removeGlow("ghost");
                                            }
                                        }
                                    }, 20L);

                                    this.cancel();
                                }
                            }

                        }
                    }
                }
                loc.add(dir);
                t++;
            }
        }.runTaskTimer(plugin,0,1);

        arrow.remove();
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            setSlot(40);
            player.getPlayer().getInventory().setHeldItemSlot(player.getItemSlot(bow));
        }
    }
}
