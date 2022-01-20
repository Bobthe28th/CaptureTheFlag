package me.bobthe28th.capturethefart.ctf.items.builder;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.Material;

public class BuiAxe extends CTFItem {

    public BuiAxe(CTFPlayer player_, Main plugin_) {
        super("Builder's Axe",Material.IRON_AXE,1,player_,plugin_);
    }
}