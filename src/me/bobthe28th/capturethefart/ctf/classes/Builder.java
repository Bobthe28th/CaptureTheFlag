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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class Builder extends CTFClass implements Listener {
    Main plugin;
    CTFPlayer player;
    String name = "Builder";

    public Builder(CTFPlayer player_, Main plugin_) {
        super("Builder",plugin_);
        player = player_;
        plugin = plugin_;
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
        player.giveItem(new BuiAxe(player,plugin),0);
        player.giveItem(new BuiShears(player,plugin),1);
        player.giveItem(new BuiWood(player,plugin),2);
        player.giveItem(new BuiWool(player,plugin),3);
    }
}