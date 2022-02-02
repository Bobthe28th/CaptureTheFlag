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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

public class ArcSonicArrow extends CTFBuildUpItem {

    ArcBow bow;

    public ArcSonicArrow(ArcBow bow_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Sonic Arrow", Material.ARROW, 5, 2, 0, player_, plugin_, defaultSlot_);
        bow = bow_;
    }

    public void shoot(Arrow arrow) {
        if (!isOnCooldown()) {
            startCooldown();
        }
        arrow.setMetadata("dontKillOnLand", new FixedMetadataValue(plugin, true));
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

        for(Player p : Bukkit.getOnlinePlayers()){
            p.spawnParticle(Particle.GLOW_SQUID_INK,loc,1,0.0,0.0,0.0,0.0);
        }

        double radius = 5.0;
        long time = 20L;
        if (loc.getWorld() != null) {
            for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                if (e instanceof Player p) {
                    if (p.getLocation().distance(loc) <= radius) {
                        if (Main.CTFPlayers.containsKey(p)) {
                            if (Main.CTFPlayers.get(p).getTeam() != player.getTeam()) {
                                Main.CTFPlayers.get(p).addGlow("sonic");
                            }
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                final Player pg = p;
                                @Override
                                public void run() {
                                    if (Main.CTFPlayers.containsKey(pg)) {
                                        Main.CTFPlayers.get(pg).removeGlow("sonic");
                                    }
                                }
                            }, time);
                        }
                    }
                }
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, arrow::remove, time);
        }
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            setSlot(40);
            player.getPlayer().getInventory().setHeldItemSlot(player.getItemSlot(bow));
        }
    }

//    @Override
//    public void onHold(PlayerItemHeldEvent event) {
//        setSlot(40);
//        player.getPlayer().getInventory().setHeldItemSlot(player.getItemSlot(bow));
//    }
}
