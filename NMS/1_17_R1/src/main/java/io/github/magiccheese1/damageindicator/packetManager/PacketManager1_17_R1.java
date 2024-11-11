package io.github.magiccheese1.damageindicator.packetManager;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Implementation of the packet manager for the 1.17 minecraft java version.
 * The implementation uses a mixture of direct calls against the re-obfuscated server internals and reflection.
 */
public final class PacketManager1_17_R1 implements PacketManager {
    @NotNull
    @Override
    public Object buildEntitySpawnPacket(@NotNull Object entity) {
        return new ClientboundAddMobPacket((LivingEntity) entity);
    }

    @NotNull
    @Override
    public Object buildEntityMetadataPacket(@NotNull Object entity, boolean forceUpdateAll) {
        final Entity entity1 = (Entity) entity;
        final int entityId = entity1.getId();
        final SynchedEntityData dataWatcher = entity1.getEntityData();
        return new ClientboundSetEntityDataPacket(entityId, dataWatcher, forceUpdateAll);
    }

    @NotNull
    @Override
    public Object buildEntityDestroyPacket(@NotNull Object entity) {
        final Entity entity1 = (Entity) entity;
        final int entityId = entity1.getId();
        return new ClientboundRemoveEntitiesPacket(entityId);
    }

    @NotNull
    @Override
    public Object buildEntityArmorStand(@NotNull Location location, @NotNull String name) {
        final World world = location.getWorld();
        final ServerLevel worldServer = ((CraftWorld) world).getHandle();

        final net.minecraft.world.entity.decoration.ArmorStand entityArmorStand =
            new net.minecraft.world.entity.decoration.ArmorStand(
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
