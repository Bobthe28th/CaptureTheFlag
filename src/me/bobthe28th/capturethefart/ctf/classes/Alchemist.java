package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamage;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamageCause;
import me.bobthe28th.capturethefart.ctf.items.alchemist.AlcPotion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class Alchemist extends CTFClass implements Listener {

    String name = "Alchemist";
    HashMap<String, AlcPotion> potions = new HashMap<>();

    public Alchemist(CTFPlayer player_, Main plugin_) {
        super("Alchemist",plugin_,player_);
        if (player_ != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        setArmor(new Material[]{Material.LEATHER,Material.LEATHER_LEGGINGS,Material.LEATHER_BOOTS});
        setHelmetCustomModel(7);
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

        //TODO spoon
        potions.put("Heal/Damage Potion", new AlcPotion(player,plugin,0,"Heal/Damage Potion",Material.SPLASH_POTION,3,PotionEffectType.HARM.getColor(),new PotionEffect[]{new PotionEffect(PotionEffectType.HARM,1,0,false,true,true),new PotionEffect(PotionEffectType.HEAL,1,0,false,true,true)}));
        potions.put("Buff/Debuff Potion", new AlcPotion(player,plugin,1,"Buff/Debuff Potion",Material.SPLASH_POTION,7,PotionEffectType.POISON.getColor(),new PotionEffect[]{new PotionEffect(PotionEffectType.SLOW,40,0,false,true,true),new PotionEffect(PotionEffectType.POISON,40,1,false,true,true),new PotionEffect(PotionEffectType.WEAKNESS,140,0,false,true,true),new PotionEffect(PotionEffectType.INCREASE_DAMAGE,160,0,false,true,true),new PotionEffect(PotionEffectType.ABSORPTION,160,0,false,true,true)}));
        potions.put("Movement Potion", new AlcPotion(player,plugin,2,"Movement Potion",Material.LINGERING_POTION,15,PotionEffectType.JUMP.getColor(),new PotionEffect[]{new PotionEffect(PotionEffectType.JUMP,20,6,false,true,true),new PotionEffect(PotionEffectType.SPEED,140,1,false,true,true),new PotionEffect(PotionEffectType.SLOW,20,2,false,true,true),new PotionEffect(PotionEffectType.LUCK,20,0,true,false,false)}));

        potions.forEach((k,v) -> player.giveItem(v));
    }

    public enum NegativeEffects{
        CONFUSION, HARM, HUNGER,POISON, SLOW_DIGGING, SLOW, WEAKNESS, WITHER
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getPotion().getShooter() != player.getPlayer()) return;

        if (event.getPotion().hasMetadata("potionName")) {
            String potionName = event.getPotion().getMetadata("potionName").get(0).asString();
            if (potions.containsKey(potionName)) {

                event.setCancelled(true);

                for (LivingEntity e : event.getAffectedEntities()) {
                    if (e instanceof Player p) {
                        if (Main.CTFPlayers.containsKey(p)) {
                            for (PotionEffect potionEffect : event.getPotion().getEffects()) {
                                boolean isBad = false;
                                boolean doesDamage = false;
                                for(NegativeEffects bad: NegativeEffects.values()){
                                    if (potionEffect.getType().getName().equalsIgnoreCase(bad.name())) {
                                        isBad = true;
                                        if (bad.name().equals("HARM")) {
                                            doesDamage = true;
                                        }
                                    }
                                }
                                if (isBad) {
                                    if (Main.CTFPlayers.get(p).getTeam() != player.getTeam()) {
                                        if (doesDamage) {
                                            Main.customDamageCause.put(p,new CTFDamage(player,CTFDamageCause.ALCHEMIST_DAMAGE_POT));
                                        }
                                        p.addPotionEffect(potionEffect);
                                    }
                                } else {
                                    if (Main.CTFPlayers.get(p).getTeam() == player.getTeam()) {
                                        p.addPotionEffect(potionEffect);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
        if (event.getEntity().hasMetadata("playerSent") && event.getEntity().getMetadata("playerSent").get(0).asString().equals(player.getPlayer().getName())) {
            if (event.getEntity().hasMetadata("potionName")) {
                String potionName = event.getEntity().getMetadata("potionName").get(0).asString();
                if (potions.containsKey(potionName)) {

                    event.setCancelled(true);

                    for (LivingEntity e : event.getAffectedEntities()) {
                        if (e instanceof Player p) {
                            if (Main.CTFPlayers.containsKey(p)) {
                                for (PotionEffect potionEffect : event.getEntity().getCustomEffects()) {
                                    boolean isBad = false;
                                    boolean doesDamage = false;
                                    for(NegativeEffects bad: NegativeEffects.values()){
                                        if (potionEffect.getType().getName().equalsIgnoreCase(bad.name())) {
                                            isBad = true;
                                            if (bad.name().equals("HARM")) {
                                                doesDamage = true;
                                            }
                                        }
                                    }
                                    if (isBad) {
                                        if (Main.CTFPlayers.get(p).getTeam() != player.getTeam()) {
                                            if (doesDamage) {
                                                Main.customDamageCause.put(p,new CTFDamage(player,CTFDamageCause.ALCHEMIST_DAMAGE_POT));
                                            }
                                            p.addPotionEffect(potionEffect);
                                        }
                                    } else {
                                        if (Main.CTFPlayers.get(p).getTeam() == player.getTeam() && !Main.CTFPlayers.get(p).isCarringFlag()) {
                                            p.addPotionEffect(potionEffect);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @EventHandler
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        if (event.getEntity().getShooter() != player.getPlayer()) return;

        if (event.getEntity().hasMetadata("potionName")) {
            AreaEffectCloud effectCloud = event.getAreaEffectCloud();
            effectCloud.setMetadata("playerSent", new FixedMetadataValue(plugin,player.getPlayer().getName()));
            effectCloud.setMetadata("potionName", new FixedMetadataValue(plugin,event.getEntity().getMetadata("potionName").get(0).asString()));
//            effectCloud.setMetadata("ctfTeam", new FixedMetadataValue(plugin,player.getTeam().getId()));
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() != player.getPlayer()) return;
        if (event.getEntity() instanceof ThrownPotion thrownPotion) {
            thrownPotion.setVelocity(thrownPotion.getVelocity().subtract(player.getPlayer().getVelocity()));
            thrownPotion.setVelocity(thrownPotion.getVelocity().multiply(1.5));
            ItemMeta meta = thrownPotion.getItem().getItemMeta();
            if (meta != null && potions.containsKey(meta.getPersistentDataContainer().get(new NamespacedKey(plugin,"ctfname"),  PersistentDataType.STRING))) {
                event.setCancelled(true);
                potions.get(meta.getPersistentDataContainer().get(new NamespacedKey(plugin,"ctfname"),  PersistentDataType.STRING)).onPotionLaunch();
            }
        }
    }

}
