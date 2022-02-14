package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.items.wizard.*;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WizardWind extends CTFClass implements Listener {

    Integer slowFallEffect;
    String name = "Wind Wizard";

    public WizardWind(CTFPlayer player_, Main plugin_) {
        super("Wind Wizard",plugin_,player_);
        if (player_ != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        setArmor(new Material[]{Material.LEATHER,null,null});
        setHelmetCustomModel(3);
    }

    @Override
    public void giveItems() {
        player.removeItems();
        player.giveItem(new WizBookWind(player,plugin,1));
        player.giveItem(new WizStickWind(player,plugin,0));
    }

    @Override
    public void deselect() {
        if (slowFallEffect != null) {
            Bukkit.getScheduler().cancelTask(slowFallEffect);
        }
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getFormattedName() {
        return ChatColor.YELLOW + name + ChatColor.RESET;
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;

        Player pE = event.getPlayer();

        if(!pE.isSneaking()) {
            if (pE.getVelocity().getY() <= 0) {
                pE.setVelocity(pE.getVelocity().setY(0.0));
            }
            slowFallEffect = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                final Player p = pE.getPlayer();
                int ticks = 0;
                float angle = 0;
                final int time = 2;
                final int particleAmount = 4;
                final int inparticleAmount = 4;
                final double radius = 0.5;
                final double inradius = 0.2;
                public void run() {
                    if (p != null) {
                        if (!((Entity) p).isOnGround()) {
                            p.addPotionEffect((new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 0, true, false, true)));
                            Location l = p.getLocation();
                            for (int i = 0; i < particleAmount; i++) {
                                double x = radius * Math.sin((angle + i * (360.0 / particleAmount)) * (Math.PI / 180));
                                double z = radius * Math.cos((angle + i * (360.0 / particleAmount)) * (Math.PI / 180));
                                for (Player pp : Bukkit.getOnlinePlayers()) {
                                    if (pp != p) {
                                        pp.spawnParticle(Particle.DUST_COLOR_TRANSITION, l.getX() + x, l.getY(), l.getZ() + z, 1, 0.05, 0.05, 0.05, 1, new Particle.DustTransition(Color.fromRGB(0, 255, 255), Color.fromRGB(255, 255, 255), 1F));
                                    }
                                }
                            }
                            for (int i = 0; i < inparticleAmount; i++) {
                                double x = inradius * Math.sin((angle + i * (360.0 / inparticleAmount)) * (Math.PI / 180));
                                double z = inradius * Math.cos((angle + i * (360.0 / inparticleAmount)) * (Math.PI / 180));
                                for (Player pp : Bukkit.getOnlinePlayers()) {
                                    if (pp != p) {
                                        pp.spawnParticle(Particle.DUST_COLOR_TRANSITION, l.getX() + x, l.getY(), l.getZ() + z, 1, 0.05, 0.05, 0.05, 1, new Particle.DustTransition(Color.fromRGB(0, 255, 255), Color.fromRGB(255, 255, 255), 0.5F));
                                    }
                                }

                            }
                            angle += (360.0 / particleAmount) / time;
                        }
                    }
                    ticks += 1;
                }
            }, 0, 1);
        } else {
            if (slowFallEffect != null) {
                Bukkit.getScheduler().cancelTask(slowFallEffect);
                slowFallEffect = null;
                PotionEffect effect = pE.getPotionEffect(PotionEffectType.SLOW_FALLING);
                if (effect != null && effect.getDuration() >= 500) {
                    pE.removePotionEffect(PotionEffectType.SLOW_FALLING);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;

        if (((Entity) player.getPlayer()).isOnGround()) {
            if (slowFallEffect != null) {
                player.getPlayer().getPotionEffect(PotionEffectType.SLOW_FALLING);
                PotionEffect effect = player.getPlayer().getPotionEffect(PotionEffectType.SLOW_FALLING);
                if (effect != null && effect.getDuration() >= 500) {
                    player.getPlayer().removePotionEffect(PotionEffectType.SLOW_FALLING);
                }
            }
        }
    }
}
