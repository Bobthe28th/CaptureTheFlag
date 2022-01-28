package me.bobthe28th.capturethefart.ctf.items.builder;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.Material;

public class BuiShears extends CTFItem {

    public BuiShears(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Builder's Shears",Material.SHEARS,1,player_,plugin_, defaultSlot_);
    }
}