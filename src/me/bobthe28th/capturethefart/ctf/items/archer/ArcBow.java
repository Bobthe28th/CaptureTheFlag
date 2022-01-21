package me.bobthe28th.capturethefart.ctf.items.archer;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.Material;

public class ArcBow extends CTFItem {
    public ArcBow(CTFPlayer player_, Main plugin_) {
        super("Bow", Material.BOW, 0, player_, plugin_);
    }
}
