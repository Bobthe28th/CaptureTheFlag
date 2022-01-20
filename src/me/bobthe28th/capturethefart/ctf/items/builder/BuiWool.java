package me.bobthe28th.capturethefart.ctf.items.builder;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class BuiWool extends CTFBuildUpItem {

    public BuiWool(CTFPlayer player_, Main plugin_) {
        super("Wool", Material.WHITE_WOOL, 3, 32, 0, player_, plugin_);
        Material tMat = Material.WHITE_WOOL;
        if (player_.getTeam().getColor() == ChatColor.RED) {
            tMat = Material.RED_WOOL;
        } else {
            if (player_.getTeam().getColor() == ChatColor.BLUE) {
                tMat = Material.BLUE_WOOL;
            }
        }
        setItem(tMat);
    }
}
