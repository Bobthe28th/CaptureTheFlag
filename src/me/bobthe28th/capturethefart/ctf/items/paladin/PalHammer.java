package me.bobthe28th.capturethefart.ctf.items.paladin;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFToolCooldownItem;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class PalHammer extends CTFToolCooldownItem {

    public PalHammer (CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Paladin's Hammer", Material.IRON_AXE,2, "Earth Shatter (not stolen from overwatch)",3,player_,plugin_, defaultSlot_);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (getCooldown() <= 0 && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            startCooldown();
            Snowball hammer = player.getPlayer().launchProjectile(Snowball.class);
            hammer.setShooter(player.getPlayer());
            hammer.setVelocity(player.getPlayer().getLocation().getDirection().multiply(0.9));
            hammer.setMetadata("hammer", new FixedMetadataValue(plugin, true));
            ItemStack item = new ItemStack(Material.IRON_AXE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(2);
                item.setItemMeta(meta);
            }
            hammer.setItem(item);

            ArmorStand hammerStand = player.getPlayer().getWorld().spawn(player.getPlayer().getLocation(), ArmorStand.class);
            hammerStand.setInvisible(true);
            hammerStand.setGravity(false);
            hammerStand.setMarker(true);
            hammerStand.setSmall(true);
            hammer.addPassenger(hammerStand);
            if (hammerStand.getEquipment() != null) {
                hammerStand.getEquipment().setHelmet(getItem());
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!hammer.isDead()) {
                        hammerStand.setHeadPose(hammerStand.getHeadPose().add(0.5, 0, 0));
                    } else {
                        for (Entity e : hammer.getWorld().getNearbyEntities(hammer.getLocation(), 3, 3, 3)) {
                            if (e instanceof Player p && hammer.getLocation().distance(p.getLocation()) <= 3 && Main.CTFPlayers.containsKey(p)) {
                                if (Main.CTFPlayers.get(p).getTeam() != player.getTeam()) {
                                    Main.customDamageCause.put(p,new Object[]{"hammerThrow",player.getPlayer()});
                                    p.damage(2,player.getPlayer());
                                    p.setVelocity(new Vector(0,p.getVelocity().getY() + 0.05,0));
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,20,2,true,true,true));
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20,0,true,true,true));
                                }
                            }
                        }
                        hammerStand.remove();
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin,0L,1L);
        }
    }

}
