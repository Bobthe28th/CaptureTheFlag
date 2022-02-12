package me.bobthe28th.capturethefart.ctf.items.paladin;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.Material;

public class PalHammer extends CTFItem {

    public PalHammer (CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Paladin's Hammer", Material.IRON_AXE,2,player_,plugin_, defaultSlot_);
    }

}
