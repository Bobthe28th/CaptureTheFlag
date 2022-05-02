package me.bobthe28th.capturethefart.ctf.items.paladin;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFToolCooldownItem;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class PalHammer extends CTFToolCooldownItem {

    public PalHammer (CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Paladin's Hammer", Material.IRON_AXE,2, "Throw",3,player_,plugin_, defaultSlot_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (getCooldown() <= 0 && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            startCooldown();
            Snowball hammer = player.getPlayer().launchProjectile(Snowball.class);
            hammer.setShooter(player.getPlayer());
            hammer.setVelocity(player.getPlayer().getLocation().getDirection().multiply(0.9));
            hammer.setMetadata("hammer", new FixedMetadataValue(plugin, true));
            hammer.setMetadata("playerSent", new FixedMetadataValue(plugin, player.getPlayer().getName()));
            ItemStack item = new ItemStack(Material.IRON_AXE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(2);
            }
            item.setItemMeta(meta);
            hammer.setItem(item);

            ArmorStand hammerStand = player.getPlayer().getWorld().spawn(player.getPlayer().getLocation(), ArmorStand.class);
            hammerStand.setInvisible(true);
            hammerStand.setGravity(false);
            hammerStand.setMarker(true);
            hammerStand.setSmall(true);
            hammer.addPassenger(hammerStand);
            if (hammerStand.getEquipment() != null) {
                ItemStack h = new ItemStack(Material.IRON_AXE);
                ItemMeta hm = item.getItemMeta();
                if (hm != null) {
                    hm.setCustomModelData(3);
                }
                h.setItemMeta(hm);
                hammerStand.getEquipment().setHelmet(h);
            }
        }
    }

}
