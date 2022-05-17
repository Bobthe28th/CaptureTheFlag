package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamage;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamageCause;
import me.bobthe28th.capturethefart.ctf.items.wizard.WizBookEnd;
import me.bobthe28th.capturethefart.ctf.items.wizard.WizStickEnd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WizardEnd extends CTFClass implements Listener {

    String name = "End Wizard";

    public WizardEnd(CTFPlayer player_, Main plugin_) {
        super("End Wizard",plugin_,player_);
        if (player_ != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        setArmor(new Material[]{Material.LEATHER,null,null});
        setHelmetCustomModel(4);
    }

    @Override
    public void giveItems() {
        player.removeItems();
        player.giveItem(new WizStickEnd(player,plugin,0));
        player.giveItem(new WizBookEnd(player,plugin,1));
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
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        if (event.getTo() != null) {

            if (!player.isCarringFlag()) {
                double radius = 2.0;
                for (Entity e : player.getPlayer().getWorld().getNearbyEntities(event.getTo(), radius, radius, radius)) {
                    if (e instanceof Player pe && Main.CTFPlayers.containsKey(pe) && Main.CTFPlayers.get(pe).getTeam() != player.getTeam()) {
                        Main.customDamageCause.put(pe, new CTFDamage(player, CTFDamageCause.WIZARD_PEARL));
                        pe.damage(6.0, player.getPlayer());
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
