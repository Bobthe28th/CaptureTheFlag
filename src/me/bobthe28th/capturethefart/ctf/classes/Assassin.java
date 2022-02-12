package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.assassin.AssKnife;
import me.bobthe28th.capturethefart.ctf.items.assassin.AssPotion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

public class Assassin extends CTFClass implements Listener {

    String name = "Assassin";
    AssKnife knife;
    AssPotion potion;

    public Assassin(CTFPlayer player_, Main plugin_) {
        super("Assassin",plugin_,player_);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        setArmor(new Material[]{Material.LEATHER,null,null});
        setHelmetCustomModel(5);
    }

    @Override
    public void giveItems() {
        player.removeItems();
        knife = new AssKnife(player,plugin,0);
        player.giveItem(knife);
        potion = new AssPotion(knife,player,plugin,1);
        player.giveItem(potion);
    }

    @Override
    public void onPotion(EntityPotionEffectEvent event) {
        potion.onPotion(event);
    }

    @Override
    public void attackPlayer(EntityDamageByEntityEvent event) {
        if (player.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            event.setDamage(event.getFinalDamage() * 2);
            player.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    @Override
    public void deselect() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getFormattedName() {
        return ChatColor.DARK_PURPLE + name + ChatColor.RESET;
    }

}
