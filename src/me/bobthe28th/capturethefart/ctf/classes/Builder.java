package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.builder.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class Builder extends CTFClass implements Listener {

    String name = "Builder";
    BuiWool wool;

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
        player.giveItem(new BuiRod(player,plugin,3));
        player.giveItem(new BuiShears(player,plugin,4));
        wool = new BuiWool(player,plugin,1);
        player.giveItem(wool);
        player.giveItem(new BuiPlan(player,plugin,2,wool));
//        player.giveItem(new BuiFish(player,plugin,5));
    }

    @Override
    public void breakBlock(Block b) {
        Material type = b.getType();
        if (type == wool.getMat()) {
            wool.add(1);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;
        event.setExpToDrop(0);
        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && event.getCaught() != null) {
            if (event.getCaught() instanceof Player p && Main.CTFPlayers.containsKey(p) && Main.CTFPlayers.get(p).getTeam() != player.getTeam()) {
                event.getCaught().setVelocity(player.getPlayer().getLocation().toVector().subtract(event.getCaught().getLocation().toVector()).multiply(0.2));
                event.getHook().remove();
                event.setCancelled(true);
            }
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() != null && event.getCaught() instanceof Item item) {
            player.setFishCaught(player.getFishCaught() + 1);
            player.getPlayer().sendMessage(ChatColor.YELLOW + "Caught FISH " + ChatColor.GOLD + "+" + ChatColor.RESET);
            if (player.getFishCaught() >= 15) {
                player.giveItem(new BuiFish(player,plugin,5));
            }
            item.remove();
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() != player.getPlayer()) return;

        if (event.getEntity() instanceof FishHook) {
            event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(2));
        }
    }

}