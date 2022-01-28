package me.bobthe28th.capturethefart.ctf.itemtypes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class CTFToolCooldownItem extends CTFItem {

    double cooldown = 0;
    double cooldownMax;
    String cooldownName;

    public CTFToolCooldownItem(String itemName_, Material item_, Integer customModel_, String cooldownName_, double cooldownMax_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super(itemName_,item_,customModel_,player_,plugin_,defaultSlot_);
        cooldownName = cooldownName_;
        cooldownMax = cooldownMax_;
    }

    public void startCooldown() {
        cooldown = cooldownMax;
        ItemStack item = player.getItemStack(this);
        Damageable meta = (Damageable) item.getItemMeta();
        if (meta != null) {
            meta.setDamage(item.getType().getMaxDurability());
            item.setItemMeta(meta);
        }
        new BukkitRunnable() {
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
