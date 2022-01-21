package me.bobthe28th.capturethefart.ctf.items.archer;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class ArcGhostArrow extends CTFBuildUpItem {

    public ArcGhostArrow(CTFPlayer player_, Main plugin_) {
        super("Ghost Arrow", Material.ARROW, 3, 12, 1, player_, plugin_);
    }

    public void shoot(Arrow arrow, CTFPlayer player) {
        arrow.setMetadata("ghostArrow", new FixedMetadataValue(plugin, true));
        arrow.setMetadata("playerSent", new FixedMetadataValue(plugin, Objects.requireNonNull(player.getPlayer()).getName()));
        arrow.setMetadata("velocity", new FixedMetadataValue(plugin, arrow.getVelocity()));
        arrow.setShooter(player.getPlayer());

        arrow.setGravity(false);
        arrow.setPierceLevel(100);
        arrow.setCritical(false);


//        arrow.setVelocity(arrow.getVelocity().normalize().multiply(2));
    }
}
