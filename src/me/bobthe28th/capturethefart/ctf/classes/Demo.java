package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.demo.DemArrow;
import me.bobthe28th.capturethefart.ctf.items.demo.DemBow;
import me.bobthe28th.capturethefart.ctf.items.demo.DemTNT;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Demo extends CTFClass implements Listener {

    String name = "Demolitionist";
    CTFBuildUpItem arrow;

    public Demo(CTFPlayer player_, Main plugin_) {
        super("Demolitionist",plugin_,player_);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        setArmor(new Material[]{Material.IRON_INGOT,Material.LEATHER_LEGGINGS,Material.IRON_BOOTS});
        setHelmetCustomModel(2);
        setEnchantments(new Enchantment[][]{null, null, new Enchantment[]{Enchantment.PROTECTION_FALL}}, new Integer[][]{null, null, new Integer[]{4}});
    }

    @Override
    public void deselect() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getFormattedName() {
        return ChatColor.RED + name + ChatColor.RESET;
    }

    @Override
    public void giveItems() {
        player.removeItems();
        player.giveItem(new DemTNT(player,plugin,3));
        arrow = new DemArrow(player,plugin,5);
        player.giveItem(arrow);
        player.giveItem(new DemBow(arrow,player,plugin,4));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;
        if (event.getBlock().getType() == Material.TNT) {
            event.getBlock().setType(Material.AIR);
            TNTPrimed tnt = player.getPlayer().getWorld().spawn(event.getBlock().getLocation().add(new Vector(0.5,0.0,0.5)), TNTPrimed.class);
            tnt.setMetadata("playerSent", new FixedMetadataValue(plugin, Objects.requireNonNull(player.getPlayer()).getName()));
            tnt.setFuseTicks(10);
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player p) {
            if (p != player.getPlayer()) return;
        }
        if (event.getProjectile() instanceof Arrow) {
            if (!arrow.isOnCooldown()) {
                arrow.startCooldown();
            }
            ((Arrow) event.getProjectile()).setCritical(false);
            event.getProjectile().setMetadata("bombArrow", new FixedMetadataValue(plugin, true));
            event.getProjectile().setMetadata("playerSent", new FixedMetadataValue(plugin, Objects.requireNonNull(player.getPlayer()).getName()));

            new BukkitRunnable() {
                final Arrow a = (Arrow) event.getProjectile();
                public void run() {
                    if (a.isDead()) {
                        this.cancel();
                    }
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,a.getLocation(),1,0.0,0.0,0.0,0.0);
                    }
                }
            }.runTaskTimer(plugin,0,1);

        }
    }

}
