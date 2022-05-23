package me.bobthe28th.capturethefart.ctf.items.wizard;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamage;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamageCause;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class WizStickFire extends CTFDoubleCooldownItem {

    public WizStickFire(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Fire Staff", Material.STICK, 3, "Solar Blast", 1.5,false, "Fire Ball", 10,false, player_, plugin_, defaultSlot_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();

        switch (action) {
            case LEFT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
                if (getCooldown(0) == 0) {
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F);
                    double coneHeight = 5.0;
                    double coneRadius = 3.0;
                    Vector shotP = p.getLocation().toVector().add(new Vector(0.0,1.0,0.0));
                    Vector dir = p.getEyeLocation().getDirection().normalize();
                    for (Entity entity : p.getNearbyEntities(10,10,10)) {
                        if (entity instanceof Player pN) {
                            if (pN.hasLineOfSight(p)) {

                                if (Main.CTFPlayers.containsKey(pN) && Main.CTFPlayers.get(pN).getTeam() != player.getTeam()) {
                                    //https://stackoverflow.com/questions/12826117/how-can-i-detect-if-a-point-is-inside-a-cone-or-not-in-3d-space/12826333

                                    Vector hitP = pN.getLocation().toVector().add(new Vector(0.0,1.0,0.0));
                                    double cDist = (hitP.clone().subtract(shotP)).clone().dot(dir);
                                    double cRad = (cDist / coneHeight) * coneRadius;
                                    double orthDist = (hitP.clone().subtract(shotP)).clone().subtract((dir.clone().multiply(cDist))).clone().length();

                                    if (orthDist < cRad && cDist >= 0 && cDist <= coneHeight) {
                                        pN.setFireTicks(70);
                                        Main.customDamageCause.put(pN,new CTFDamage(player, CTFDamageCause.WIZARD_SOLAR_BLAST));
                                        pN.damage(4,p);
                                        player.getPlayer().playSound(player.getPlayer(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,0.5F,0.5F);
                                    }
                                }
                            }
                        }
                    }

                    Location pOrgin = p.getEyeLocation();
                    Vector pDir = pOrgin.getDirection();
                    pDir.normalize();
                    pDir.multiply(0.1);
                    Random rand = new Random();
                    double maxRandomDistance = 0.03;
                    for (int i = 0; i < 20; i++) {
                        Vector pDirR = pDir.clone();
                        pDirR.add(new Vector((rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance,(rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance,(rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance));
                        pDirR.multiply(0.9);
                        Objects.requireNonNull(pOrgin.getWorld()).spawnParticle(Particle.FLAME, pOrgin, 0, pDirR.getX(), pDirR.getY(), pDirR.getZ(), 5.0);
                    }

                    startCooldown(0);
                }
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                if (getCooldown(1) == 0) {
                    startCooldown(1);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0F, 1.0F);
                    Fireball f = p.getWorld().spawn(p.getLocation().add(p.getLocation().getDirection().normalize().multiply(1.5)).add(new Vector(0.0, 1.0, 0.0)), Fireball.class);
                    f.setDirection(p.getLocation().getDirection().normalize());
                    f.setVelocity(p.getEyeLocation().getDirection().multiply(1.7));
                    f.setShooter(p);
                    f.setYield(0);
                    f.setIsIncendiary(false);
                    f.setMetadata("ctfProjectile",new FixedMetadataValue(plugin,true));
                    f.setMetadata("ctfProjectileType",new FixedMetadataValue(plugin,"fireball"));
                    f.setMetadata("playerSent", new FixedMetadataValue(plugin, Objects.requireNonNull(p.getPlayer()).getName()));
                }
                break;
            default:
                break;
        }
    }

}
