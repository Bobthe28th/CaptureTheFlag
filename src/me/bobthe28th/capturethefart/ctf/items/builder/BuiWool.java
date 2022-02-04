package me.bobthe28th.capturethefart.ctf.items.builder;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuiWool extends CTFBuildUpItem {

    public BuiWool(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Wool", Material.WHITE_WOOL, 3, 32, 0, player_, plugin_, defaultSlot_);
        Material tMat = Material.WHITE_WOOL;
        if (player_.getTeam().getChatColor() == ChatColor.RED) {
            tMat = Material.RED_WOOL;
        } else {
            if (player_.getTeam().getChatColor() == ChatColor.BLUE) {
                tMat = Material.BLUE_WOOL;
            }
        }
        setItem(tMat);
    }

    @Override
    public void onblockPlace(BlockPlaceEvent event) {
        Main.breakableBlocks.put(event.getBlock(),player.getTeam());
        if (!isOnCooldown()) {
            startCooldown();
        }
    }
}
