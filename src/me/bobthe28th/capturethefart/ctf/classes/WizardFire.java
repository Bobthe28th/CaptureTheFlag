package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.wizard.WizBookFire;
import me.bobthe28th.capturethefart.ctf.items.wizard.WizStickFire;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WizardFire extends CTFClass implements Listener {

    String name = "Fire Wizard";

    public WizardFire(CTFPlayer player_, Main plugin_) {
        super("Fire Wizard",plugin_,player_);
        if (player_ != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        setArmor(new Material[]{Material.LEATHER,null,null});
        setHelmetCustomModel(1);
    }

    @Override
    public void giveItems() {
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,Integer.MAX_VALUE,0,true,false,true));
        player.removeItems();
        player.giveItem(new WizStickFire(player,plugin,0));
        player.giveItem(new WizBookFire(player,plugin,1));
    }

    @Override
    public void deselect() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getFormattedName() {
        return ChatColor.RED + name + ChatColor.RESET;
    }

}
