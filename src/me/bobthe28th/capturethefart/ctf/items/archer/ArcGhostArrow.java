package me.bobthe28th.capturethefart.ctf.items.archer;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.Material;

public class ArcGhostArrow extends CTFBuildUpItem {

    public ArcGhostArrow(CTFPlayer player_, Main plugin_) {
        super("Ghost Arrow", Material.ARROW, 3, 12, 1, player_, plugin_);
    }

}
