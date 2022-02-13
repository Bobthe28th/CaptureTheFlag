package me.bobthe28th.capturethefart.ctf.items.alchemist;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFStackCooldownItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class AlcSlowFall extends CTFStackCooldownItem {

    PotionEffectType effect = PotionEffectType.SLOW_FALLING;
    int duration = 100;
    int amplifier = 0;

    public AlcSlowFall(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Slow Fall Potion", Material.POTION,0,"Slow Fall Potion",5,Material.GLASS_BOTTLE,player_,plugin_,defaultSlot_);
        setPotionEffect(new PotionData(PotionType.SLOW_FALLING));
    }

    @Override
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            startCooldown();
            event.setCancelled(true);
            player.getPlayer().addPotionEffect(new PotionEffect(effect,duration,amplifier,true,true,true));
        }
    }
}