package me.bobthe28th.capturethefart.ctf.items.Wizard;

import java.util.Objects;
import java.util.Random;

import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;

public class WizStickFire extends CTFDoubleCooldownItem {

    Main plugin;
    CTFPlayer player;

    public WizStickFire(CTFPlayer player_, Main plugin_) {
        super("Fire Staff", Material.STICK, 3, "Solar Blast", 2, "Ballz", 69, player_, plugin_);
        plugin = plugin_;
        player = player_;
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
//		Block block = event.getClickedBlock();

        switch (action) {
            case LEFT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
                if (getCooldown(0) == 0) {

                    Location pOrgin = player.getEyeLocation();
                    Vector pDir = pOrgin.getDirection();
                    pDir.multiply(10);
                    pDir.normalize();
                    pDir.multiply(0.1);
                    Random rand = new Random();
                    double maxRandomDistance = 0.05;
                    for (int i = 0; i < 20; i++) {
                        Vector pDirR = pDir.clone();

                        pDirR.add(new Vector((rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance,(rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance,(rand.nextFloat()*maxRandomDistance*2)-maxRandomDistance));

                        Objects.requireNonNull(pOrgin.getWorld()).spawnParticle(Particle.FLAME, pOrgin, 0, pDirR.getX(), pDirR.getY(), pDirR.getZ(), 5.0);
                    }

                    startCooldown(0);
                }
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                Fireball f = player.launchProjectile(Fireball.class);
                f.setYield(0);
                f.setIsIncendiary(false);
                break;
            default:
                break;
        }
    }

}
