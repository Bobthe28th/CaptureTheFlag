package me.bobthe28th.capturethefart.ctf.items.demo;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.Material;

public class DemBow extends CTFItem {
    public DemBow(CTFPlayer player_, Main plugin_) {
        super("Bazooka", Material.CROSSBOW, 0, player_, plugin_);
    }
}
