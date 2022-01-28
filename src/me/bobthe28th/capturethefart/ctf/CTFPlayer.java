package me.bobthe28th.capturethefart.ctf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.persistence.PersistentDataType;

import me.bobthe28th.capturethefart.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.util.EulerAngle;

public class CTFPlayer implements Listener {

    Player player;
    CTFTeam team = null;
    CTFItem[] hotbar = new CTFItem[9];
    Main plugin;
    int cooldownTask;
	CTFClass pClass;
    CTFFlag carriedFlag = null;
    ArmorStand flagOnHead = null;

    public CTFPlayer(Main plugin_, Player p) {
        player = p;
        plugin = plugin_;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        player.setLevel(0);
        player.setExp(0.0F);
        player.setGlowing(false);
        for (Entity e : player.getPassengers()) {
            player.removePassenger(e);
            e.remove();
        }

        cooldownTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            int slot = player.getInventory().getHeldItemSlot();
            if (hotbar[slot] != null) {
                hotbar[slot].displayCooldowns();
            } else {
                Objects.requireNonNull(player.getPlayer()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            }
        }, 0, 2);

    }

    public void setTeam(CTFTeam t) {
        team = t;
        t.getTeam().addEntry(player.getName());
    }

    public void leaveTeam() {
        team.getTeam().removeEntry(player.getName());
        team = null;
    }

    public CTFTeam getTeam() {
        return team;
    }

    public String getFormattedName() {
        if (team != null) {
            return team.getColor() + player.getName() + ChatColor.RESET;
        } else {
            return player.getName();
        }
    }

    public void pickupFlag(CTFFlag flag) {
        player.setGlowing(true);
        carriedFlag = flag;
        flagOnHead = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        flagOnHead.setInvisible(true);
        flagOnHead.setInvulnerable(true);
        flagOnHead.setMarker(true);
        flagOnHead.setSmall(true);
        if (flagOnHead.getEquipment() != null) {
            flagOnHead.getEquipment().setHelmet(new ItemStack(flag.getTeam().getBanner()));
        }

        flag.getTeam().getTeam().addEntry(flagOnHead.getUniqueId().toString());
        flagOnHead.setGlowing(true); //set to false to make flag not glow

        player.addPassenger(flagOnHead);

    }

    public boolean isCarringFlag() {
        return carriedFlag != null;
    }

    public void captureFlag() {
        carriedFlag.capture(this);
        player.setGlowing(false);
        carriedFlag = null;
        flagOnHead.remove();
    }

    public void dropFlag() {
        player.setGlowing(false);
        carriedFlag.fall(player.getLocation());
        carriedFlag = null;
        flagOnHead.remove();
    }

    public void death(boolean byEntity) {
        if (carriedFlag != null) {
            dropFlag();
        }
    }

    public void remove() {
        leaveClass();
        leaveTeam();
        Bukkit.getServer().getScheduler().cancelTask(cooldownTask);
        HandlerList.unregisterAll(this);

        removeItems();

        Main.CTFPlayers.remove(player);
    }

    public void removeItems() {
        for (int i = 0; i <= 35; i ++) {
            ItemStack invItem = player.getInventory().getItem(i);
            if (invItem != null) {
                ItemMeta meta = invItem.getItemMeta();
                if (meta != null) {
                    Byte ctfitemData = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE);
                    if (ctfitemData != null && ctfitemData == (byte) 1) {
                        if (i < 9) {
                            hotbar[i] = null;
                        }
                        invItem.setAmount(0);
                    }
                }
            }
        }
        player.updateInventory();
    }

    public Player getPlayer() {
        return player;
    }

	public <C extends CTFClass> void setClass(C cl) {
		if (pClass != null) {
            pClass.deselect();
        }
	removeItems();
        pClass = cl;
        pClass.giveItems();
	}

    public void leaveClass() {
        pClass.deselect();
        removeItems();
        pClass = null;
    }

    public <I extends CTFItem> void giveItem(I it, Integer slot) {
        hotbar[slot] = it;
        player.getInventory().setItem(slot,it.getItem());
    }

    public <I extends CTFItem> ItemStack getItemStack(I it) {
        int index = Arrays.asList(hotbar).indexOf(it);
        if (index == -1) {
            return null;
        }
        return player.getInventory().getItem(index);
    }

    public <I extends CTFItem> int getItemSlot(I it) {
        return Arrays.asList(hotbar).indexOf(it);
    }

    public CTFItem getItem(Integer slot) {
        return hotbar[slot];
    }

    public void removeItem(Integer slot) {
        hotbar[slot] = null;
    }

    public int getHeldItemSlot() {
        return player.getInventory().getHeldItemSlot();
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if (event.getPlayer() != player) return;
        int slot = player.getInventory().getHeldItemSlot();
        if (hotbar[slot] != null) {
            hotbar[slot].onclickAction(event);
        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (event.getPlayer() != player) return;
        int slot = player.getInventory().getHeldItemSlot();
        if (hotbar[slot] != null) {
            hotbar[slot].onblockPlace(event);
        }
    }

    @EventHandler
    public void onPlacePickupArrow(PlayerPickupArrowEvent event) {
        if (event.getPlayer() != player) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player p && event.getClickedInventory() != null) {
            if (p != player) return;
            List<ItemStack> items = new ArrayList<>();
            items.add(event.getCurrentItem());
            items.add(event.getCursor());
            items.add((event.getClick() == ClickType.NUMBER_KEY) ? event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem());
            for(ItemStack item : items) {
                if(item != null && item.hasItemMeta()) {
                    if (Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(new NamespacedKey(plugin,"ctfitem"),  PersistentDataType.BYTE) != null) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getPlayer() != player) return;
        ItemStack item = event.getItemDrop().getItemStack();
        if(item.hasItemMeta()) {
            if (Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(new NamespacedKey(plugin,"ctfitem"),  PersistentDataType.BYTE) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (event.getPlayer() != player) return;
        if (player.getGameMode() != GameMode.CREATIVE) {
            if (Main.breakableBlocks.contains(event.getBlock())) {
                event.setDropItems(false);
                Main.breakableBlocks.remove(event.getBlock());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (event.getPlayer() != player) return;
        event.setCancelled(true);
    }
}
