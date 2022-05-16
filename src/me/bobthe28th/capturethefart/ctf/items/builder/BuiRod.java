package me.bobthe28th.capturethefart.ctf.items.builder;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.Material;

public class BuiRod extends CTFItem {

    public BuiRod(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Builder's Fising Rod", Material.FISHING_ROD,1,player_,plugin_, defaultSlot_);
    }

}
