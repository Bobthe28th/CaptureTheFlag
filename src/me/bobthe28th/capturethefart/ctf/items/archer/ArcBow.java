package me.bobthe28th.capturethefart.ctf.items.archer;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFOtherItemSlot;
import org.bukkit.Material;

public class ArcBow extends CTFOtherItemSlot {
    public ArcBow(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Bow", Material.BOW, 40, 0, player_, plugin_, defaultSlot_);
    }
}
