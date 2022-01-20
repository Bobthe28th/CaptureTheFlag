package me.bobthe28th.capturethefart.ctf.itemtypes;

import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.bobthe28th.capturethefart.Main;

public abstract class CTFItem {

    String itemName;
    Material item;
    int customModel;
    public Main plugin;
    public CTFPlayer player;
    public int amount = 1;

    public CTFItem(String itemName_, Material item_, Integer customModel_, CTFPlayer player_, Main plugin_) {
        itemName = itemName_;
        item = item_;
        customModel = customModel_;
        plugin = plugin_;
        player = player_;
    }

    public void onclickAction(PlayerInteractEvent event) {}

    public void onblockPlace(BlockPlaceEvent event) {}

    public void displayCooldowns() {
        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
    }

    public void setItem(Material nItem) {
        item = nItem;
    }

    public ItemStack getItem() {
        ItemStack it = new ItemStack(item,amount);
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + itemName);
            meta.setCustomModelData(customModel);
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE, (byte) 1);
            it.setItemMeta(meta);
            return it;
        } else {
            return null;
        }
    }
}
