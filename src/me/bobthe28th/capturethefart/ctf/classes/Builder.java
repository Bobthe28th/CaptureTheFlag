package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.builder.BuiAxe;
import me.bobthe28th.capturethefart.ctf.items.builder.BuiShears;
import me.bobthe28th.capturethefart.ctf.items.builder.BuiWood;
import me.bobthe28th.capturethefart.ctf.items.builder.BuiWool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Builder extends CTFClass implements Listener {

    String name = "Builder";
    BuiWool wool;
    BuiWood wood;

    public Builder(CTFPlayer player_, Main plugin_) {
        super("Builder",plugin_,player_);
        if (player_ != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        setArmor(new Material[]{Material.IRON_INGOT,Material.LEATHER_LEGGINGS,Material.LEATHER_BOOTS});
        setHelmetCustomModel(1);
    }

    @Override
    public void deselect() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getFormattedName() {
        return ChatColor.GOLD + name + ChatColor.RESET;
    }

    @Override
    public void giveItems() {
        player.removeItems();
        player.giveItem(new BuiAxe(player,plugin,0));
        player.giveItem(new BuiShears(player,plugin,1));
        wood = new BuiWood(player,plugin,2);
        wool = new BuiWool(player,plugin,3);
        player.giveItem(wood);
        player.giveItem(wool);
    }

    @Override
    public void breakBlock(Block b) {
        Material type = b.getType();
        if (type == wood.getMat()) {
            wood.add(1);
        } else {
            if (type == wool.getMat()) {
                wool.add(1);
            }
        }
    }
}