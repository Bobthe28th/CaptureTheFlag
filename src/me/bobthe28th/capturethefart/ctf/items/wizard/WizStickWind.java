package me.bobthe28th.capturethefart.ctf.items.wizard;

import java.util.*;

import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;

public class WizStickWind extends CTFDoubleCooldownItem {

    public WizStickWind(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Lightning Staff",Material.STICK,1,"Zap",0.5,"Lightning Strike",20,player_,plugin_, defaultSlot_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();
        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (getCooldown(0) == 0) {
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BEE_HURT, 1.0F, 2.0F);
                    Location orgin = p.getEyeLocation();
                    Vector dir = orgin.getDirection();
                    int dis = 128;
                    dir.multiply(dis);
                    dir.normalize();
                    Random rand = new Random();
                    double maxRandomDistance = 0.05;
                    int randomPoints = 5;
                    dir.add(new Vector((double)(rand.nextInt(randomPoints)-(randomPoints/2))/(randomPoints/maxRandomDistance),(double)(rand.nextInt(randomPoints)-(randomPoints/2))/(randomPoints/maxRandomDistance),(double)(rand.nextInt(randomPoints)-(randomPoints/2))/(randomPoints/maxRandomDistance)));
                    shoot:
                    for (int i = 0; i < dis; i++) {
                        Location loc = orgin.add(dir);
                        for (Entity entity : Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc,0.3,0.3,0.3)) {
                            if (entity.getType() == EntityType.PLAYER) {
                                Player pd = (Player)entity;
                                if (Main.CTFPlayers.containsKey(pd)) {
                                    if (pd != p && Main.CTFPlayers.get(pd).getTeam() != player.getTeam()) {
                                        ArrayList<Player> playersChained = new ArrayList<>();
                                        Main.customDamageCause.put(pd, new Object[]{"wizardZap",p});
                                        pd.damage(1.0, p);
                                        pd.getWorld().playSound(pd.getLocation(), Sound.ENTITY_BEE_DEATH, 1.0F, 2.0F);
                                        playersChained.add(p);
                                        playersChained.add(pd);
                                        lightningToClosestPlayer(pd.getLocation(), playersChained);
                                        break shoot;
                                    }
                                }
                            }
                        }
                        if (loc.getBlock().getType().isSolid()) {
                            break;
                        }
                        loc.getWorld().spawnParticle(Particle.BUBBLE_POP, loc, 1, 0.0, 0.0, 0.0, 0.001);
                        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(Color.fromRGB(255, 255, 0), 0.3F));
                        loc.getWorld().spawnParticle(Particle.BUBBLE_POP, new Location(loc.getWorld(), loc.getX() + dir.getX()/2, loc.getY() + dir.getY()/2, loc.getZ() + dir.getZ()/2), 1, 0.0, 0.0, 0.0, 0.001);
                        loc.getWorld().spawnParticle(Particle.REDSTONE, new Location(loc.getWorld(), loc.getX() + dir.getX()/2, loc.getY() + dir.getY()/2, loc.getZ() + dir.getZ()/2), 1, new Particle.DustOptions(Color.fromRGB(255, 255, 0), 0.3F));
                    }
                    startCooldown(0);
                }
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                if (getCooldown(1) == 0) {

                    int range = 500;

                    Entity target = Main.getLookedAtPlayer(p,1);

                    Block b = p.getTargetBlock(null, range);
                    Location loc = b.getLocation().add(new Vector(0.5, 1, 0.5));

                    if (target != null) {
                        loc = target.getLocation();
                    }

                    p.getWorld().strikeLightningEffect(loc);
                    for (Entity entity : Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc,3.0,3.0,3.0)) {
                        if (entity.getType() == EntityType.PLAYER) {
                            Player pd = (Player) entity;
                            if (Main.CTFPlayers.containsKey(pd)) {
                                if (Main.CTFPlayers.get(pd).getTeam() != player.getTeam()) {
                                    Main.customDamageCause.put(pd, new Object[]{"wizardLightning",p});
                                    pd.damage(6.0);
                                    break;
                                }
                            }
                        }
                    }
                    startCooldown(1);
                }
            default:
                break;
        }
    }

    void lightningToClosestPlayer(Location loc, ArrayList<Player> pChained) {
        double lastDistance = Double.MAX_VALUE;
        Player result = null;
        for(Entity entity : Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc,6.0,6.0,6.0)) {
            if(entity instanceof Player pd) {
                if (Main.CTFPlayers.containsKey(pd)) {
                    if (Main.CTFPlayers.get(pd).getTeam() != player.getTeam()) {
                        double distance = loc.distance(pd.getLocation());
                        if (!pChained.contains(pd)) {
                            if (distance < lastDistance) {
                                lastDistance = distance;
                                result = pd;
                            }
                        }
                    }
                }
            }
        }

        if (result != null) {
            pChained.add(result);

            result.getWorld().playSound(result.getLocation(), Sound.ENTITY_BEE_HURT, 1.0F, 2.0F);

            Main.customDamageCause.put(result,new Object[]{"wizardZap",pChained.get(0)});
            result.damage(1.0, pChained.get(0));

            Location start = loc.add(new Vector(0.0,1.0,0.0));
            Location end = result.getLocation().add(new Vector(0.0,1.0,0.0));
            Vector dir = new Vector(end.getX() - start.getX(), end.getY() - start.getY(), end.getZ() - start.getZ());
            dir.multiply(start.distance(end));
            dir.normalize();
            for (int i = 0; i < Math.ceil(start.distance(end)); i++) {
                Location l = start.add(dir);
                World w = l.getWorld();
                if (w != null) {
                    w.spawnParticle(Particle.BUBBLE_POP, l, 1, 0.0, 0.0, 0.0, 0.001);
                    w.spawnParticle(Particle.REDSTONE, l, 1, new Particle.DustOptions(Color.fromRGB(255, 255, 0), 0.3F));
                    w.spawnParticle(Particle.BUBBLE_POP, new Location(l.getWorld(), l.getX() + dir.getX() / 2, l.getY() + dir.getY() / 2, l.getZ() + dir.getZ() / 2), 1, 0.0, 0.0, 0.0, 0.001);
                    w.spawnParticle(Particle.REDSTONE, new Location(l.getWorld(), l.getX() + dir.getX() / 2, l.getY() + dir.getY() / 2, l.getZ() + dir.getZ() / 2), 1, new Particle.DustOptions(Color.fromRGB(255, 255, 0), 0.3F));
                }
            }
            lightningToClosestPlayer(result.getLocation(),pChained);
        }
    }

}
