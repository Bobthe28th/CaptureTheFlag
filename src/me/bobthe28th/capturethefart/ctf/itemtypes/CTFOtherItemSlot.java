package me.bobthe28th.capturethefart.ctf.itemtypes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;

public class CTFOtherItemSlot extends CTFItem {

    Integer otherCooldownSlot;

    public CTFOtherItemSlot(String itemName_, Material item_, Integer otherCooldownSlot_, Integer customModel_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super(itemName_, item_, customModel_, player_, plugin_,defaultSlot_);
        otherCooldownSlot=otherCooldownSlot_;
    }

    @Override
    public void displayCooldowns() {
        if (player.getItem(otherCooldownSlot) != null) {
            player.getItem(otherCooldownSlot).displayCooldowns();
        } else {
            player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
        }
    }
}
