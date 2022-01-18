package me.bobthe28th.capturethefart.ctf.items.Paladin;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;

public class PalIron extends CTFBuildUpItem {

    public PalIron(CTFPlayer player_, Main plugin_) {
        super("Paladin Iron", Material.IRON_BLOCK, 3, 16, 0, player_, plugin_);
    }
}
