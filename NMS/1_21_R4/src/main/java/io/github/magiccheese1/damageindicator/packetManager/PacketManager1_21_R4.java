package io.github.magiccheese1.damageindicator.packetManager;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Implementation of the packet manager for the 1.21.5 minecraft java version.
 * The implementation uses a mixture of direct calls against the re-obfuscated server internals and reflection.
 */
public final class PacketManager1_21_R4 implements PacketManager {

    @NotNull
    @Override
    public Object buildEntitySpawnPacket(@NotNull Object entity) {
        Entity entity1 = (Entity) entity;
        return new ClientboundAddEntityPacket(entity1.getId(), entity1.getUUID(), entity1.getX(), entity1.getY(), entity1.getZ(), entity1.getXRot(), entity1.getYRot(),entity1.getType(),0, Vec3.ZERO, entity1.getYHeadRot());
    }

    @NotNull
    @Override
    public Object buildEntityMetadataPacket(@NotNull Object entity, boolean forceUpdateAll) {
        final Entity entity1 = (Entity) entity;
        final int entityId = entity1.getId();
        final SynchedEntityData dataWatcher = entity1.getEntityData();
        return new ClientboundSetEntityDataPacket(entityId, dataWatcher.packDirty());
    }

    @NotNull
    @Override
    public Object buildEntityDestroyPacket(@NotNull Object entity) {
        final int entityId = ((Entity) entity).getId();
        return new ClientboundRemoveEntitiesPacket(entityId);
    }

    @NotNull
    @Override
    public Object buildEntityArmorStand(@NotNull Location location, @NotNull String name) {
        final World world = location.getWorld();
        final ServerLevel worldServer = ((CraftWorld) world).getHandle();

        final net.minecraft.world.entity.decoration.ArmorStand entityArmorStand = new net.minecraft.world.entity.decoration.ArmorStand(
            worldServer,
            location.getX(), location.getY(), location.getZ()
        );
        final ArmorStand armorStand = (ArmorStand) entityArmorStand.getBukkitEntity();
        armorStand.setMarker(true);
        armorStand.setInvisible(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(name);

        return entityArmorStand;
    }

    @Override
    public void sendPacket(@NotNull Object packet, Collection<Player> players) {
        for (Player player : players) {
            final ServerPlayer handle = ((CraftPlayer) player).getHandle();
            final ServerGamePacketListenerImpl playerConnection = handle.connection;
            playerConnection.send((Packet<?>) packet);
        }
    }
}
