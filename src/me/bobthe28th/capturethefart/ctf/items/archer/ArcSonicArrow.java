package me.bobthe28th.capturethefart.ctf.items.archer;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

public class ArcSonicArrow extends CTFBuildUpItem {

    ArcBow bow;

    public ArcSonicArrow(ArcBow bow_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Sonic Arrow", Material.ARROW, 3, 12, 0, player_, plugin_, defaultSlot_);
        bow = bow_;
    }

    public void shoot(Arrow arrow, CTFPlayer player) {
        if (!isOnCooldown()) {
            startCooldown();
        }
        arrow.setCritical(false);
        new BukkitRunnable() {
            public void run() {
                if (arrow.isDead() || arrow.isOnGround()) {
                    if (arrow.isOnGround()) {
                        land(arrow.getLocation(), arrow);
                    }
                    this.cancel();
                }
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.spawnParticle(Particle.GLOW,arrow.getLocation(),1,0.0,0.0,0.0,0.0);
                }
            }
        }.runTaskTimer(plugin,0,1);
    }

    public void land(Location loc, Arrow arrow) {
        double radius = 5.0;
        if (loc.getWorld() != null) {
            for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                if (e instanceof Player p) {
                    if (p.getLocation().distance(loc) <= radius) {
                        if (Main.CTFPlayers.containsKey(p)) {
                            Main.CTFPlayers.get(p).addGlow("sonic");
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                final Player pg = p;
                                @Override
                                public void run() {
                                    if (Main.CTFPlayers.containsKey(pg)) {
                                        Main.CTFPlayers.get(pg).removeGlow("sonic");
                                    }
                                    if (!arrow.isDead()) {
                                        arrow.remove();
                                    }
                                }
                            }, 20L);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onHold(PlayerItemHeldEvent event) {
        setSlot(40);
        player.getPlayer().getInventory().setHeldItemSlot(player.getItemSlot(bow));
    }
}
