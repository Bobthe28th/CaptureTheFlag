package me.bobthe28th.capturethefart.ctf;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import net.md_5.bungee.api.chat.ItemTag;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bobthe28th.capturethefart.Main;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CTFCommands implements CommandExecutor {
    Main plugin;
    public CTFCommands(Main plugin_) {
        plugin = plugin_;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }


        switch (cmd.getName().toLowerCase()) {
            case "ctfjoin":
                Player t = null;
                if (args.length > 0) {
                    t = Bukkit.getPlayer(args[0]);
                }
                if (t != null) {
                    if (Main.CTFPlayers.containsKey(t)) {
                        player.sendMessage(ChatColor.RED + t.getName() + " is already a CTF player!" + ChatColor.RESET);
                        return true;
                    }
                    player.sendMessage(ChatColor.GREEN + "Made " + t.getName() + " a CTF player." + ChatColor.RESET);
                    Main.CTFPlayers.put(t, new CTFPlayer(plugin, t));
                    return true;
                }

                if (Main.CTFPlayers.containsKey(player)) {
                    player.sendMessage(ChatColor.RED + "You are already a CTF player!" + ChatColor.RESET);
                    return true;
                }
                player.sendMessage(ChatColor.GREEN + "Made you a CTF player." + ChatColor.RESET);
                Main.CTFPlayers.put(player, new CTFPlayer(plugin, player));

                return true;
            case "ctfleave":
                Player t1 = null;
                if (args.length > 0) {
                    t1 = Bukkit.getPlayer(args[0]);
                }
                if (t1 != null) {
                    if (Main.CTFPlayers.containsKey(t1)) {
                        Main.CTFPlayers.get(t1).remove();
                        player.sendMessage(ChatColor.GREEN + "Removed " + t1.getName() + " from being a CTF player." + ChatColor.RESET);
                        return true;
                    }
                    player.sendMessage(ChatColor.RED + t1.getName() + " is not a CTF player." + ChatColor.RESET);
                    return true;
                }

                if (Main.CTFPlayers.containsKey(player)) {
                    Main.CTFPlayers.get(player).remove();
                    player.sendMessage(ChatColor.GREEN + "Removed you from being a CTF player." + ChatColor.RESET);
                    return true;
                }
                player.sendMessage(ChatColor.RED + "You are not a CTF player." + ChatColor.RESET);
                return true;
            case "ctffulljoin":
                Player t6 = null;
                if (args.length > 0) {
                    t6 = Bukkit.getPlayer(args[0]);
                } else {
                    Bukkit.broadcastMessage(ChatColor.RED + "Please specify a player." + ChatColor.RESET);
                    return true;
                }
                if (t6 != null) {
                    if (args.length > 1) {
                        String className = args[1];
                        Class<?> cClass = null;

                        String[] classNames = Main.CTFClassNames;
                        for (int i = 0; i < classNames.length; i++) {
                            if (className.equals(classNames[i])) {
                                cClass = Main.CTFClasses[i];
                            }
                        }

                        if (cClass != null) {

                            CTFTeam team = null;
                            StringBuilder teamName = new StringBuilder();

                            if (args.length <= 2) {
                                player.sendMessage(ChatColor.RED + "Please specify a team." + ChatColor.RESET);
                                return true;
                            }

                            if (args.length > 3) {
                                for (int i = 2; i < args.length; i++) {
                                    if (i != 2) {
                                        teamName.append(" ");
                                    }
                                    teamName.append(args[i]);
                                }
                            } else {
                                teamName.append(args[2]);
                            }

                            String[] teamNames = Main.getTeamNames();
                            for (int i = 0; i < teamNames.length; i++) {
                                if (teamName.toString().equals(teamNames[i])) {
                                    team = Main.CTFTeams[i];
                                }
                            }

                            if (team != null) {

                                try {
                                    if (!Main.CTFPlayers.containsKey(t6)) {
                                        Main.CTFPlayers.put(t6, new CTFPlayer(plugin, t6));
                                    }

                                    Main.CTFPlayers.get(t6).setTeam(team);

                                    Constructor<?> constructor = cClass.getConstructor(CTFPlayer.class, Main.class);
                                    CTFClass c = (CTFClass) constructor.newInstance(Main.CTFPlayers.get(t6),plugin);
                                    Main.CTFPlayers.get(t6).setClass(c);
                                    t6.setGameMode(GameMode.SURVIVAL);
                                    player.sendMessage(ChatColor.GREEN + "Set " + t6.getName() + " to: " + ChatColor.RESET + c.getFormattedName() + ChatColor.GREEN + " on " + team.getFormattedName());
                                } catch (Exception ignored) {}

                            } else {
                                player.sendMessage(ChatColor.RED + "Please specify a team." + ChatColor.RESET);
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Please specify a class." + ChatColor.RESET);
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Please specify a class." + ChatColor.RESET);
                        return true;
                    }

                }

                return true;
            case "ctfteamjoin":
                Player t2 = null;
                if (args.length > 0) {
                    t2 = Bukkit.getPlayer(args[0]);
                }
                if (t2 != null) {

                    if (Main.CTFPlayers.containsKey(t2)) {
                        CTFTeam team = null;
                        StringBuilder teamName = new StringBuilder();
                        if (args.length > 2) {
                            for (int i = 1; i < args.length; i++) {
                                if (i != 1) {
                                    teamName.append(" ");
                                }
                                teamName.append(args[i]);
                            }
                        } else {
                            teamName.append(args[1]);
                        }

                        String[] teamNames = Main.getTeamNames();
                        for (int i = 0; i < teamNames.length; i++) {
                            if (teamName.toString().equals(teamNames[i])) {
                                team = Main.CTFTeams[i];
                            }
                        }

                        if (team != null) {
                            Main.CTFPlayers.get(t2).setTeam(team);
                            player.sendMessage(ChatColor.GREEN + "Set " + t2.getName() + " to: " + ChatColor.RESET + team.getFormattedName());
                        } else {
                            player.sendMessage(ChatColor.RED + "Please specify a team." + ChatColor.RESET);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + t2.getName() + " is not a CTF player." + ChatColor.RESET);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Please specify a player." + ChatColor.RESET);
                }
                return true;
            case "ctfteamleave":
                Player t3 = null;
                if (args.length > 0) {
                    t3 = Bukkit.getPlayer(args[0]);
                }
                if (t3 != null) {
                    if (Main.CTFPlayers.containsKey(t3)) {
                        Main.CTFPlayers.get(t3).leaveTeam();
                        player.sendMessage(ChatColor.GREEN + "Removed " + t3.getName() + " from their team." + ChatColor.RESET);
                    } else {
                        player.sendMessage(ChatColor.RED + t3.getName() + " is not a CTF player." + ChatColor.RESET);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Please specify a player." + ChatColor.RESET);
                }
                return true;
            case "ctfteams":
                for (int i = 0; i < Main.CTFTeams.length; i++) {
                    player.sendMessage(Main.CTFTeams[i].getFormattedName());
                    for (CTFPlayer p : Main.CTFPlayers.values()) {
                        if (Main.CTFTeams[i] == p.getTeam()) {
                            player.sendMessage(" ● " + p.getPlayer().getName());
                        }
                    }
                }
                return true;
            case "ctfsetclass":
                Player t5 = null;
                if (args.length > 0) {
                    t5 = Bukkit.getPlayer(args[0]);
                }
                if (t5 != null) {
                    if (Main.CTFPlayers.containsKey(t5)) {
                        if (args.length > 1) {
                            String className = args[1];
                            Class<?> cClass = null;

                            String[] classNames = Main.CTFClassNames;
                            for (int i = 0; i < classNames.length; i++) {
                                if (className.equals(classNames[i])) {
                                    cClass = Main.CTFClasses[i];
                                }
                            }

                            if (cClass != null) {
                                try {
                                    Constructor<?> constructor = cClass.getConstructor(CTFPlayer.class, Main.class);
                                    CTFClass c = (CTFClass) constructor.newInstance(Main.CTFPlayers.get(t5),plugin);
                                    Main.CTFPlayers.get(t5).setClass(c);
                                    player.sendMessage(ChatColor.GREEN + "Set " + t5.getName() + " to: " + ChatColor.RESET + c.getFormattedName());
                                } catch (Exception ignored) {}
                            } else {
                                player.sendMessage(ChatColor.RED + "Please specify a class." + ChatColor.RESET);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Please specify a class." + ChatColor.RESET);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + t5.getName() + " is not a CTF player." + ChatColor.RESET);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Please specify a player." + ChatColor.RESET);
                }
                return true;
            case "ctfleaveclass":
                Player t4 = null;
                if (args.length > 0) {
                    t4 = Bukkit.getPlayer(args[0]);
                }
                if (t4 != null) {
                    if (Main.CTFPlayers.containsKey(t4)) {
                        Main.CTFPlayers.get(t4).leaveClass();
                        player.sendMessage(ChatColor.GREEN + "Removed " + t4.getName() + " from their class." + ChatColor.RESET);
                    } else {
                        player.sendMessage(ChatColor.RED + t4.getName() + " is not a CTF player." + ChatColor.RESET);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Please specify a player." + ChatColor.RESET);
                }
                return true;
            case "fly":
                player.setAllowFlight(!player.getAllowFlight());
                player.sendMessage(player.getAllowFlight() ? ChatColor.GREEN + "Flight Enabled" : ChatColor.RED + "Flight Disabled");
                return true;
            case "heal":
                player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                player.setFoodLevel(20);
                player.setSaturation(20.0F);
                return true;
            case "test":

//                Main.CTFFlags[0].fall(player.getLocation());

//                PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
//                packet.getIntegers().write(0,player.getEntityId());
////                WrappedDataWatcher w = WrappedDataWatcher.getEntityWatcher(player).deepClone();
////                w.setObject(6, player.getPose());
////                packet.getWatchableCollectionModifier().write(0,w.getWatchableObjects());
//                WrappedDataWatcher watcher = new WrappedDataWatcher();
//                watcher.setEntity(player);
//                WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
//////                watcher.setObject(0, serializer , (byte) (0x40)); glow
//////                watcher.setObject(0, serializer , (byte) (0x01));
//////                watcher.setObject(9, serializer , 10.0F);
//                watcher.setObject(6, serializer, );
//
//                packet.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());
//                try {
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//			PacketContainer packet = new PacketContainer(PacketType.Play.Server.EXPLOSION);
//			packet.getDoubles()
//				.write(0,player.getLocation().getX())
//				.write(1,player.getLocation().getY())
//				.write(2,player.getLocation().getZ());
//			packet.getFloat().write(0,3.0F);

//			PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
//			packet.getBlockPositionModifier().write(0, new BlockPosition(player.getLocation().toVector()));
//			packet.getBlockData().write(0, WrappedBlockData.createData(Material.SAND));


//			PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
//			packet.getBlockPositionModifier().write(0, new BlockPosition(player.getLocation().add(0.0,-1.0,0.0).toVector()));
//			packet.getIntegers().write(0, 78).write(1, 3);

//			PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
//
//			packet.getBytes().write(0,(byte)5);
//			packet.getFloat().write(0,101.0F);

//			PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
//            packet.getIntegers().write(0,player.getEntityId());
//            ArrayList<Pair<EnumWrappers.ItemSlot, ItemStack>> al = new ArrayList<>();
//            Pair<EnumWrappers.ItemSlot, ItemStack> pa = new Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemStack(Material.RED_BANNER));
//            al.add(pa);
//            List<Pair<EnumWrappers.ItemSlot, ItemStack>> m = al;
//            packet.getSlotStackPairLists().write(0, m);
////            packet.getItemModifier().write(0,new ItemStack(Material.RED_BANNER));
////            packet.getItemSlots().write(0, EnumWrappers.ItemSlot.HEAD);
////            packet.getItemSlots().write(0, EnumWrappers.ItemSlot.HEAD);
////                packet.getItemModifier().write(0,new ItemStack(Material.RED_BANNER));
//
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

			try {
		        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }

//                player.setCooldown(Material.DIRT, 120);


//			PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(ep, (byte) 35);
//
//			try {
//	            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
//	        } catch (InvocationTargetException e) {
//	            e.printStackTrace();
//	        }
//			PacketPlayOutEntityStatus status = new PacketPlayOutEntityStatus(ep, (byte) 35);
//			ep.playerConnection.sendPacket(status);

//			player.spawnParticle(Particle.TOTEM, player.getLocation(), 10);

//			player.playEffect(EntityEffect.TOTEM_RESURRECT);


//			PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_STATUS);
//			packet.getIntegers().write(0, player.getEntityId());
//			packet.getBytes().write(0, (byte) 35);
//
//			try {
//	            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
//	        } catch (InvocationTargetException e) {
//	            e.printStackTrace();
//	        }

                return true;
            default:
                return false;
        }


//		return true;
    }

}
