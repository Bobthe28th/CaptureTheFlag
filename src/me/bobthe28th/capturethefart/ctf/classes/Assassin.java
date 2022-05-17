package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamage;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamageCause;
import me.bobthe28th.capturethefart.ctf.items.assassin.AssKnife;
import me.bobthe28th.capturethefart.ctf.items.assassin.AssPotion;
import me.bobthe28th.capturethefart.ctf.items.assassin.AssSmoke;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class Assassin extends CTFClass implements Listener {

    String name = "Assassin";
    AssKnife knife;
    AssPotion potion;

    public Assassin(CTFPlayer player_, Main plugin_) {
        super("Assassin",plugin_,player_);
        if (player_ != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        setArmor(new Material[]{Material.LEATHER,null,null});
        setHelmetCustomModel(5);
    }

    @Override
    public void giveItems() {
        player.removeItems();
        knife = new AssKnife(player,plugin,0);
        player.giveItem(knife);
        potion = new AssPotion(knife,player,plugin,2);
        player.giveItem(potion);
        player.giveItem(new AssSmoke(player,plugin,1));
    }

    @Override
    public void deselect() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getFormattedName() {
        return ChatColor.DARK_PURPLE + name + ChatColor.RESET;
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player pf) {
            if (pf != player.getPlayer()) return;
            if (potion != null) {
                potion.onPotion(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player pf) {
            if (event instanceof EntityDamageByEntityEvent eEvent) {
                if (eEvent.getDamager() instanceof Player pA) {
                    if (pA == player.getPlayer() && player.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        Main.customDamageCause.put(pf,new CTFDamage(player, CTFDamageCause.ASSASSIN_KNIFE_STRONG));
                        event.setDamage(event.getDamage() * 2.5);
                        player.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                        player.getPlayer().removePotionEffect(PotionEffectType.SPEED);
                    }
                }
            }
            if (pf == player.getPlayer() && player.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                player.getPlayer().removePotionEffect(PotionEffectType.SPEED);
            }
        }
    }
}
