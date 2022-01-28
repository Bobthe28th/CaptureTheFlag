package me.bobthe28th.capturethefart.ctf.items.wizard;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class WizBookFire extends CTFDoubleCooldownItem {



    public WizBookFire(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Fire Tome", Material.BOOK, 3, "Fire Wall", 13, "FUCK YOU", 69, player_, plugin_, defaultSlot_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();

        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (getCooldown(0) == 0) {
                    startAction(0);
                    new BukkitRunnable() {
                        int t = 0;
                        final int s = 2;
                        final Location origin = p.getLocation();
                        final Vector direction = p.getLocation().getDirection().setY(0).normalize();
                        int lastBlockY = origin.getBlockY();

                        public void run() {
                            t++;
                            if (t > 60) {
                                startCooldown(0);
                                this.cancel();
                            }

                            if (t % s == 0) {

                                BlockIterator blocksToAdd = new BlockIterator(origin.clone().add(direction.clone().multiply((t+s) / s)).setDirection(new Vector(0.0, -1.0, 0.0)).add(new Vector(0.0, lastBlockY + 3.0, 0.0)),0,100);

                                Location blockToAdd;
                                while(blocksToAdd.hasNext()) {
                                    blockToAdd = blocksToAdd.next().getLocation();
                                    Location fire = blockToAdd.clone().add(new Vector(0.0,1.0,0.0));
                                    if (blockToAdd.getBlock().getType().isSolid() && fire.getBlock().isEmpty()) {
                                        if (fire.getBlockY() < lastBlockY + 3 && fire.getBlockY() > lastBlockY - 3) {
                                            lastBlockY = fire.getBlockY();
                                            fire.getBlock().setType(Material.FIRE);
                                        } else {
                                            startCooldown(0);
                                            this.cancel();
                                        }
                                        return;
                                    }
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
