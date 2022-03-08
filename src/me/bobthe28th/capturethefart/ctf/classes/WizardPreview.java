package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFTeam;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public class WizardPreview extends CTFClass implements Listener {

    String name = "Wizard";

    public WizardPreview(CTFTeam team, Main plugin_) {
        super("Wizard", plugin_, null);
        setArmor(new Material[]{Material.LEATHER,null,null});
        setHelmetCustomModel(1);
    }

    @Override
    public void deselect() {}

    @Override
    public String getFormattedName() {
        return ChatColor.LIGHT_PURPLE + name;
    }

    @Override
    public void giveItems() {}

}
