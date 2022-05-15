package me.bobthe28th.capturethefart.ctf.items.assassin;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.damage.CTFDamageCause;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class AssKnife extends CTFItem {

    boolean attackState = false;

    public AssKnife(CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Knife", Material.STONE_SWORD, 1, player_, plugin_, defaultSlot_);
        setMeleeDeathMessage(CTFDamageCause.ASSASSIN_KNIFE);
    }

    public void setAttackState(boolean attackState_) {
        attackState = attackState_;
        if (attackState) {
            setCustomModel(2);
        } else {
            setCustomModel(1);
        }
        player.giveItem(this);
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            player.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

}
