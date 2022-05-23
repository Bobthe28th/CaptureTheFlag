package me.bobthe28th.capturethefart.ctf.itemtypes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class CTFToolCooldownItem extends CTFItem {

    double cooldown = 0;
    double cooldownMax;
    String cooldownName;
    Material actionItem;

    public CTFToolCooldownItem(String itemName_, Material item_, Integer customModel_, String cooldownName_, double cooldownMax_, Material actionItem_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super(itemName_,item_,customModel_,player_,plugin_,defaultSlot_);
        cooldownName = cooldownName_;
        cooldownMax = cooldownMax_;
        actionItem = actionItem_;
    }

    public void startAction() {
        cooldown = -1;
        ItemStack cItem = new ItemStack(actionItem);
        cItem.setAmount(1);
        if (cItem.hasItemMeta()) {
            ItemMeta cMeta = cItem.getItemMeta();
            if (cMeta != null) {
                cMeta.setDisplayName(ChatColor.RESET + itemName);
                cMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE, (byte) 1);
                cItem.setItemMeta(cMeta);
            }
        }
        if (player.getItemSlot(this) != -1) {
            player.getPlayer().getInventory().setItem(player.getItemSlot(this),cItem);
        }

        displayCooldowns();
    }

    public void startCooldown() {
        cooldown = cooldownMax;
        player.getPlayer().getInventory().setItem(player.getItemSlot(this),getItem());
        ItemStack itemM = player.getItemStack(this);
        Damageable meta = (Damageable) itemM.getItemMeta();
        if (meta != null) {
            meta.setDamage(itemM.getType().getMaxDurability());
            itemM.setItemMeta(meta);
        }
        new BukkitRunnable() {
            final ItemStack item = itemM;
            @Override
            public void run() {
                cooldown -= 0.1;
                cooldown = Math.round(cooldown*10.0)/10.0;
                if (cooldown % 1 == 0) {
                    Damageable meta = (Damageable) item.getItemMeta();
                    if (meta != null) {
                        meta.setDamage(Math.min((int) ((1 - ((cooldownMax - cooldown) / cooldownMax)) * item.getType().getMaxDurability()), item.getType().getMaxDurability()));
                        item.setItemMeta(meta);
                    }
                }
                if (cooldown <= 0) {
                    cooldown = 0;
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 2L, 2L);

    }

    @Override
    public void displayCooldowns() {
        String cooldownT;
        if (cooldown == 0) {
            cooldownT = "READY";
        } else {
            if (cooldown == -1) {
                cooldownT = "WORKING";
            } else {
                cooldownT = cooldown + "s";
            }
        }
        String text = ((cooldown == 0) ? ChatColor.GREEN : ChatColor.RED) + cooldownName + ": " + cooldownT + ChatColor.RESET;
        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }

    public double getCooldown() {
        return cooldown;
    }
}
