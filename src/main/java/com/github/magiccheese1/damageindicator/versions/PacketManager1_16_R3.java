package com.github.magiccheese1.damageindicator.versions;

import com.github.magiccheese1.damageindicator.exceptions.NMSAccessException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

/**
 * Implementation of the packet manager for the 1.16 minecraft java version.
 * The implementation relies purely on reflective access to the server internals.
 */
public final class PacketManager1_16_R3 implements PacketManager {

    private static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3]
            + "." + className);
    }

    @NotNull
    public Object buildEntitySpawnPacket(@NotNull Object entity) {
        try {
            return getNMSClass("PacketPlayOutSpawnEntityLiving")
                .getConstructor(getNMSClass("EntityLiving"))
                .newInstance(entity);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity spawn packet", e);
        }
    }

    @NotNull
    public Object buildEntityMetadataPacket(@NotNull Object entity, boolean forceUpdateAll) {
        try {
            final Constructor<?> packetConstructor = getNMSClass("PacketPlayOutEntityMetadata")
                .getConstructor(int.class, getNMSClass("DataWatcher"), boolean.class);

            final int entityId = (int) entity.getClass().getMethod("getId").invoke(entity);
            final Object dataWatcher = entity.getClass().getMethod("getDataWatcher").invoke(entity);
            return packetConstructor.newInstance(entityId, dataWatcher, forceUpdateAll);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity metadata packet", e);
        }
    }

    @SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
    @NotNull
    public Object buildEntityDestroyPacket(@NotNull Object entity) {
        try {
            final int entityId = (int) entity.getClass().getMethod("getId").invoke(entity);
            return getNMSClass("PacketPlayOutEntityDestroy")
                .getConstructor(int[].class)
                .newInstance(new int[]{entityId});
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity destroy packet", e);
        }
    }

    @NotNull
    public Object buildEntityArmorStand(@NotNull Location location, @NotNull String name) {
        try {
            final Constructor<?> entityArmorStandConstructor = getNMSClass("EntityArmorStand")
                .getConstructor(getNMSClass("World"), double.class, double.class, double.class);
            final Constructor<?> chatMessageConstructor = getNMSClass("ChatMessage").getConstructor(String.class);

            final World world = location.getWorld();
            final Object worldServer = world.getClass().getMethod("getHandle").invoke(world);

            final Object entityArmorStand = entityArmorStandConstructor.newInstance(
                worldServer,
                location.getX(), location.getY(), location.getZ()
            );
            entityArmorStand.getClass().getMethod("setMarker", boolean.class).invoke(entityArmorStand, true);
            entityArmorStand.getClass().getMethod("setInvisible", boolean.class).invoke(entityArmorStand, true);
            entityArmorStand.getClass().getMethod("setCustomNameVisible", boolean.class).invoke(entityArmorStand, true);
            entityArmorStand.getClass().getMethod("setCustomName", getNMSClass("IChatBaseComponent")).invoke(
                entityArmorStand, chatMessageConstructor.newInstance(name)
            );
            return entityArmorStand;
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity armor stand", e);
        }
    }

    public void sendPacket(@NotNull Object packet, @NotNull Player player) {
        try {
            final Object handle = player.getClass().getMethod("getHandle").invoke(player);
            final Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException(
                String.format("Failed to queue packet for player %s", player.getUniqueId()),
                e
            );
        }
    }
}
