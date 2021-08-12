package com.github.magiccheese1.damageindicator.versions;

import com.github.magiccheese1.damageindicator.exceptions.NMSAccessException;
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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of the packet manager for the 1.17 minecraft java version.
 * The implementation uses a mixture of direct calls against the re-obfuscated server internals and reflection.
 */
public final class PacketManager1_17_R1 implements PacketManager {

    @NotNull
    public Object buildEntitySpawnPacket(@NotNull Object entity) {
        return new PacketPlayOutSpawnEntityLiving((EntityLiving) entity);
    }

    @NotNull
    public Object buildEntityMetadataPacket(@NotNull Object entity, boolean forceUpdateAll) {
        try {
            final int entityId = (int) entity.getClass().getMethod("getId").invoke(entity);
            final Object dataWatcher = entity.getClass().getMethod("getDataWatcher").invoke(entity);
            return new PacketPlayOutEntityMetadata(entityId, (DataWatcher) dataWatcher, forceUpdateAll);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity metadata packet", e);
        }
    }

    @NotNull
    public Object buildEntityDestroyPacket(@NotNull Object entity) {
        try {
            final int entityId = (int) entity.getClass().getMethod("getId").invoke(entity);
            return new PacketPlayOutEntityDestroy(entityId);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity destroy packet", e);
        }
    }

    @NotNull
    public Object buildEntityArmorStand(@NotNull Location location, @NotNull String name) {
        try {
            final World world = location.getWorld();
            final WorldServer worldServer = (WorldServer) world.getClass().getMethod("getHandle").invoke(world);

            final Object entityArmorStand = new EntityArmorStand(
                worldServer,
                location.getX(), location.getY(), location.getZ()
            );
            entityArmorStand.getClass().getMethod("setMarker", boolean.class).invoke(entityArmorStand, true);
            entityArmorStand.getClass().getMethod("setInvisible", boolean.class).invoke(entityArmorStand, true);
            entityArmorStand.getClass().getMethod("setCustomNameVisible", boolean.class).invoke(entityArmorStand, true);
            entityArmorStand.getClass().getMethod("setCustomName", IChatBaseComponent.class).invoke(
                entityArmorStand,
                new ChatMessage(name)
            );
            return entityArmorStand;
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create new entity armor stand", e);
        }
    }

    public void sendPacket(@NotNull Object packet, @NotNull Player player) {
        try {
            final Object handle = player.getClass().getMethod("getHandle").invoke(player);
            final Object playerConnection = handle.getClass().getField("b").get(handle);
            playerConnection.getClass().getMethod("sendPacket", Packet.class).invoke(playerConnection, packet);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException(String.format("Failed to send packet to player %s", player.getUniqueId()), e);
        }
    }
}
