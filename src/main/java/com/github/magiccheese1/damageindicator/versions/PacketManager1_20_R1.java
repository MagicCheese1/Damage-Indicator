package com.github.magiccheese1.damageindicator.versions;

import com.github.magiccheese1.damageindicator.exceptions.NMSAccessException;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Implementation of the packet manager for the 1.20 minecraft java version.
 * The implementation uses a mixture of direct calls against the re-obfuscated server internals and reflection.
 */
public final class PacketManager1_20_R1 implements PacketManager {

    private final Method entityGetIdMethod;
    private final Method entityGetHandleMethod;
    private final Method entityGetBukkitEntityMethod;
    private final Method worldGetHandleMethod;
    private final Method playerConnectionSendPacketMethod;
    private final Method entityGetDataMethod;
    private final Method synchedEntityDataPackDirtyMethod;
    private final Constructor<?> clientboundSetEntityDataPacketInit;
    private final Field entityPlayerPlayerConnectionField;


    public PacketManager1_20_R1(final @NotNull Method entityGetIdMethod,
                                final @NotNull Method entityGetHandleMethod,
                                final @NotNull Method entityGetBukkitEntityMethod,
                                final @NotNull Method worldGetHandleMethod,
                                final @NotNull Method playerConnectionSendPacketMethod,
                                final @NotNull Method entityGetDataMethod,
                                final @NotNull Method synchedEntityDataGetDirtyMethod,
                                final Constructor<?> clientboundSetEntityDataPacketInitMethod,
                                final @NotNull Field entityPlayerPlayerConnectionField) {
        this.entityGetIdMethod = entityGetIdMethod;
        this.entityGetHandleMethod = entityGetHandleMethod;
        this.entityGetBukkitEntityMethod = entityGetBukkitEntityMethod;
        this.worldGetHandleMethod = worldGetHandleMethod;
        this.playerConnectionSendPacketMethod = playerConnectionSendPacketMethod;
        this.entityGetDataMethod = entityGetDataMethod;
        this.synchedEntityDataPackDirtyMethod = synchedEntityDataGetDirtyMethod;
        this.clientboundSetEntityDataPacketInit = clientboundSetEntityDataPacketInitMethod;
        this.entityPlayerPlayerConnectionField = entityPlayerPlayerConnectionField;
    }

    @NotNull
    public static PacketManager1_20_R1 make() {
        try {
            return new PacketManager1_20_R1(
                getMojangClass("world.entity.Entity").getMethod("af"),
                getCBClass("entity.CraftEntity").getMethod("getHandle"),
                getMojangClass("world.entity.Entity").getMethod("getBukkitEntity"),
                getCBClass("CraftWorld").getMethod("getHandle"),
                getMojangClass("server.network.PlayerConnection")
                    .getMethod("a", getMojangClass("network.protocol.Packet")),
                getMojangClass("world.entity.Entity").getMethod("aj"),
                getMojangClass("network.syncher.DataWatcher").getMethod("b"),
                getMojangClass("network.protocol.game.PacketPlayOutEntityMetadata").getConstructor(int.class, List.class),
                getMojangClass("server.level.EntityPlayer").getField("c")
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create version specific server accessor", e);
        }
    }

    @NotNull
    private static Class<?> getMojangClass(@NotNull final String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft." + className);
    }

    @NotNull
    private static Class<?> getCBClass(@NotNull final String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3]
            + "." + className);
    }

    @NotNull
    @Override
    public Object buildEntitySpawnPacket(@NotNull Object entity) {
        return new PacketPlayOutSpawnEntity((EntityLiving) entity);
    }

    @NotNull
    @Override
    public Object buildEntityMetadataPacket(@NotNull Object entity, boolean forceUpdateAll) {
        try {
            final int entityId = (int) this.entityGetIdMethod.invoke(entity);
            final Object synchedEntityData = this.entityGetDataMethod.invoke(entity);
//            return new PacketPlayOutEntityMetadata(entityId, this.synchedEntityDataGetDirtyMethod.invoke(synchedEntityData));
            return this.clientboundSetEntityDataPacketInit.newInstance(entityId, this.synchedEntityDataPackDirtyMethod.invoke(synchedEntityData));
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity metadata packet", e);
        }
    }

    @NotNull
    @Override
    public Object buildEntityDestroyPacket(@NotNull Object entity) {
        try {
            final int entityId = (int) this.entityGetIdMethod.invoke(entity);
            return new PacketPlayOutEntityDestroy(entityId);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity destroy packet", e);
        }
    }

    @NotNull
    @Override
    public Object buildEntityArmorStand(@NotNull Location location, @NotNull String name) {
        try {
            final World world = location.getWorld();
            final WorldServer worldServer = (WorldServer) this.worldGetHandleMethod.invoke(world);

            final Object entityArmorStand = new EntityArmorStand(
                worldServer,
                location.getX(), location.getY(), location.getZ()
            );
            final ArmorStand armorStand = (ArmorStand) this.entityGetBukkitEntityMethod.invoke(entityArmorStand);
            armorStand.setMarker(true);
            armorStand.setInvisible(true);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(name);

            return entityArmorStand;
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create new entity armor stand", e);
        }
    }

    @Override
    public void sendPacket(@NotNull Object packet, @NotNull Player player) {
        try {
            final Object handle = this.entityGetHandleMethod.invoke(player);
            final Object playerConnection = this.entityPlayerPlayerConnectionField.get(handle);
            this.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException(String.format("Failed to send packet to player %s", player.getUniqueId()), e);
        }
    }
}
