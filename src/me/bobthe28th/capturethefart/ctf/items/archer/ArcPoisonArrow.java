package me.bobthe28th.capturethefart.ctf.items.archer;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFBuildUpItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ArcPoisonArrow extends CTFBuildUpItem {

    ArcBow bow;

    public ArcPoisonArrow(ArcBow bow_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super("Poison Arrow", Material.TIPPED_ARROW, 3, 12, 0, player_, plugin_, defaultSlot_);
        setPotionEffect(new PotionData(PotionType.POISON));
        bow = bow_;
    }

    @Override
    public void onHold(PlayerItemHeldEvent event) {
        setSlot(40);
        player.getPlayer().getInventory().setHeldItemSlot(player.getItemSlot(bow));
    }
}
