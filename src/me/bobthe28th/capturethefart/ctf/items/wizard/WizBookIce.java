package me.bobthe28th.capturethefart.ctf.items.wizard;

import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;

public class WizBookIce extends CTFDoubleCooldownItem {

    public WizBookIce(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Ice Tome",Material.BOOK, 2,"Ice Wall", 10,"Ice Skate", 20, player_,plugin_, defaultSlot_);
        plugin = plugin_;
        player = player_;
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();
        Block block = event.getClickedBlock();

        switch (action) {
            case LEFT_CLICK_BLOCK:
                if (getCooldown(0) == 0 && block != null) {
                    float yaw = p.getEyeLocation().getYaw() + 180;
                    int dir = Math.round(yaw / 90) == 4 ? 0 : Math.round(yaw / 90);

                    boolean xDir = (dir % 2) == 0;

                    int wallWidth = 3;
                    int wallHeight = 3;
                    int wallHeightBelow = 2;

                    for (int x = wallWidth * -1; x <= wallWidth; x++) {
                        for (int y = wallHeightBelow * -1 + 1; y <= wallHeight; y++) {
                            Location l = block.getLocation().clone().add(new Vector((xDir) ? x : 0, y, (xDir) ? 0 : x));

                            if (l.getBlock().isEmpty()) {
                                Main.createFadingBlock(l, Material.FROSTED_ICE, Material.AIR, 3, (int)(Math.random() * 22 + 18), plugin);
                            }
                        }

                    }
                    startCooldown(0);
                }
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                if (getCooldown(1) == 0) {
                    startAction(1);
                    Vector pvS = p.getVelocity().clone().setY(0.0);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60,1,true,false,true));
                    new BukkitRunnable() {
                        int t = 0;
                        final double y = p.getLocation().getY() - 1.0;
                        public void run() {
                            t++;
                            if (t > 60) {
                                startCooldown(1);
                                this.cancel();
                            }

                            if (p.getGameMode() == GameMode.SPECTATOR) {
                                this.cancel();
                            }

                            if (!isCancelled()) {
                                if (t % 10 == 1) {
                                    if (t == 1) {
                                        Location tLoc = p.getLocation().clone();
                                        tLoc.setY(y + 1.0);
                                        p.teleport(tLoc);
                                        p.setVelocity(pvS);
                                    }
                                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (float) 1000.0, (float) t / 60);
                                }

                                Location l = p.getLocation();
                                l.setY(y);
                                if (l.getBlock().getType() == Material.AIR) {
                                    Main.createFadingBlock(l, Material.FROSTED_ICE, Material.AIR, 3, (int) (Math.random() * 22 + 18), plugin);
                                }
                            }
                        }
                    }.runTaskTimer(plugin,0,1);

                }
                break;
            default:
                break;
        }
    }

}
