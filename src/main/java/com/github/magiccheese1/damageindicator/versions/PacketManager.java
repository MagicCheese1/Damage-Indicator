package com.github.magiccheese1.damageindicator.versions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface PacketManager {
    Object buildEntitySpawnPacket(Object entity);

    Object buildEntityMetadataPacket(Object entity, boolean a);

    Object buildEntityDestroyPacket(Object entity);

    Object BuildEntityArmorStand(Location location, String name);

    void sendPacket(Object packet, Player player);
}
