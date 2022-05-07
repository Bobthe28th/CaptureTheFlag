package me.bobthe28th.capturethefart.ctf.items.alchemist;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFStackCooldownItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class AlcPotion extends CTFStackCooldownItem {

    Material type;
    Color potionColor;
    PotionEffect[] potionEffects;

    public AlcPotion(CTFPlayer player_, Main plugin_, Integer defaultSlot_, String name_, Material type_, int cooldown_, Color potionColor_, PotionEffect[] potionEffects_) {
        super(name_, type_,0,name_,cooldown_,Material.GLASS_BOTTLE,player_,plugin_,defaultSlot_);
        type = type_;
        potionColor = potionColor_;
        potionEffects = potionEffects_.clone();

        setPotionColor(potionColor);
        for (PotionEffect pE : potionEffects) {
            addPotionEffect(pE);
        }
    }

    @Override
    public void onPotionLaunch() {
        startCooldown();
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta instanceof PotionMeta pMeta) {
                pMeta.setColor(potionColor);
                for (PotionEffect pE : potionEffects) {
                    pMeta.addCustomEffect(pE,true);
                }
            }
        }
        item.setItemMeta(meta);
        ThrownPotion potion = player.getPlayer().launchProjectile(ThrownPotion.class);
        potion.setItem(item);
    }

}