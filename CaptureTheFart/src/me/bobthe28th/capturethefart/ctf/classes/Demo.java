package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.Demo.DemArrow;
import me.bobthe28th.capturethefart.ctf.items.Demo.DemBow;
import me.bobthe28th.capturethefart.ctf.items.Demo.DemTNT;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Demo extends CTFClass implements Listener {

    Main plugin;
    CTFPlayer player;
    String name = "Demolitionist";
    CTFBuildUpItem arrow;

    public Demo(CTFPlayer player_, Main plugin_) {
        super("Demolitionist",plugin_);
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
        return ChatColor.RED + name + ChatColor.RESET;
    }

    @Override
    public void giveItems() {
        player.removeItems();
        player.giveItem(new DemTNT(player,plugin),3);
        player.giveItem(new DemBow(player,plugin),4);
        arrow = new DemArrow(player,plugin);
        player.giveItem(arrow,5);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;
        if (event.getBlock().getType() == Material.TNT) {
            event.getBlock().setType(Material.AIR);
            TNTPrimed tnt = player.getPlayer().getWorld().spawn(event.getBlock().getLocation().add(new Vector(0.5,0.0,0.5)), TNTPrimed.class);
            tnt.setMetadata("playerSent", new FixedMetadataValue(plugin, Objects.requireNonNull(player.getPlayer()).getName()));
            tnt.setFuseTicks(10);
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player p) {
            if (p != player.getPlayer()) return;
        }
        if (!arrow.isOnCooldown()) {
            arrow.startCooldown();
        }
        event.getProjectile().setMetadata("bombArrow", new FixedMetadataValue(plugin, true));
        event.getProjectile().setMetadata("playerSent", new FixedMetadataValue(plugin, Objects.requireNonNull(player.getPlayer()).getName()));
    }

}
