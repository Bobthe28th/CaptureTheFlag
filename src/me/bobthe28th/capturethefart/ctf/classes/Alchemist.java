package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.alchemist.*;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class Alchemist extends CTFClass implements Listener {

    String name = "Alchemist";
    HashMap<PotionEffectType, CTFItem> potions = new HashMap<>();

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
        potions.put(PotionEffectType.SPEED,new AlcSpeed(player,plugin,0));
        potions.put(PotionEffectType.WEAKNESS,new AlcWeak(player,plugin,1));
        potions.put(PotionEffectType.SLOW,new AlcSlow(player,plugin,2));
        potions.put(PotionEffectType.INCREASE_DAMAGE,new AlcStrength(player,plugin,3));
        potions.put(PotionEffectType.HARM,new AlcDamage(player,plugin,4));
        potions.put(PotionEffectType.JUMP,new AlcJump(player,plugin,5));
        potions.put(PotionEffectType.POISON,new AlcPoison(player,plugin,6)); // linger
        potions.put(PotionEffectType.REGENERATION,new AlcRegen(player,plugin,7)); //linger
        potions.put(PotionEffectType.SLOW_FALLING,new AlcSlowFall(player,plugin,8));
        potions.forEach((k,v) -> player.giveItem(v));
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() != player.getPlayer()) return;
        if (event.getEntity() instanceof ThrownPotion thrownPotion) {
            for (PotionEffect pEffect : thrownPotion.getEffects()) {
                if (potions.containsKey(pEffect.getType())) {
                    potions.get(pEffect.getType()).onPotionLaunch(event,thrownPotion);
                }
            }
        }
    }

}
