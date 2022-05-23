package me.bobthe28th.capturethefart.ctf.items.builder;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BuiFish extends CTFDoubleCooldownItem {

    public BuiFish(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super(ChatColor.GOLD + "F I S H" + ChatColor.RESET, Material.TROPICAL_FISH, 1, "Vine Boom Sound Effect", 2,false, "Spawn 100 Ghasts", 60,false, player_, plugin_, defaultSlot_);
        startCooldown(1);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();

        switch (action) {
            case LEFT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
                if (getCooldown(0) == 0) {
                    p.getWorld().playSound(p.getLocation(), "minecraft:boom", 1, 1);
                    for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(),5,5,5)) {
                        if (e instanceof Player pNear && Main.CTFPlayers.containsKey(pNear) && Main.CTFPlayers.get(pNear).getTeam() != player.getTeam()) {
                            pNear.setVelocity(pNear.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(2));
                        }
                    }
                    startCooldown(0);
                }
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                if (getCooldown(1) == 0) {
                    startAction(1);
                    p.getWorld().playSound(p.getLocation(), "minecraft:ghast", 10000, 1);
                    for (int i = 0; i < 100; i++) {
                        p.getWorld().spawn(p.getLocation(), Ghast.class);
                    }
                }
                break;
        }

    }

}
