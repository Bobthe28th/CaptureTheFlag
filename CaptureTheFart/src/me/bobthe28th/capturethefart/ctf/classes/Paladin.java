package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.paladin.PalIron;
import me.bobthe28th.capturethefart.ctf.items.paladin.PalSword;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class Paladin extends CTFClass implements Listener {
    Main plugin;
    CTFPlayer player;
    String name = "Paladin";
    public Paladin(CTFPlayer player_, Main plugin_) {
        super("Paladin",plugin_);
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
        return ChatColor.GRAY + name + ChatColor.RESET;
    }

    @Override
    public void giveItems() {
        player.removeItems();
        player.giveItem(new PalSword(player,plugin),3);
        player.giveItem(new PalIron(player,plugin),5);
    }
}
