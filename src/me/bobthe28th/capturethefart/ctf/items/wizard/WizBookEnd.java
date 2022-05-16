package me.bobthe28th.capturethefart.ctf.items.wizard;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class WizBookEnd extends CTFDoubleCooldownItem {

    public WizBookEnd(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("End Tome", Material.BOOK, 4, "Fire Wall", 10, "Attack Boost", 10, player_, plugin_, defaultSlot_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();

        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (getCooldown(0) == 0) {
                    startCooldown(0);
                    int innerRadius = 7;
                    int outerRadius = 7;
                    Random rand = new Random();
                    for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(), innerRadius, innerRadius, innerRadius)) {
                        if (e instanceof Player pe && Main.CTFPlayers.containsKey(pe)) {
                            CTFPlayer cp = Main.CTFPlayers.get(pe);
                            if (cp.getTeam() != player.getTeam()) {
                                boolean teleported = false;
                                int iteration = 0;
                                while (!teleported && iteration <= 100) {
                                    int x = rand.nextInt((outerRadius + innerRadius) * 2) - (outerRadius + innerRadius);
                                    int z = rand.nextInt((outerRadius + innerRadius) * 2) - (outerRadius + innerRadius);
                                    if ((x > innerRadius || x < -innerRadius) && (z > innerRadius || z < -innerRadius)) {
                                        for (int y = 0; y < outerRadius * 2; y++) {
                                            Location loc = new Location(p.getWorld(), x + p.getLocation().getBlockX(), y - outerRadius + p.getLocation().getBlockY(), z + p.getLocation().getBlockZ(), pe.getLocation().getYaw(), pe.getLocation().getPitch());
                                            if (Main.gameController.getMap().getBoundingBox().contains(loc.toVector()) && loc.getBlock().getType().isSolid() && loc.clone().add(new Vector(0.0, 1.0, 0.0)).getBlock().getType().isAir() && loc.clone().add(new Vector(0.0, 2.0, 0.0)).getBlock().getType().isAir()) {
                                                pe.getWorld().spawnParticle(Particle.PORTAL, pe.getLocation().add(new Vector(0.0, 1.0, 0.0)), 50, 0.1, 0.5, 0.1);
                                                pe.teleport(loc.clone().add(new Vector(0.5, 1.0, 0.5)));
                                                pe.getWorld().playSound(pe, Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
                                                teleported = true;
                                                break;
                                            }
                                        }
                                    }
                                    iteration++;
                                }
                            }
                        }
                    }
                }
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                startCooldown(1);
                double radius = 5.0;
                //TODO particles
                for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(), radius, radius, radius)) {
                    if (e instanceof Player pe && Main.CTFPlayers.containsKey(pe) && Main.CTFPlayers.get(pe).getTeam() == player.getTeam()) {
                        if (p.getLocation().distance(pe.getLocation()) <= radius) {
                            pe.setVelocity(pe.getVelocity().setY(0.75)); //TODO only glide for a few seconds
                            pe.setGliding(true);
                        }
                    }
                }

                break;
        }

    }
}
