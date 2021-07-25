package com.github.magiccheese1.damageindicator.versions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.decoration.EntityArmorStand;

public class PacketManager1_17_R1 implements PacketManager {
    private static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        return Class.forName(
                "net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + className);
    }

    public Object buildEntitySpawnPacket(Object entity) {
        return new PacketPlayOutSpawnEntityLiving((EntityLiving) entity);
    }

    public Object buildEntityMetadataPacket(Object entity, boolean a) {
        try {
            int entityId = (int) entity.getClass().getMethod("getId").invoke(entity);
            Object dataWatcher = entity.getClass().getMethod("getDataWatcher").invoke(entity);
            return new PacketPlayOutEntityMetadata(entityId, (DataWatcher) dataWatcher, a);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object buildEntityDestroyPacket(Object entity) {
        try {
            int entityId = (int) entity.getClass().getMethod("getId").invoke(entity);
            return new PacketPlayOutEntityDestroy(entityId);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object BuildEntityArmorStand(Location location, String name) {
        try {
            World world = location.getWorld();
            WorldServer WorldServer = (WorldServer) world.getClass().getMethod("getHandle").invoke(world);
            Object entityArmorStand = new EntityArmorStand((net.minecraft.world.level.World) WorldServer,
                    location.getX(), location.getY(), location.getZ());
            entityArmorStand.getClass().getMethod("setMarker", boolean.class).invoke(entityArmorStand, true);
            entityArmorStand.getClass().getMethod("setInvisible", boolean.class).invoke(entityArmorStand, true);
            entityArmorStand.getClass().getMethod("setCustomNameVisible", boolean.class).invoke(entityArmorStand, true);
            entityArmorStand.getClass().getMethod("setCustomName", IChatBaseComponent.class).invoke(entityArmorStand,
                    new ChatMessage(name));
            return entityArmorStand;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendPacket(Object packet, Player player) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("b").get(handle);
            playerConnection.getClass().getMethod("sendPacket", Packet.class).invoke(playerConnection, packet);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
