package me.bobthe28th.capturethefart.ctf.items.archer;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class testarrow extends CTFBuildUpItem {

    ArcBow bow;

    public testarrow(ArcBow bow_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("testarrow", Material.ARROW, 3, 12, 1, player_, plugin_, defaultSlot_);
        bow = bow_;
    }

    public void shoot(Arrow arrow, CTFPlayer player) {

        if (!isOnCooldown()) {
            startCooldown();
        }

        arrow.setMetadata("testarrow", new FixedMetadataValue(plugin, true));
        arrow.setMetadata("playerSent", new FixedMetadataValue(plugin, Objects.requireNonNull(player.getPlayer()).getName()));
        arrow.setShooter(player.getPlayer());
    }

    @Override
    public void onHold(PlayerItemHeldEvent event) {
        setSlot(40);
        player.getPlayer().getInventory().setHeldItemSlot(player.getItemSlot(bow));
    }

}
