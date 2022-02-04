package me.bobthe28th.capturethefart.ctf.itemtypes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class CTFBuildUpItem extends CTFItem {

    double cooldownMax;
    double cooldown = 0;
    int itemMax;
    boolean onCooldown = false;

    public CTFBuildUpItem(String itemName_, Material item_, double cooldownMax_, int itemMax_, Integer customModel_, CTFPlayer player_, Main plugin_, Integer defaultSlot_) {
        super(itemName_, item_, customModel_, player_, plugin_,defaultSlot_);
        cooldownMax = cooldownMax_;
        itemMax = itemMax_;
        amount = itemMax;
    }

    @Override
    public void displayCooldowns() {
        String cooldownT;
        if (cooldown == 0) {
            cooldownT = "FULL";
        } else {
            cooldownT = cooldown + "s";
        }
        String text = ((cooldown == 0) ? ChatColor.GREEN : ChatColor.RED) + itemName + ": " + cooldownT + ChatColor.RESET;
        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }

    public boolean isOnCooldown() {
        return onCooldown;
    }

    public void add(Integer add) {
        int currentA = player.getItemStack(this).getAmount();
        add = Math.min(add, itemMax - currentA);
        player.getItemStack(this).setAmount(currentA + add);
        if (player.getItemStack(this).getAmount() >= itemMax) {
            onCooldown = false;
        }
    }

    public void startCooldown() {
        CTFBuildUpItem t = this;
        onCooldown = true;
        cooldown = cooldownMax;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getItemSlot(t) != -1 && onCooldown) {
                    cooldown -= 0.1;
                    cooldown = Math.round(cooldown * 10.0) / 10.0;

                    if (cooldown <= 0) {
                        cooldown = 0;
                        if (player.getItemStack(t) != null) {
                            player.getItemStack(t).setAmount(player.getItemStack(t).getAmount() + 1);
                        } else {
                            ItemStack it = getItem();
                            it.setAmount(1);
                            player.getPlayer().getInventory().setItem(player.getItemSlot(t), it);
                        }
                        onCooldown = false;
                        if (player.getItemStack(t).getAmount() < itemMax) {
                            startCooldown();
                        }
                        this.cancel();
                    }
                } else {
                    cooldown = 0;
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 2L, 2L);
    }
}
