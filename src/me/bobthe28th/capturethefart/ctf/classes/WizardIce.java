package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.wizard.WizBookIce;
import me.bobthe28th.capturethefart.ctf.items.wizard.WizBookWind;
import me.bobthe28th.capturethefart.ctf.items.wizard.WizStickIce;
import me.bobthe28th.capturethefart.ctf.items.wizard.WizStickWind;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class WizardIce extends CTFClass implements Listener {

    String name = "Ice Wizard";

    public WizardIce(CTFPlayer player_, Main plugin_) {
        super("Ice Wizard",plugin_,player_);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        setArmor(new Material[]{Material.LEATHER,null,null});
        setHelmetCustomModel(2);
    }

    @Override
    public void giveItems() {
        player.removeItems();
        player.giveItem(new WizBookIce(player,plugin,1));
        player.giveItem(new WizStickIce(player,plugin,0));
    }

    @Override
    public void deselect() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getFormattedName() {
        return ChatColor.AQUA + name + ChatColor.RESET;
    }

}
