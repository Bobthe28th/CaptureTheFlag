package me.bobthe28th.capturethefart.ctf.items.alchemist;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFStackCooldownItem;
import org.bukkit.Material;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class AlcJump extends CTFStackCooldownItem {

    PotionEffectType effect = PotionEffectType.JUMP;
    int duration = 100;
    int amplifier = 0;

    public AlcJump(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Jump Boost Potion", Material.SPLASH_POTION,0,"Jump Boost Potion",5,Material.GLASS_BOTTLE,player_,plugin_,defaultSlot_);
        setPotionEffect(new PotionData(PotionType.JUMP));
    }

    @Override
    public void onPotionLaunch() {
        startCooldown();
        ItemStack item = new ItemStack(Material.SPLASH_POTION);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta instanceof PotionMeta pMeta) {
                pMeta.setColor(effect.getColor());
                pMeta.addCustomEffect(new PotionEffect(effect,duration,amplifier),true);
            }
        }
        item.setItemMeta(meta);
        ThrownPotion potion = player.getPlayer().launchProjectile(ThrownPotion.class);
        potion.setItem(item);
    }
}
