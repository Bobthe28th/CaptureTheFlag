package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.archer.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Archer extends CTFClass implements Listener {

    String name = "Archer";
    ArcBow bow;
    ArcArrow arrow;
    ArcGhostArrow ghostArrow;
    ArcPoisonArrow poisonArrow;
    ArcSonicArrow sonicArrow;

    public Archer(CTFPlayer player_, Main plugin_) {
        super("Archer",plugin_,player_);
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
        return ChatColor.DARK_GREEN + name + ChatColor.RESET;
    }

    @Override
    public void givePassives() {
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP,Integer.MAX_VALUE,1,true,false,true));
    }

    @Override
    public void giveItems() {
        player.removeItems();

        bow = new ArcBow(player,plugin,4);
        arrow = new ArcArrow(bow,player,plugin,0);
        ghostArrow = new ArcGhostArrow(bow,player,plugin,1);
        poisonArrow = new ArcPoisonArrow(bow,player,plugin,2);
        sonicArrow = new ArcSonicArrow(bow,player,plugin,3);
        player.giveItem(bow);
        player.giveItem(arrow);
        player.giveItem(ghostArrow);
        player.giveItem(poisonArrow);
        player.giveItem(sonicArrow);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player p) {
            if (p != player.getPlayer()) return;

            event.setConsumeItem(false);

            Arrow a = (Arrow) event.getProjectile();

            if (event.getConsumable() != null) {

                if (p.getInventory().getItem(EquipmentSlot.OFF_HAND) != null && Objects.equals(p.getInventory().getItem(EquipmentSlot.OFF_HAND), event.getConsumable()) && event.getConsumable().getItemMeta() != null) {
                    String name = event.getConsumable().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin,"ctfname"),  PersistentDataType.STRING);
                        if (name != null) {
                            switch (name) {
                                case "Arrow" -> arrow.shoot(a);
                                case "Ghost Arrow" -> ghostArrow.shoot(a,event.getForce());
                                case "Poison Arrow" -> poisonArrow.shoot(a);
                                case "Sonic Arrow" -> sonicArrow.shoot(a);
                            }
                        }
                } else {
                    arrow.shoot(a);
                }

                event.getConsumable().setAmount(event.getConsumable().getAmount() - 1);
                arrow.setSlot(40);
            }



//            event.setConsumeItem(false);
//            if (event.getConsumable() != null) {
//                if (p.getInventory().getItem(EquipmentSlot.OFF_HAND) != null && Objects.requireNonNull(p.getInventory().getItem(EquipmentSlot.OFF_HAND)).getType() == event.getConsumable().getType()) {
//                    if (event.getProjectile() instanceof Arrow && event.getConsumable().getItemMeta() != null && event.getConsumable().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"ctfname"),  PersistentDataType.STRING)) {
//                        String name = event.getConsumable().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin,"ctfname"),  PersistentDataType.STRING);
//                        if (name != null) {
//                            Arrow a = (Arrow) event.getProjectile();
//                            switch (name) {
//                                case "Arrow" -> arrow.shoot(a);
//                                case "Ghost Arrow" -> ghostArrow.shoot(a,event.getForce());
//                                case "Poison Arrow" -> poisonArrow.shoot(a);
//                                case "Sonic Arrow" -> sonicArrow.shoot(a);
//                            }
//                        }
//                    }
//                    if (event.getConsumable() != null) {
//                        event.getConsumable().setAmount(event.getConsumable().getAmount() - 1);
//                    }
//                } else {
//                    event.setCancelled(true);
//                    p.updateInventory();
//                }
//            }
        }
    }
}
