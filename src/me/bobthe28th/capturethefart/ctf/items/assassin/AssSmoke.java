package me.bobthe28th.capturethefart.ctf.items.assassin;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFStackCooldownItem;
import org.bukkit.Material;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class AssSmoke extends CTFStackCooldownItem {

    public AssSmoke(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Smoke Bomb", Material.GREEN_DYE, 1, "Smoke Bomb", 20, Material.GRAY_DYE, player_, plugin_, defaultSlot_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        if (getCooldown() <= 0 && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            startCooldown();
            Snowball bomb = player.getPlayer().launchProjectile(Snowball.class);
            bomb.setVelocity(bomb.getVelocity().multiply(0.5));
            bomb.setMetadata("ctfProjectile",new FixedMetadataValue(plugin,true));
            bomb.setMetadata("ctfProjectileType",new FixedMetadataValue(plugin,"smokebomb"));
            ItemStack item = new ItemStack(Material.GREEN_DYE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(1);
            }
            item.setItemMeta(meta);
            bomb.setItem(item);
        }
    }

}
