package me.bobthe28th.capturethefart.ctf.items.demo;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFOtherItem;
import org.bukkit.Material;

public class DemBow extends CTFOtherItem {
    public DemBow(CTFItem otherItem_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Bazooka", Material.CROSSBOW, otherItem_, 1, player_, plugin_, defaultSlot_);
    }
}
