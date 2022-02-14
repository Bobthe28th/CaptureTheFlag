package me.bobthe28th.capturethefart.ctf.items.assassin;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFStackCooldownItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class AssPotion extends CTFStackCooldownItem {

    AssKnife knife;

    public AssPotion(AssKnife knife_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Invisibility Potion", Material.POTION,0,"Invisibility Potion",20,Material.GLASS_BOTTLE,player_,plugin_,defaultSlot_);
        setPotionEffect(new PotionData(PotionType.INVISIBILITY));
        knife = knife_;
    }

    @Override
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            startAction();
            player.removeArmor();
            event.setCancelled(true);
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,1200,0,true,true,true));
            knife.setAttackState(true);
        }
    }

    public void onPotion(EntityPotionEffectEvent event) {
        if ((event.getCause() == EntityPotionEffectEvent.Cause.EXPIRATION || event.getAction() == EntityPotionEffectEvent.Action.REMOVED) && event.getModifiedType().equals(PotionEffectType.INVISIBILITY)) {
            player.giveArmor();
            startCooldown();
            knife.setAttackState(false);
        }
    }
}
