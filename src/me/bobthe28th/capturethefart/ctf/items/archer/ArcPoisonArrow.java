package me.bobthe28th.capturethefart.ctf.items.archer;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFStackCooldownItem;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ArcPoisonArrow extends CTFStackCooldownItem {

    ArcBow bow;

    public ArcPoisonArrow(ArcBow bow_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Poison Arrow", Material.TIPPED_ARROW, 0, "Poison Arrow", 5, Material.STICK, player_, plugin_, defaultSlot_);
        setPotionColor(PotionEffectType.POISON.getColor());
        addPotionEffect(new PotionEffect(PotionEffectType.POISON,300,1));
        bow = bow_;
    }

    public void shoot(Arrow arrow) {
        if (getCooldown() == 0) {
            startCooldown();
        }
        arrow.setMetadata("ctfProjectile",new FixedMetadataValue(plugin,true));
        arrow.setMetadata("ctfProjectileType",new FixedMetadataValue(plugin,"archerarrow"));
        arrow.setMetadata("ArcherArrowType",new FixedMetadataValue(plugin,"poison"));
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            setSlot(40);
            player.getPlayer().getInventory().setHeldItemSlot(player.getItemSlot(bow));
        }
    }
}
