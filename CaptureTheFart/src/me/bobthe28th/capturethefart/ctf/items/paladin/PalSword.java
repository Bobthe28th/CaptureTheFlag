package me.bobthe28th.capturethefart.ctf.items.paladin;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFStackCooldownItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PalSword extends CTFStackCooldownItem {

    public PalSword(CTFPlayer player_, Main plugin_) {
        super("Paladins Sword", Material.IRON_SWORD, 0, "Fortify", 13.0, Material.WOODEN_SWORD, player_, plugin_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (getCooldown() == 0) {
                Player p = event.getPlayer();
                p.setVelocity(p.getVelocity().add(new Vector(0.0, 1.0, 0.0)));
                startCooldown();
            }
        }

    }
}
