package com.github.magiccheese1.damageindicator.versions;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Abstraction interface representing any form of interaction with the server internal implementation.
 * As the server implementation may differ between minecraft releases, this interface is implemented for each supported
 * minecraft version.
 */
public interface PacketManager {

    /**
     * Creates a new entity spawn packet for a virtual entity instance.
     * The packet, when send to the client, causes the client to display the entity instance at the respective location.
     * The spawn packet, by definition, does not include any meta data about the entity such as the custom name.
     *
     * @param entity the entity instance for which the spawn packet is to be constructed. This instance should be an
     *               instance of the server internal entity class, instead of the spigot/bukkit classes. It should hence
     *               most likely be constructed through {@link #buildEntityArmorStand(Location, String)}.
     *
     * @return the created packet instance. This packet may be send to a player using {@link
     *     #sendPacket(Object, Player)}.
     */
    @NotNull
    Object buildEntitySpawnPacket(@NotNull Object entity);

    /**
     * Creates a new entity metadata packet for a virtual entity instance.
     * The packet, when send to the client, causes the client to update its local data watcher for the entity, updating
     * data like the custom name or the pose.
     *
     * @param entity         the entity instance for which the metadata packet is to be constructed. This instance
     *                       should be an instance of the server internal entity class, instead of the spigot/bukkit
     *                       classes. It should hence most likely be constructed through {@link
     *                       #buildEntityArmorStand(Location, String)}.
     * @param forceUpdateAll whether or not the entire data watcher should be published to the client. If forceUpdateAll
     *                       is {@link Boolean#FALSE} only flags marked as dirty will be published to the client.
     *
     * @return the created packet instance. This packet may be send to a player using
     *     {@link #sendPacket(Object, Player)}.
     */
    @NotNull
    Object buildEntityMetadataPacket(@NotNull Object entity, boolean forceUpdateAll);

    /**
     * Creates a new entity destroy packet for a virtual entity instance.
     * The packet, when send to the client, causes the client to no longer display the entity for the user.
     *
     * @param entity the entity instance for which the destroy packet is to be constructed. This instance should be an
     *               instance of the server internal entity class, instead of the spigot/bukkit classes.
     *               It should hence most likely be constructed through {@link #buildEntityArmorStand(Location,
     *               String)}.
     *
     * @return the created packet instance. This packet may be send to a player using
     *     {@link #sendPacket(Object, Player)}.
     */
    @NotNull
    Object buildEntityDestroyPacket(@NotNull Object entity);

    /**
     * Creates a new instance of the server internal {@link ArmorStand} representation at any given location.
     * Besides defining the location, this method will also give the armor stand a specific custom name which is also
     * made visible.
     * The armor stand additionally is made invisible and updated to a marker, effectively setting removing its hitbox.
     *
     * @param location the location at which the armor stand should be spawned.
     * @param name     the custom display name of the armor stand. This display name is passed in the bukkit legacy
     *                 format, using § denoted colour codes.
     *
     * @return the created entity armor stand instance. This instance is not actually added to any world but will have a
     *     unique entity id for client side tracking.
     *
     * @throws IllegalArgumentException if the provided location did not have a world assigned to it.
     */
    @NotNull
    Object buildEntityArmorStand(@NotNull Location location, @NotNull String name) throws IllegalArgumentException;

    /**
     * Queues a new packet in the packet queue of a player.
     * This method will not flush the packet queue, the passed packet will hence be published to the player in the next
     * full server tick.
     *
     * @param packet the server internal representation of the packet that should be send to the player.
     * @param player the player instance that is going to receive the packet.
     */
    void sendPacket(@NotNull Object packet, @NotNull Player player);
}
