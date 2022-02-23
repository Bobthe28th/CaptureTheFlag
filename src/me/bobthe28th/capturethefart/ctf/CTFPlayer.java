package me.bobthe28th.capturethefart.ctf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFItem;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import me.bobthe28th.capturethefart.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class CTFPlayer implements Listener {

    Player player;
    CTFTeam team = null;
    CTFItem[] hotbar = new CTFItem[9];
    Main plugin;
    int cooldownTask;
    CTFClass pClass;
    CTFFlag carriedFlag = null;
    ArmorStand flagOnHead = null;
    ArrayList<String> glowReason = new ArrayList<>();
    double healCooldown = 0.0;
    boolean onHealCooldown = false;


    public CTFPlayer(Main plugin_, Player p) {
        player = p;
        plugin = plugin_;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        player.setLevel(0);
        player.setExp(0.0F);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getDefaultValue());
        player.setFoodLevel(20);
        player.setSaturation(0F);
        for (PotionEffect pEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(pEffect.getType());
        }
        player.setGlowing(false);
        for (Entity e : player.getPassengers()) {
            player.removePassenger(e);
            e.remove();
        }

        cooldownTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            int slot = player.getInventory().getHeldItemSlot();
            if (getItem(slot) != null) {
                getItem(slot).displayCooldowns();
            } else {
                Objects.requireNonNull(player.getPlayer()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            }
        }, 0, 2);

    }

    public void setTeam(CTFTeam t) {
        team = t;
        t.getTeam().addEntry(player.getName());
        giveArmor();
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
            return team.getChatColor() + player.getName() + ChatColor.RESET;
        } else {
            return player.getName();
        }
    }

    public void pickupFlag(CTFFlag flag) {
        addGlow("flag");
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

    public void addGlow(String reason) {
        player.addPotionEffect((new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, true, false, true)));
        if (!glowReason.contains(reason)) {
            glowReason.add(reason);
        }
    }

    public void removeGlow(String reason) {
        glowReason.remove(reason);
        if (glowReason.isEmpty()) {
            player.removePotionEffect(PotionEffectType.GLOWING);
        }
    }

    public boolean isCarringFlag() {
        return carriedFlag != null;
    }

    public void captureFlag() {
        carriedFlag.capture(this);
        removeGlow("flag");
        carriedFlag = null;
        flagOnHead.remove();
    }

    public void dropFlag() {
        removeGlow("flag");
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

    public void giveArmor() {
        if (team != null) {
            ItemStack chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta lam = (LeatherArmorMeta) chestPlate.getItemMeta();
            if (lam != null) {
                lam.setColor(team.getColor());
                lam.getPersistentDataContainer().set(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE, (byte) 1);
                chestPlate.setItemMeta(lam);
            }
            player.getInventory().setItem(EquipmentSlot.CHEST, chestPlate);
            if (pClass != null) {
                pClass.giveArmor();
            }
        }
    }

    public void removeItems() {
        for (int i = 0; i <= 35; i ++) {
            ItemStack invItem = player.getInventory().getItem(i);
            if (invItem != null) {
                ItemMeta meta = invItem.getItemMeta();
                if (meta != null) {
                    Byte ctfitemData = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE);
                    if (ctfitemData != null && ctfitemData == (byte) 1) {
                        if (getItem(i) != null) {
                            hotbar[getItem(i).getDefaultSlot()] = null;
                        }
                        invItem.setAmount(0);
                    }
                }
            }
        }
        //offhand
        ItemStack invItem = player.getInventory().getItem(40);
        if (invItem != null) {
            ItemMeta meta = invItem.getItemMeta();
            if (meta != null) {
                Byte ctfitemData = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE);
                if (ctfitemData != null && ctfitemData == (byte) 1) {
                    if (getItem(40) != null) {
                        hotbar[getItem(40).getDefaultSlot()] = null;
                    }
                    invItem.setAmount(0);
                }
            }
        }
        //armor
        if (player.getEquipment() != null) {
            for (ItemStack eq : player.getEquipment().getArmorContents()) {
                if (eq != null) {
                    ItemMeta meta = eq.getItemMeta();
                    if (meta != null) {
                        Byte ctfitemData = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE);
                        if (ctfitemData != null && ctfitemData == (byte) 1) {
                            eq.setAmount(0);
                        }
                    }
                }
            }
        }

        player.updateInventory();
    }

    public void removeArmor() {
        if (player.getEquipment() != null) {
            for (ItemStack eq : player.getEquipment().getArmorContents()) {
                if (eq != null) {
                    ItemMeta meta = eq.getItemMeta();
                    if (meta != null) {
                        Byte ctfitemData = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE);
                        if (ctfitemData != null && ctfitemData == (byte) 1) {
                            eq.setAmount(0);
                        }
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
        for (PotionEffect pEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(pEffect.getType());
        }
        pClass = cl;
        pClass.giveItems();
        giveArmor();
	}

    public void leaveClass() {
        pClass.deselect();
        removeItems();
        pClass = null;
    }

    public <I extends CTFItem> void giveItem(I it) {
        hotbar[it.getDefaultSlot()] = it;
        player.getInventory().setItem(it.getDefaultSlot(),it.getItem());
    }

    public <I extends CTFItem> ItemStack getItemStack(I it) {
        return player.getInventory().getItem(getItemSlot(it));
    }

    public <I extends CTFItem> int getItemSlot(I it) {
        for (CTFItem cit : hotbar) {
            if (cit == it) {
                return cit.getSlot();
            }
        }
        return -1;
    }

    public CTFItem getItem(Integer slot) {
        for (CTFItem cit : hotbar) {
            if (cit != null) {
                if (cit.getSlot() == slot) {
                    return cit;
                }
            }
        }
        return null;
    }

    public void regen() {
        long rate = 16L;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (onHealCooldown) {
                    this.cancel();
                } else {
                    if (player.getHealth() < Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getDefaultValue()) {
                        PacketContainer packet = new PacketContainer(PacketType.Play.Server.UPDATE_HEALTH);
                        double healAmout = Math.min(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getDefaultValue(),player.getHealth() + 1);
                        packet.getFloat().write(0, (float)healAmout);
                        packet.getIntegers().write(0,20);
                        try {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        player.setHealth(healAmout);
                    } else {
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, rate);
    }

    public void startHealCooldown() {
        onHealCooldown = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                healCooldown -= 0.1;
                healCooldown = Math.round(healCooldown*10.0)/10.0;
                if (healCooldown <= 0) {
                    healCooldown = 0;
                    onHealCooldown = false;
                    regen();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 2L, 2L);
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if (event.getPlayer() != player) return;
        int slot = player.getInventory().getHeldItemSlot();
        if (getItem(slot) != null) {
            getItem(slot).onclickAction(event);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getPlayer() != player) return;
        int slot = player.getInventory().getHeldItemSlot();
        if (getItem(slot) != null) {
            getItem(slot).onConsume(event);
        }
    }

    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        if (event.getPlayer() != player) return;
        int slot = event.getNewSlot();
        if (getItem(slot) != null) {
            getItem(slot).onHold(event);
        }

        //display cooldowns
        if (getItem(slot) != null) {
            getItem(slot).displayCooldowns();
        } else {
            Objects.requireNonNull(player.getPlayer()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (event.getPlayer() != player) return;
        int slot = player.getInventory().getHeldItemSlot();
        if (getItem(slot) != null) {
            getItem(slot).onblockPlace(event);
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
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer() != player) return;
        event.setCancelled(true);
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
            if (Main.breakableBlocks.containsKey(event.getBlock())) {
                if (Main.breakableBlocks.get(event.getBlock()) == team) {
                    pClass.breakBlock(event.getBlock());
                }
                event.setDropItems(false);
                Main.breakableBlocks.remove(event.getBlock());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player pf) {
            if (pf != player) return;
            healCooldown = 7.0;
            if (!onHealCooldown) {
                startHealCooldown();
            }
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player pf) {
            if (pf != player) return;
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player pf) {
            if (pf != player) return;
            player.setFoodLevel(20);
            player.setSaturation(0F);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (event.getPlayer() != player) return;
        event.setCancelled(true);
    }
}
