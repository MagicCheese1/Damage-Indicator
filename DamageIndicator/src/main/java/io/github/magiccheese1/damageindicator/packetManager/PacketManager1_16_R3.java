package io.github.magiccheese1.damageindicator.packetManager;

import com.google.common.base.Preconditions;
import io.github.magiccheese1.damageindicator.exceptions.NMSAccessException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Implementation of the packet manager for the 1.16 minecraft java version.
 * The implementation relies purely on reflective access to the server internals.
 */
public final class PacketManager1_16_R3 implements PacketManager {

    private final Constructor<?> packetPlayOutSpawnEntityLivingConstructor;
    private final Constructor<?> packetPlayOutEntityMetadataConstructor;
    private final Constructor<?> packetPlayOutDestroyEntityConstructor;
    private final Constructor<?> entityArmorStandConstructor;

    private final Method entityGetIdMethod;
    private final Method entityGetDataWatcherMethod;
    private final Method entityGetHandleMethod;
    private final Method entityGetBukkitEntityMethod;
    private final Method worldGetHandleMethod;
    private final Method playerConnectionSendPacketMethod;

    private final Field entityPlayerPlayerConnectionField;

    public PacketManager1_16_R3(final @NotNull Constructor<?> packetPlayOutSpawnEntityLivingConstructor,
                                final @NotNull Constructor<?> packetPlayOutEntityMetadataConstructor,
                                final @NotNull Constructor<?> packetPlayOutDestroyEntityConstructor,
                                final @NotNull Constructor<?> entityArmorStandConstructor,
                                final @NotNull Method entityGetIdMethod,
                                final @NotNull Method entityGetDataWatcherMethod,
                                final @NotNull Method entityGetHandleMethod,
                                final @NotNull Method entityGetBukkitEntityMethod,
                                final @NotNull Method worldGetHandleMethod,
                                final @NotNull Method playerConnectionSendPacketMethod,
                                final @NotNull Field entityPlayerPlayerConnectionField) {
        this.packetPlayOutSpawnEntityLivingConstructor = packetPlayOutSpawnEntityLivingConstructor;
        this.packetPlayOutEntityMetadataConstructor = packetPlayOutEntityMetadataConstructor;
        this.packetPlayOutDestroyEntityConstructor = packetPlayOutDestroyEntityConstructor;
        this.entityArmorStandConstructor = entityArmorStandConstructor;
        this.entityGetIdMethod = entityGetIdMethod;
        this.entityGetDataWatcherMethod = entityGetDataWatcherMethod;
        this.entityGetHandleMethod = entityGetHandleMethod;
        this.entityGetBukkitEntityMethod = entityGetBukkitEntityMethod;
        this.worldGetHandleMethod = worldGetHandleMethod;
        this.playerConnectionSendPacketMethod = playerConnectionSendPacketMethod;
        this.entityPlayerPlayerConnectionField = entityPlayerPlayerConnectionField;
    }

    @NotNull
    public static PacketManager1_16_R3 make() {
        try {
            return new PacketManager1_16_R3(
                getNMSClass("PacketPlayOutSpawnEntityLiving").getConstructor(getNMSClass("EntityLiving")),
                getNMSClass("PacketPlayOutEntityMetadata")
                    .getConstructor(int.class, getNMSClass("DataWatcher"), boolean.class),
                getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int[].class),
                getNMSClass("EntityArmorStand")
                    .getConstructor(getNMSClass("World"), double.class, double.class, double.class),

                getNMSClass("Entity").getMethod("getId"),
                getNMSClass("Entity").getMethod("getDataWatcher"),
                getCBClass("entity.CraftEntity").getMethod("getHandle"),
                getNMSClass("Entity").getMethod("getBukkitEntity"),
                getCBClass("CraftWorld").getMethod("getHandle"),
                getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet")),

                getNMSClass("EntityPlayer").getField("playerConnection")
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create version specific server accessor", e);
        }
    }

    @NotNull
    private static Class<?> getNMSClass(@NotNull final String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3]
            + "." + className);
    }

    @NotNull
    private static Class<?> getCBClass(@NotNull final String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3]
            + "." + className);
    }

    @NotNull
    @Override
    public Object buildEntitySpawnPacket(@NotNull Object entity) {
        try {
            return this.packetPlayOutSpawnEntityLivingConstructor.newInstance(entity);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity spawn packet", e);
        }
    }

    @NotNull
    @Override
    public Object buildEntityMetadataPacket(@NotNull Object entity, boolean forceUpdateAll) {
        try {
            final int entityId = (int) this.entityGetIdMethod.invoke(entity);
            final Object dataWatcher = this.entityGetDataWatcherMethod.invoke(entity);
            return this.packetPlayOutEntityMetadataConstructor.newInstance(entityId, dataWatcher, forceUpdateAll);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity metadata packet", e);
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
    public Object buildEntityDestroyPacket(@NotNull Object entity) {
        try {
            final int entityId = (int) this.entityGetIdMethod.invoke(entity);
            return this.packetPlayOutDestroyEntityConstructor.newInstance(new int[]{entityId});
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity destroy packet", e);
        }
    }

    @NotNull
    @Override
    public Object buildEntityArmorStand(@NotNull Location location, @NotNull String name) {
        try {
            final World world = location.getWorld();
            Preconditions.checkArgument(world != null, "provided location did not have a world assigned");

            final Object worldServer = this.worldGetHandleMethod.invoke(world);

            final Object entityArmorStand = this.entityArmorStandConstructor.newInstance(
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
            throw new NMSAccessException("Failed to create entity armor stand", e);
        }
    }

    @Override
    public void sendPacket(@NotNull Object packet, @NotNull Player player) {
        try {
            final Object handle = this.entityGetHandleMethod.invoke(player);
            final Object playerConnection = this.entityPlayerPlayerConnectionField.get(handle);
            this.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException(
                String.format("Failed to queue packet for player %s", player.getUniqueId()),
                e
            );
        }
    }

    @Override
    public void sendPacket(@NotNull Object packet, Collection<Player> players) {
        for (Player player : players)
            sendPacket(packet, player);
    }
}
