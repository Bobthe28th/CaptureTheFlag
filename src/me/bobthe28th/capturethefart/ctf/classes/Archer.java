package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.archer.ArcBow;
import me.bobthe28th.capturethefart.ctf.items.archer.ArcGhostArrow;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class Archer extends CTFClass implements Listener {

    String name = "Archer";

    public Archer(CTFPlayer player_, Main plugin_) {
        super("Archer",plugin_,player_);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void deselect() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getFormattedName() {
        return ChatColor.GREEN + name + ChatColor.RESET;
    }

    @Override
    public void giveItems() {
        player.removeItems();
        player.giveItem(new ArcBow(player,plugin),0);
        player.giveItem(new ArcGhostArrow(player,plugin),1);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player p) {
            if (p != player.getPlayer()) return;
        }
        if (event.getConsumable() != null && event.getConsumable().getItemMeta() != null && event.getConsumable().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"ctfname"),  PersistentDataType.BYTE)) {
            //TODO
        }
    }

}
