package me.bobthe28th.capturethefart.ctf.items.wizard;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamage;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamageCause;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class WizStickEnd extends CTFDoubleCooldownItem {

    public WizStickEnd(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("End Staff", Material.STICK, 4, "Shulker Shot", 2, "Phantom", 14, player_, plugin_, defaultSlot_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();

        switch (action) {
            case LEFT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
                if (getCooldown(0) == 0) {
                    CTFPlayer target = Main.getLookedAtCTFPlayer(player,5);
                    if (target != null) {
                        if (target.getTeam() != player.getTeam() && p.hasLineOfSight(target.getPlayer()) && (target.getPlayer().hasPotionEffect(PotionEffectType.GLOWING) || !target.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY))) {
                            startCooldown(0);
                            ShulkerBullet Sbullet = p.launchProjectile(ShulkerBullet.class);
                            Sbullet.setGravity(false);
                            Sbullet.setMetadata("ctfProjectile", new FixedMetadataValue(plugin, true));
                            Sbullet.setMetadata("ctfProjectileType", new FixedMetadataValue(plugin, "shulker"));
                            new BukkitRunnable() {
                                int t = 0;
                                final ShulkerBullet bullet = Sbullet;
                                @Override
                                public void run() {
                                    if (bullet.isDead() || target.getPlayer().getGameMode() == GameMode.SPECTATOR || t >= 80) {
                                        bullet.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, bullet.getLocation(), 3);
                                        bullet.getWorld().playSound(bullet.getLocation(), Sound.ENTITY_SHULKER_BULLET_HIT, 1F, 1F);
                                        bullet.remove();
                                        this.cancel();
                                    } else {
                                        Vector direction = target.getPlayer().getLocation().toVector().subtract(bullet.getLocation().toVector()).add(new Vector(0, 1, 0));
                                        bullet.setVelocity(bullet.getVelocity().add(direction.normalize().multiply(0.1)).normalize().multiply(0.5));
                                        t++;
                                    }
                                }
                            }.runTaskTimer(plugin, 0, 1L);
                        }
                    }
                }
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                if (getCooldown(1) == 0) {
                    startCooldown(1);
                    Phantom Ephantom = player.getPlayer().getWorld().spawn(player.getPlayer().getLocation().setDirection(player.getPlayer().getLocation().getDirection().multiply(new Vector(1,-1,1))).add(new Vector(0.0,1,0.0)), Phantom.class);
                    Ephantom.setSize(5);
                    Ephantom.setAI(false);
                    Ephantom.setGravity(false);
                    Ephantom.setInvulnerable(true);
                    Ephantom.setCollidable(false);
                    Vector direction = player.getPlayer().getLocation().getDirection().normalize();
                    Ephantom.setVelocity(direction);
                    new BukkitRunnable() {
                        int t = 0;
                        final Phantom phantom = Ephantom;
                        final ArrayList<Player> hitPlayers = new ArrayList<>();
                        @Override
                        public void run() {
                            if (phantom.getVelocity().length() < 0.3) {
                                phantom.remove();
                            }
                            if (phantom.isDead() || t >= 400) {
                                phantom.remove();
                                this.cancel();
                            }

                            double radius = 3.0;
                            for (Entity e : phantom.getWorld().getNearbyEntities(phantom.getLocation(),radius,radius,radius)) {
                                if (e instanceof Player pHit && phantom.getLocation().distance(pHit.getLocation().add(new Vector(0,1,0))) <= radius) {
                                    if (Main.CTFPlayers.containsKey(pHit) && Main.CTFPlayers.get(pHit).getTeam() != player.getTeam()) {
                                        if (!hitPlayers.contains(pHit)) {
                                            hitPlayers.add(pHit);
                                            Main.customDamageCause.put(pHit,new CTFDamage(player, CTFDamageCause.WIZARD_PHANTOM));
                                            pHit.damage(8.0,player.getPlayer());
                                            player.getPlayer().playSound(player.getPlayer(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,0.5F,0.5F);
                                        }
                                    }
                                }
                            }
                            phantom.getWorld().spawnParticle(Particle.SPELL_WITCH,phantom.getLocation().add(new Vector(0,0.2,0)),5,0.7,0.2,0.7,0.0);
                            phantom.setVelocity(direction);
                            t++;
                        }
                    }.runTaskTimer(plugin,0,1L);

                }
                break;
        }
    }

}
