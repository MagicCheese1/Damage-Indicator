package com.github.magiccheese1.damageindicator.versions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PacketManager1_16_R3 implements PacketManager {

        private static Class<?> getNMSClass(String className) throws ClassNotFoundException {
                return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3]
                                + "." + className);
        }

        public Object buildEntitySpawnPacket(Object entity) {
                Constructor<?> packetConstructor;
                try {
                        packetConstructor = getNMSClass("PacketPlayOutSpawnEntityLiving")
                                        .getConstructor(getNMSClass("EntityLiving"));
                        return packetConstructor.newInstance(entity);
                } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException
                                | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                }
                return null;
        }

        public Object buildEntityMetadataPacket(Object entity, boolean a) {
                try {
                        Constructor<?> packetConstructor = getNMSClass("PacketPlayOutEntityMetadata")
                                        .getConstructor(int.class, getNMSClass("DataWatcher"), boolean.class);
                        int entityId = (int) entity.getClass().getMethod("getId").invoke(entity);
                        Object dataWatcher = entity.getClass().getMethod("getDataWatcher").invoke(entity);
                        return packetConstructor.newInstance(entityId, dataWatcher, a);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                                | NoSuchMethodException | SecurityException | InstantiationException
                                | ClassNotFoundException e) {
                        e.printStackTrace();
                }
                return null;
        }

        public Object buildEntityDestroyPacket(Object entity) {
                Constructor<?> packetConstructor;
                try {
                        packetConstructor = getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int[].class);
                        int entityId = (int) entity.getClass().getMethod("getId").invoke(entity);
                        return packetConstructor.newInstance(new int[] { entityId });
                } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException
                                | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                        e.printStackTrace();
                }
                return null;
        }

        public Object BuildEntityArmorStand(Location location, String name) {
                try {
                        World world = location.getWorld();
                        Object WorldServer = world.getClass().getMethod("getHandle").invoke(world);
                        Constructor<?> entityArmorStandConstructor = getNMSClass("EntityArmorStand")
                                        .getConstructor(getNMSClass("World"), double.class, double.class, double.class);
                        Object entityArmorStand = entityArmorStandConstructor.newInstance(WorldServer, location.getX(),
                                        location.getY(), location.getZ());
                        entityArmorStand.getClass().getMethod("setMarker", boolean.class).invoke(entityArmorStand,
                                        true);
                        entityArmorStand.getClass().getMethod("setInvisible", boolean.class).invoke(entityArmorStand,
                                        true);
                        entityArmorStand.getClass().getMethod("setCustomNameVisible", boolean.class)
                                        .invoke(entityArmorStand, true);
                        Constructor<?> chatMessageConstructor = null;
                        chatMessageConstructor = getNMSClass("ChatMessage").getConstructor(String.class);
                        entityArmorStand.getClass().getMethod("setCustomName", getNMSClass("IChatBaseComponent"))
                                        .invoke(entityArmorStand, chatMessageConstructor.newInstance(name));
                        return entityArmorStand;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                                | NoSuchMethodException | SecurityException | ClassNotFoundException
                                | InstantiationException e) {
                        e.printStackTrace();
                }
                return null;
        }

        public void sendPacket(Object packet, Player player) {
                try {
                        Object handle = player.getClass().getMethod("getHandle").invoke(player);
                        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"))
                                        .invoke(playerConnection, packet);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                                | NoSuchMethodException | SecurityException | NoSuchFieldException
                                | ClassNotFoundException e) {
                        e.printStackTrace();
                }
        }
}
