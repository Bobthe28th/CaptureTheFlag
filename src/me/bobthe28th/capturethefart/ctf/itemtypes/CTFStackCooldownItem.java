package me.bobthe28th.capturethefart.ctf.itemtypes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class CTFStackCooldownItem extends CTFItem {
    double cooldown = 0;
    double cooldownMax;
    String cooldownName;
    Material cooldownItem;

    public CTFStackCooldownItem(String itemName_, Material item_, Integer customModel_, String cooldownName_, double cooldownMax_, Material cooldownItem_, CTFPlayer player_, Main plugin_) {
        super(itemName_,item_,customModel_,player_,plugin_);
        cooldownName = cooldownName_;
        cooldownMax = cooldownMax_;
        cooldownItem = cooldownItem_;
    }

    public void startCooldown() {
        cooldown = cooldownMax;
        CTFItem t = this;
        ItemStack cItem = new ItemStack(cooldownItem);
        cItem.setAmount((int)(cooldown));
        player.getPlayer().getInventory().setItem(player.getItemSlot(t),cItem);
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldown -= 0.1;
                cooldown = Math.round(cooldown*10.0)/10.0;
                if (cooldown % 1 == 0) {
                    ItemStack cItem = new ItemStack(cooldownItem);
                    cItem.setAmount((int)(cooldown));
                    if (player.getItemSlot(t) != -1) {
                        player.getPlayer().getInventory().setItem(player.getItemSlot(t),cItem);
                    }
                }
                if (cooldown <= 0) {
                    cooldown = 0;
                    if (player.getItemSlot(t) != -1) {
                        player.getPlayer().getInventory().setItem(player.getItemSlot(t),t.getItem());
                    }
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
