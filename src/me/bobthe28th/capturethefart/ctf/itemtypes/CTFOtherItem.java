package me.bobthe28th.capturethefart.ctf.itemtypes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import org.bukkit.Material;

public class CTFOtherItem extends CTFItem {

    CTFItem otherCooldown;

    public CTFOtherItem(String itemName_, Material item_, CTFItem otherCooldown_, Integer customModel_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super(itemName_, item_, customModel_, player_, plugin_,defaultSlot_);
        otherCooldown=otherCooldown_;
    }

    @Override
    public void displayCooldowns() {
        otherCooldown.displayCooldowns();
    }

}
