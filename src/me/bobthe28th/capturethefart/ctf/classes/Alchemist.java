package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.alchemist.*;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class Alchemist extends CTFClass implements Listener {

    String name = "Alchemist";
    HashMap<String, CTFItem> potions = new HashMap<>();

    public Alchemist(CTFPlayer player_, Main plugin_) {
        super("Alchemist",plugin_,player_);
        if (player_ != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        setArmor(new Material[]{Material.LEATHER_HELMET,Material.LEATHER_LEGGINGS,Material.LEATHER_BOOTS});
    }

    @Override
    public void deselect() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getFormattedName() {
        return ChatColor.LIGHT_PURPLE + name + ChatColor.RESET;
    }

    @Override
    public void giveItems() {
        player.removeItems();
        potions.put("Speed Potion", new AlcPotion(player,plugin,0,"Speed Potion",Material.SPLASH_POTION,5,PotionEffectType.SPEED.getColor(),new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED,200,0),new PotionEffect(PotionEffectType.JUMP,200,1)}));
        potions.forEach((k,v) -> player.giveItem(v));
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() != player.getPlayer()) return;
        if (event.getEntity() instanceof ThrownPotion thrownPotion) {
            ItemMeta meta = thrownPotion.getItem().getItemMeta();
            if (meta != null && potions.containsKey(meta.getPersistentDataContainer().get(new NamespacedKey(plugin,"ctfname"),  PersistentDataType.STRING))) {
                event.setCancelled(true);
                potions.get(meta.getPersistentDataContainer().get(new NamespacedKey(plugin,"ctfname"),  PersistentDataType.STRING)).onPotionLaunch();
            }
        }
    }

}
