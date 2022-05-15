package me.bobthe28th.capturethefart.ctf.items.wizard;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class WizStickEnd extends CTFDoubleCooldownItem {

    public WizStickEnd(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("End Staff", Material.STICK, 4, "Shulker Shot", 2, "Fire Ball", 3, player_, plugin_, defaultSlot_);
        setNoHit(true);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();

        switch (action) {
            case LEFT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
                if (getCooldown(0) == 0) {
                    Player target = (Player) Main.getLookedAtPlayer(p,5);
                    if (target != null) {
                        startCooldown(0);
                        ShulkerBullet bullet = p.launchProjectile(ShulkerBullet.class);
                        bullet.setGravity(false);
                        bullet.setMetadata("ctfProjectile",new FixedMetadataValue(plugin,true));
                        bullet.setMetadata("ctfProjectileType",new FixedMetadataValue(plugin,"shulker"));
                        new BukkitRunnable() {
                            int t = 0;
                            @Override
                            public void run() {
                                if (bullet.isDead() || target.getGameMode() == GameMode.SPECTATOR || t >= 60) {
                                    bullet.getWorld().spawnParticle(Particle.EXPLOSION_LARGE,bullet.getLocation(),3);
                                    bullet.getWorld().playSound(bullet.getLocation(),Sound.ENTITY_SHULKER_BULLET_HIT,1F,1F);
                                    bullet.remove();
                                    this.cancel();
                                } else {
                                    Vector direction = target.getLocation().toVector().subtract(bullet.getLocation().toVector()).add(new Vector(0,1,0));
                                    bullet.setVelocity(bullet.getVelocity().add(direction.normalize().multiply(0.1)).normalize().multiply(0.5));
                                    t++;
                                }
                            }
                        }.runTaskTimer(plugin,0,1L);
                    }
                }
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                if (getCooldown(1) == 0) {
                    startCooldown(1);
                    int innerRadius = 7;
                    int outerRadius = 7;
                    Random rand = new Random();
                    for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(),innerRadius,innerRadius,innerRadius)) {
                        if (e instanceof Player pe && Main.CTFPlayers.containsKey(pe)) {
                            CTFPlayer cp = Main.CTFPlayers.get(pe);
                            if (cp.getTeam() != player.getTeam()) {
                                boolean teleported = false;
                                int iteration = 0;
                                while (!teleported && iteration <= 100) { //TODO bounding box in gamecontroller and align xyz
                                    int x = rand.nextInt((outerRadius+innerRadius) * 2) - (outerRadius+innerRadius);
                                    int z = rand.nextInt((outerRadius+innerRadius) * 2) - (outerRadius+innerRadius);
                                    if ((x > innerRadius || x < -innerRadius) && (z > innerRadius || z < -innerRadius)) {
                                        for (int y = 0; y < outerRadius * 2; y++) {
                                            Location loc = new Location(p.getWorld(),x + p.getLocation().getBlockX(), y - outerRadius + p.getLocation().getBlockY(),z + p.getLocation().getBlockZ(),pe.getLocation().getYaw(),pe.getLocation().getPitch());
                                            if (loc.getBlock().getType().isSolid() && loc.clone().add(new Vector(0.0,1.0,0.0)).getBlock().getType().isAir() && loc.clone().add(new Vector(0.0,2.0,0.0)).getBlock().getType().isAir()) {
                                                pe.getWorld().spawnParticle(Particle.PORTAL,pe.getLocation().add(new Vector(0.0,1.0,0.0)),50,0.1,0.5,0.1);
                                                pe.teleport(loc.clone().add(new Vector(0.0,1.0,0.0)));
                                                pe.getWorld().playSound(pe, Sound.ENTITY_ENDERMAN_TELEPORT,1F,1F);
                                                teleported = true;
                                                break;
                                            }
                                        }
                                    }
                                    iteration ++;
                                }
                            }
                        }
                    }
                }

                break;
        }
    }

}
