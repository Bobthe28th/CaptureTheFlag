package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.paladin.PalHammer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

//TODO add GENERIC_KNOCKBACK_RESISTANCE

public class Paladin extends CTFClass implements Listener {

    String name = "Paladin";

    public Paladin(CTFPlayer player_, Main plugin_) {
        super("Paladin",plugin_,player_);
        if (player_ != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        setArmor(new Material[]{Material.IRON_HELMET,Material.IRON_LEGGINGS,Material.IRON_BOOTS});
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
        player.giveItem(new PalHammer(player,plugin,0));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;
        if (player.getpClass() != this) return;
        if (((Entity) player.getPlayer()).isOnGround()) {
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,0,true,false,true));
        } else if (player.getPlayer().hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        }
    }
}
