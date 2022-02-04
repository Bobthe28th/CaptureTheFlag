package me.bobthe28th.capturethefart.ctf.itemtypes;

import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import me.bobthe28th.capturethefart.Main;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

public abstract class CTFItem {

    String itemName;
    Material item;
    PotionData pData;
    int customModel;
    public Main plugin;
    public CTFPlayer player;
    public int amount = 1;
    public int defaultSlot;
    public int slot;

    public CTFItem(String itemName_, Material item_, Integer customModel_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        itemName = itemName_;
        item = item_;
        customModel = customModel_;
        plugin = plugin_;
        player = player_;
        defaultSlot = defaultSlot_;
        slot = defaultSlot;
    }

    public void onclickAction(PlayerInteractEvent event) {}

    public void onblockPlace(BlockPlaceEvent event) {}

    public void onHold(PlayerItemHeldEvent event) {}

    public void displayCooldowns() {
        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
    }

    public void setPotionEffect(PotionData pD) {
        pData = pD;
    }

    public void setItem(Material nItem) {
        item = nItem;
    }

    public ItemStack getItem() {
        ItemStack it = new ItemStack(item,amount);
        ItemMeta meta = it.getItemMeta();

        if (meta != null) {

            if (meta instanceof PotionMeta pMeta) {
                pMeta.setBasePotionData(pData);
            }

            meta.setDisplayName(ChatColor.RESET + itemName);
            meta.setCustomModelData(customModel);
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "ctfname"), PersistentDataType.STRING, itemName);
            it.setItemMeta(meta);
            return it;
        } else {
            return null;
        }
    }

    public void setSlot(int newSlot) {

        if (player.getItem(newSlot) != null) {
            player.getItem(newSlot).setSlot(player.getItem(newSlot).getDefaultSlot());
        }

        ItemStack oldItem = player.getPlayer().getInventory().getItem(slot);

        if (oldItem != null) {
            player.getPlayer().getInventory().setItem(newSlot, oldItem);
            oldItem.setAmount(0);
        }
        player.getPlayer().updateInventory();
        slot = newSlot;
    }

    public int getDefaultSlot() {
        return defaultSlot;
    }

    public int getSlot() {
        return slot;
    }
}
