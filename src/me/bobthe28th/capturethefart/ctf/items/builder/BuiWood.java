package me.bobthe28th.capturethefart.ctf.items.builder;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuiWood extends CTFBuildUpItem {

    public BuiWood(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Wood", Material.OAK_PLANKS, 7, 16, 0, player_, plugin_, defaultSlot_);
        Material tMat = Material.OAK_PLANKS;
        if (player_.getTeam().getChatColor() == ChatColor.RED) {
            tMat = Material.CRIMSON_PLANKS;
        } else {
            if (player_.getTeam().getChatColor() == ChatColor.BLUE) {
                tMat = Material.WARPED_PLANKS;
            }
        }
        setItem(tMat);
    }

    @Override
    public void onblockPlace(BlockPlaceEvent event) {
        Main.breakableBlocks.add(event.getBlock());
        if (!isOnCooldown()) {
            startCooldown();
        }
    }
}
