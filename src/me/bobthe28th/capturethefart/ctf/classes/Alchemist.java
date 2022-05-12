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
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Objects;

public class Alchemist extends CTFClass implements Listener {

    String name = "Alchemist";
    HashMap<String, AlcPotion> potions = new HashMap<>();

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
        //TODO set ambient false
        potions.put("Damage Potion", new AlcPotion(player,plugin,0,"Damage Potion",Material.SPLASH_POTION,3,PotionEffectType.HARM.getColor(),new PotionEffect[]{new PotionEffect(PotionEffectType.HARM,20,0)},false,true));
        potions.put("Debuff Potion", new AlcPotion(player,plugin,1,"Debuff Potion",Material.SPLASH_POTION,5,PotionEffectType.POISON.getColor(),new PotionEffect[]{new PotionEffect(PotionEffectType.SLOW,40,0),new PotionEffect(PotionEffectType.POISON,40,1),new PotionEffect(PotionEffectType.WEAKNESS,140,0)},false,true));
        potions.put("Heal Potion", new AlcPotion(player,plugin,2,"Heal Potion",Material.SPLASH_POTION,5,PotionEffectType.HEAL.getColor(),new PotionEffect[]{new PotionEffect(PotionEffectType.HEAL,20,0)},true,false));
        potions.put("Attack Potion", new AlcPotion(player,plugin,3,"Attack Potion",Material.SPLASH_POTION,5,PotionEffectType.INCREASE_DAMAGE.getColor(),new PotionEffect[]{new PotionEffect(PotionEffectType.INCREASE_DAMAGE,160,0),new PotionEffect(PotionEffectType.ABSORPTION,160,0)},true,false));
        potions.put("Movement Potion", new AlcPotion(player,plugin,4,"Movement Potion",Material.LINGERING_POTION,5,PotionEffectType.JUMP.getColor(),new PotionEffect[]{new PotionEffect(PotionEffectType.JUMP,20,6),new PotionEffect(PotionEffectType.SPEED,140,1),new PotionEffect(PotionEffectType.LUCK,20,0,true,false,false)},true,false));

        potions.forEach((k,v) -> player.giveItem(v));
    }

    public enum NegativeEffects {
        CONFUSION, HARM, HUNGER, POISON, SLOW_DIGGING, SLOW, WEAKNESS, WITHER
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getPotion().getShooter() != player.getPlayer()) return;

        if (event.getPotion().hasMetadata("potionName")) {
            String potionName = event.getPotion().getMetadata("potionName").get(0).asString();
            if (potions.containsKey(potionName)) {

                for (LivingEntity e : event.getAffectedEntities()) {
                    if (e instanceof Player p) {
                        if (Main.CTFPlayers.containsKey(p)) {
                            if (Main.CTFPlayers.get(p).getTeam() == player.getTeam()) {
                                if (!potions.get(potionName).applyToTeammates()) {
                                    event.setIntensity(p,0.0);
                                }
                            } else {
                                if (!potions.get(potionName).applyToEnemies()) {
                                    event.setIntensity(p,0.0);
                                }
                            }
                        }
                    }
                }


            }
        }
    }

//    @EventHandler
//    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
//        if (event.getEntity().getShooter() != player.getPlayer()) return;
//        if (event.getEntity().hasMetadata("potionName")) {
//            String potionName = event.getEntity().getMetadata("potionName").get(0).asString();
//            if (potionName.equals("Movement Potion")) {
//                event.getAreaEffectCloud().setMetadata("movement",new FixedMetadataValue(plugin,true)); //TODO does nothing rn
//            }
//        }
//    }

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
