package me.bobthe28th.capturethefart.ctf.items.Wizard;

import java.util.Objects;
import java.util.Random;

import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.*;
import org.bukkit.Particle.DustTransition;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;

public class WizBookWind extends CTFDoubleCooldownItem {

    Main plugin;
    CTFPlayer player;

    public WizBookWind(CTFPlayer player_, Main plugin_) {
        super("Wind Tome",Material.BOOK,1,"Wind Blast",15,"Wind Jump",15,player_,plugin_);
        plugin = plugin_;
        player = player_;
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        switch (action) {
            case RIGHT_CLICK_BLOCK:
                if (getCooldown(1) == 0 && block != null) {
                    makeTornado(block.getLocation().add(new Vector(0.0, 1.0, 0.0)));
                    startCooldown(1);
                }
                break;
            case LEFT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
                if (getCooldown(0) == 0) {
                    Location l = player.getEyeLocation().add(player.getEyeLocation().clone().getDirection().normalize().multiply(2));
                    for (Entity entity : player.getWorld().getNearbyEntities(l,2.0,2.0,2.0)) {
                        if (entity.getType() == EntityType.PLAYER && entity != player) {
                            Player pd = (Player)entity;
                            pd.setVelocity(pd.getVelocity().add(player.getEyeLocation().getDirection().normalize().multiply(2)));
                        }
                    }

                    Location pOrgin = player.getEyeLocation();
                    Vector pDir = pOrgin.getDirection();
                    pDir.multiply(10);
                    pDir.normalize();

                    Random rand = new Random();
                    double maxRandomDistance = 0.3;
                    for (int i = 0; i < 10; i++) {
                        Vector pDirR = pDir.clone();
                        pDirR.add(new Vector((rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance,(rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance,(rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance));
                        Objects.requireNonNull(pOrgin.getWorld()).spawnParticle(Particle.CLOUD, pOrgin, 0, pDirR.getX(), pDirR.getY(), pDirR.getZ());
                    }
                    for (int i = 0; i < 10; i++) {
                        Vector pDirR = pDir.clone();
                        pDirR.add(new Vector((rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance,(rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance,(rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance));
                        pOrgin.getWorld().spawnParticle(Particle.END_ROD, pOrgin, 0, pDirR.getX(), pDirR.getY(), pDirR.getZ());
                    }

                    player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().normalize().multiply(-2)));
                    startCooldown(0);
                }
                break;
            default:
                break;
        }

    }

    void makeTornado(Location pos) {
        int tid = Main.tornado.size();
        Main.tornado.add(tid, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            final Location tornadoLoc = pos.add(new Vector(0.5, 0.5, 0.5));
            int tornadoTicks = 0;
            final int id = tid;
            float angle = 0;
            final int time = 20;
            final int height = 15;
            final int particleAmount = 16;
            double radius = 1;
            final int whiteHangTime = 5;
            public void run() {
                for (int i = 0; i < particleAmount; i++) {
                    double x = radius * Math.sin((angle + i * (360.0 / particleAmount)) * (Math.PI / 180));
                    double z = radius * Math.cos((angle + i * (360.0 / particleAmount)) * (Math.PI / 180));
                    double r = 0.70196078431;
                    double g = 0.82352941176;
                    double b = 0.96862745098;
                    r = Math.min(r + (tornadoTicks) * ((1.0 - r) / (time - whiteHangTime)), 1.0);
                    g = Math.min(g + (tornadoTicks) * ((1.0 - g) / (time - whiteHangTime)), 1.0);
                    b = Math.min(b + (tornadoTicks) * ((1.0 - b) / (time - whiteHangTime)), 1.0);
                    World w = tornadoLoc.getWorld();
                    if (w != null) {
                        w.spawnParticle(Particle.SPELL_MOB, tornadoLoc.getX() + x, tornadoLoc.getY() - 0.5 + tornadoTicks / ((double) time / height), tornadoLoc.getZ() + z, 0, 0.001, r, g, b);
                        w.spawnParticle(Particle.SPELL_MOB_AMBIENT, tornadoLoc.getX() + x, tornadoLoc.getY() - 0.5 + tornadoTicks / ((double) time / height), tornadoLoc.getZ() + z, 0, 0.001, r, g, b);
                        w.spawnParticle(Particle.DUST_COLOR_TRANSITION, tornadoLoc.getX() + x, tornadoLoc.getY() - 0.5 + tornadoTicks / ((double) time / height), tornadoLoc.getZ() + z, 1, 0.1, 0.1, 0.1, 1, new DustTransition(Color.fromRGB(255 * (tornadoTicks) / time, 255, 255), Color.fromRGB(255, 255, 255), 1.0F));
                    }
                }
                angle += 5;
                radius += 0.1;
                if (tornadoTicks % 5 == 0) {
                    for (Entity ps : tornadoLoc.getWorld().getNearbyEntities(tornadoLoc, 20, 20, 20)) {
                        if (ps instanceof Player){
                            if (ps.getLocation().distanceSquared(tornadoLoc) <= 6) {
                                ps.setVelocity(ps.getVelocity().setY(1.5));
                                if (!Main.disableFall.contains((Player)ps)) {
                                    Main.disableFall.add((Player)ps);
                                }
                            }
                        }
                    }
                }
                if (tornadoTicks >= time) {
                    Bukkit.getScheduler().cancelTask(Main.tornado.get(id));
                }
                tornadoTicks += 1;
            }
        }, 0, 1));
    }

}
