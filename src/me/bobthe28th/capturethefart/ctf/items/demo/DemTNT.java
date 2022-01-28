package me.bobthe28th.capturethefart.ctf.items.demo;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;

public class DemTNT extends CTFBuildUpItem {

    public DemTNT(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("TNT", Material.TNT, 10, 3, 0, player_, plugin_, defaultSlot_);
    }

    @Override
    public void onblockPlace(BlockPlaceEvent event) {
        if (!isOnCooldown()) {
            startCooldown();
        }
    }
}
