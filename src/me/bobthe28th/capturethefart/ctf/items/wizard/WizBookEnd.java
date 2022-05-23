package me.bobthe28th.capturethefart.ctf.items.wizard;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.CTFTeam;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class WizBookEnd extends CTFDoubleCooldownItem {

    public WizBookEnd(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("End Tome", Material.BOOK, 4, "Ender Pearl", 20,true, "Scatter", 10,false, player_, plugin_, defaultSlot_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();

        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (getCooldown(0) == 0 && !player.isCarringFlag()) {
                    startCooldown(0);
                    p.launchProjectile(EnderPearl.class); //TODO particles
                }
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                if (getCooldown(1) == 0) {
                    startCooldown(1);
                    int innerRadius = 7;
                    int outerRadius = 7;
                    Random rand = new Random();
                    for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(), innerRadius, innerRadius, innerRadius)) {
                        if (e instanceof Player pe && Main.CTFPlayers.containsKey(pe) && pe.getGameMode() != GameMode.SPECTATOR) {
                            CTFPlayer cp = Main.CTFPlayers.get(pe);
                            if (cp.getTeam() != player.getTeam()) {
                                boolean teleported = false;
                                int iteration = 0;

                                while (!teleported && iteration <= 100) {
                                    int x = rand.nextInt((outerRadius + innerRadius) * 2) - (outerRadius + innerRadius);
                                    int z = rand.nextInt((outerRadius + innerRadius) * 2) - (outerRadius + innerRadius);
                                    if ((x > innerRadius || x < -innerRadius) && (z > innerRadius || z < -innerRadius)) {
                                        for (int y = 0; y < outerRadius * 2; y++) {
                                            if (!teleported) {
                                                Location loc = new Location(p.getWorld(), x + p.getLocation().getBlockX(), y - outerRadius + p.getLocation().getBlockY(), z + p.getLocation().getBlockZ(), pe.getLocation().getYaw(), pe.getLocation().getPitch());
                                                boolean inSpawn = false;
                                                for (CTFTeam sBoxTeam : Main.gameController.getMap().getSpawnMoveBoxes().keySet()) {
                                                    if (sBoxTeam != cp.getTeam() && Main.gameController.getMap().getSpawnMoveBoxes().get(sBoxTeam).contains(loc.toVector())) {
                                                        inSpawn = true;
                                                    }
                                                }
                                                if (!inSpawn && Main.gameController.getMap().getBoundingBox().contains(loc.toVector()) && loc.getBlock().getType().isSolid() && loc.clone().add(new Vector(0.0, 1.0, 0.0)).getBlock().getType().isAir() && loc.clone().add(new Vector(0.0, 2.0, 0.0)).getBlock().getType().isAir()) {
                                                    pe.getWorld().spawnParticle(Particle.PORTAL, pe.getLocation().add(new Vector(0.0, 1.0, 0.0)), 50, 0.1, 0.5, 0.1);
                                                    pe.teleport(loc.clone().add(new Vector(0.5, 1.0, 0.5)), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                                    pe.getWorld().playSound(pe, Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
                                                    teleported = true;
                                                }
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
        }

    }
}
