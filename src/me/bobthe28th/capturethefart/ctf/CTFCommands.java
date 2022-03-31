package me.bobthe28th.capturethefart.ctf;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import me.bobthe28th.capturethefart.Main;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.*;


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

        Player target = null;

        switch (cmd.getName().toLowerCase()) {
            case "ctfjoin":
                if (args.length > 0) {
                    target = Bukkit.getPlayer(args[0]);
                }
                if (target != null) {
                    if (Main.CTFPlayers.containsKey(target)) {
                        player.sendMessage(ChatColor.RED + target.getName() + " is already a CTF player!" + ChatColor.RESET);
                        return true;
                    }
                    player.sendMessage(ChatColor.GREEN + "Made " + target.getName() + " a CTF player." + ChatColor.RESET);
                    Main.CTFPlayers.put(target, new CTFPlayer(plugin, target));
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
                if (args.length > 0) {
                    target = Bukkit.getPlayer(args[0]);
                }
                if (target != null) {
                    if (Main.CTFPlayers.containsKey(target)) {
                        Main.CTFPlayers.get(target).remove();
                        player.sendMessage(ChatColor.GREEN + "Removed " + target.getName() + " from being a CTF player." + ChatColor.RESET);
                        return true;
                    }
                    player.sendMessage(ChatColor.RED + target.getName() + " is not a CTF player." + ChatColor.RESET);
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
                if (args.length > 0) {
                    target = Bukkit.getPlayer(args[0]);
                } else {
                    Bukkit.broadcastMessage(ChatColor.RED + "Please specify a player." + ChatColor.RESET);
                    return true;
                }
                if (target != null) {
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
                                    if (!Main.CTFPlayers.containsKey(target)) {
                                        Main.CTFPlayers.put(target, new CTFPlayer(plugin, target));
                                    }

                                    Main.CTFPlayers.get(target).setTeam(team);

                                    Constructor<?> constructor = cClass.getConstructor(CTFPlayer.class, Main.class);
                                    CTFClass c = (CTFClass) constructor.newInstance(Main.CTFPlayers.get(target),plugin);
                                    Main.CTFPlayers.get(target).setClass(c);
                                    target.setGameMode(GameMode.SURVIVAL);
                                    player.sendMessage(ChatColor.GREEN + "Set " + target.getName() + " to: " + ChatColor.RESET + c.getFormattedName() + ChatColor.GREEN + " on " + team.getFormattedName());
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
                if (args.length > 0) {
                    target = Bukkit.getPlayer(args[0]);
                }
                if (target != null) {

                    if (Main.CTFPlayers.containsKey(target)) {
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
                            Main.CTFPlayers.get(target).setTeam(team);
                            player.sendMessage(ChatColor.GREEN + "Set " + target.getName() + " to: " + ChatColor.RESET + team.getFormattedName());
                        } else {
                            player.sendMessage(ChatColor.RED + "Please specify a team." + ChatColor.RESET);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + target.getName() + " is not a CTF player." + ChatColor.RESET);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Please specify a player." + ChatColor.RESET);
                }
                return true;
            case "ctfteamleave":
                if (args.length > 0) {
                    target = Bukkit.getPlayer(args[0]);
                }
                if (target != null) {
                    if (Main.CTFPlayers.containsKey(target)) {
                        Main.CTFPlayers.get(target).leaveTeam();
                        player.sendMessage(ChatColor.GREEN + "Removed " + target.getName() + " from their team." + ChatColor.RESET);
                    } else {
                        player.sendMessage(ChatColor.RED + target.getName() + " is not a CTF player." + ChatColor.RESET);
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
                if (args.length > 0) {
                    target = Bukkit.getPlayer(args[0]);
                }
                if (target != null) {
                    if (Main.CTFPlayers.containsKey(target)) {
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
                                    CTFClass c = (CTFClass) constructor.newInstance(Main.CTFPlayers.get(target),plugin);
                                    Main.CTFPlayers.get(target).setClass(c);
                                    player.sendMessage(ChatColor.GREEN + "Set " + target.getName() + " to: " + ChatColor.RESET + c.getFormattedName());
                                } catch (Exception ignored) {}
                            } else {
                                player.sendMessage(ChatColor.RED + "Please specify a class." + ChatColor.RESET);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Please specify a class." + ChatColor.RESET);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + target.getName() + " is not a CTF player." + ChatColor.RESET);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Please specify a player." + ChatColor.RESET);
                }
                return true;
            case "ctfleaveclass":
                if (args.length > 0) {
                    target = Bukkit.getPlayer(args[0]);
                }
                if (target != null) {
                    if (Main.CTFPlayers.containsKey(target)) {
                        Main.CTFPlayers.get(target).leaveClass();
                        player.sendMessage(ChatColor.GREEN + "Removed " + target.getName() + " from their class." + ChatColor.RESET);
                    } else {
                        player.sendMessage(ChatColor.RED + target.getName() + " is not a CTF player." + ChatColor.RESET);
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
//                player.setSaturation(20.0F);
                return true;
            case "music":
                if (args.length > 0) {
                    switch (args[0]) {
                        case "play":
                            if (args.length > 1) {
                                if (Arrays.asList(Main.music).contains(args[1])) {
                                    Main.playMusic(args[1],plugin,true);
                                } else {
                                    player.sendMessage(ChatColor.RED + "Please specify a REAL song." + ChatColor.RESET);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Please specify a song." + ChatColor.RESET);
                            }
                            return true;
                        case "stop":
                            Main.stopMusic(true);
                            return true;
                        default:
                            return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Please specify a command." + ChatColor.RESET);
                }
                return true;
            case "test":

                Main.gameController.selectTeam();
//                Main.fakeClass(player,UUID.fromString("00000000-0000-0000-0000-000000000000"),70,new Demo(null,plugin),plugin);

//                UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
//
//                CTFClass cClass = new Paladin(null,plugin);
//
//                PacketContainer add = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
//
//                add.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
//
//                WrappedGameProfile profile = new WrappedGameProfile(uuid, cClass.getFormattedName());
//
//                profile.getProperties().put("textures", WrappedGameProfile.fromPlayer(player).getProperties().get("textures").iterator().next());
//
//                WrappedChatComponent name = WrappedChatComponent.fromText(profile.getName());
//
//                List<PlayerInfoData> pd = new ArrayList<>();
//
//                pd.add(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.CREATIVE, name));
//
//                add.getPlayerInfoDataLists().write(0,pd);
//
//                try {
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, add);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//                PacketContainer spawn = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
//
//                spawn.getIntegers().write(0,70);
//
//                spawn.getUUIDs().write(0, uuid);
//
//                spawn.getBytes().write(0,(byte)0).write(1,(byte)0);
//
//                Location pLoc = player.getLocation();
//                spawn.getDoubles().write(0,pLoc.getX()).write(1,pLoc.getY()).write(2,pLoc.getZ());
//
//                try {
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawn);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                PacketContainer outerSkin = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
//
//                outerSkin.getIntegers().write(0,70);
//
//                WrappedDataWatcher watcher = new WrappedDataWatcher(player);
//
//                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(17, WrappedDataWatcher.Registry.get(Byte.class)), (byte)127);
//
//                outerSkin.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());
//
//                try {
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, outerSkin);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                PacketContainer equipment = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
//
//                equipment.getIntegers().write(0,70);
//
//                List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = new ArrayList<>();
//
//
//                Material[] armor = cClass.getArmor();
//                Integer helmetM = cClass.getHelmetModel();
//                Enchantment[][] armorE = cClass.getEnchantments();
//                Integer[][] armorEL = cClass.getEnchantmentLevels();
//
//                cClass.deselect();
//
//                ItemStack[] armorItem = new ItemStack[3];
//                for (int i = 0; i < armor.length; i++) {
//                    if (armor[i] != null) {
//                        armorItem[i] = new ItemStack(armor[i]);
//                        ItemMeta meta = armorItem[i].getItemMeta();
//                        if (meta != null) {
//                            if (i == 0) {
//                                meta.setCustomModelData(helmetM);
//                            }
//                            if (armorE != null && armorEL != null) {
//                                if (armorE[i] != null && armorEL[i] != null) {
//                                    for (int j = 0; j < armorE[i].length; j++) {
//                                        if (armorE[i][j] != null && armorEL[i][j] != null) {
//                                            meta.addEnchant(armorE[i][j],armorEL[i][j], true);
//                                        }
//                                    }
//                                }
//                            }
//                            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE, (byte) 1);
//                            armorItem[i].setItemMeta(meta);
//                        }
//                        switch (i) {
//                            case 0 -> pairList.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, armorItem[i]));
//                            case 1 -> pairList.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, armorItem[i]));
//                            case 2 -> pairList.add(new Pair<>(EnumWrappers.ItemSlot.FEET, armorItem[i]));
//                        }
//                    }
//                }
//
//                ItemStack chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE);
//                LeatherArmorMeta lam = (LeatherArmorMeta) chestPlate.getItemMeta();
//                if (Main.CTFPlayers.containsKey(player)) {
//                    CTFPlayer cPlayer = Main.CTFPlayers.get(player);
//                    if (lam != null) {
//                        lam.setColor(cPlayer.getTeam().getColor());
//                        lam.getPersistentDataContainer().set(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE, (byte) 1);
//                        chestPlate.setItemMeta(lam);
//                    }
//                }
//                pairList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, chestPlate));
//                equipment.getSlotStackPairLists().write(0, pairList);
//
//
//                try {
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, equipment);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                WorldCreator wc = new WorldCreator("hell");
//                wc.environment(World.Environment.NORMAL);
//                wc.type(WorldType.NORMAL);
//                World wo = wc.createWorld();
//                for (Player pl : Bukkit.getOnlinePlayers()) {
//                    pl.teleport(new Location(wo, 0, 70, 0));
//                }
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
//            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
//
//			try {
//		        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		    }

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
