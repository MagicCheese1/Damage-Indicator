package io.github.magiccheese1.damageindicator.packetManager;

import com.google.common.base.Preconditions;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Implementation of the packet manager for the 1.16 minecraft java version.
 */
public final class PacketManager1_16_R3 implements PacketManager {
    @NotNull
    @Override
    public Object buildEntitySpawnPacket(@NotNull Object entity) {
        return new PacketPlayOutSpawnEntityLiving((EntityLiving) entity);
    }

    @NotNull
    @Override
    public Object buildEntityMetadataPacket(@NotNull Object entity, boolean forceUpdateAll) {
        final Entity entity1 = (Entity) entity;
        final int entityId = entity1.getId();
        final DataWatcher dataWatcher = entity1.getDataWatcher();
        return new PacketPlayOutEntityMetadata(entityId, dataWatcher, forceUpdateAll);
    }

    @NotNull
    @Override
    @SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
    public Object buildEntityDestroyPacket(@NotNull Object entity) {
        final Entity entity1 = (Entity) entity;
        final int entityId = entity1.getId();
        return new PacketPlayOutEntityDestroy(entityId);
    }

    @NotNull
    @Override
    public Object buildEntityArmorStand(@NotNull Location location, @NotNull String name) {
        final World world = location.getWorld();
        Preconditions.checkArgument(world != null, "provided location did not have a world assigned");

        final WorldServer worldServer = ((CraftWorld) world).getHandle();

        final EntityArmorStand entityArmorStand = new EntityArmorStand(
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
            final EntityPlayer handle = ((CraftPlayer) player).getHandle();
            final PlayerConnection playerConnection = handle.playerConnection;
            playerConnection.sendPacket((Packet<?>) packet);
        }
    }
}
