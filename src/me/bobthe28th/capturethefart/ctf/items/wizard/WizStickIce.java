package me.bobthe28th.capturethefart.ctf.items.wizard;

import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;

import java.util.Objects;

public class WizStickIce extends CTFDoubleCooldownItem {

    public WizStickIce(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Snow Staff",Material.STICK, 2,"Snowball", 0.5,"Snow Chunk", 20, player_,plugin_, defaultSlot_);
        setNoHit(true);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();

        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (getCooldown(0) == 0) {
                    Snowball ball = p.getWorld().spawn(p.getEyeLocation(), Snowball.class);
                    ball.setShooter(p);
                    ball.setVelocity(p.getLocation().getDirection().multiply(1.8));
                    ball.setMetadata("ctfProjectile",new FixedMetadataValue(plugin,true));
                    ball.setMetadata("ctfProjectileType",new FixedMetadataValue(plugin,"snowball"));
                    if (Main.snowBallEffect.get(ball) == null) {
                        Main.snowBallEffect.put(ball, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                            final Snowball s = ball;
                            public void run() {
                                if (!s.isDead()) {
                                    Location l = s.getLocation();
                                    Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.SNOWFLAKE, l, 1, 0.0, 0.0, 0.0, 0.05);
                                } else {
                                    Bukkit.getScheduler().cancelTask(Main.snowBallEffect.get(s));
                                }
                            }
                        }, 0, 1));
                    }
                    startCooldown(0);
                }
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (getCooldown(1) == 0) {
                    Block b = p.getTargetBlock(null, 20);
                    Location loc = b.getLocation().add(new Vector(0.5, 1.0, 0.5));

                    Entity target = Main.getLookedAtPlayer(p,0.0001);

                    if (target != null) {
                        loc = target.getLocation();
                    }

                    int sizeR = 5;
                    int height = 4;
                    for (int z = 0; z < sizeR; z++) {
                        for (int x = 0; x < sizeR; x++) {
                            Location l = loc.clone().add(new Vector(x - (sizeR - 1.0)/2, height, z - (sizeR - 1.0)/2));
                            FallingBlock f = event.getPlayer().getWorld().spawnFallingBlock(l, Bukkit.createBlockData(Material.SNOW_BLOCK));
                            f.setDropItem(false);
                            f.setMetadata("playerSent", new FixedMetadataValue(plugin, Objects.requireNonNull(p.getPlayer()).getName()));
                        }
                    }
                    startCooldown(1);
                }
                break;
            default:
                break;
        }
    }

}
