package com.github.magiccheese1.damageindicator.versions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface PacketManager {
    public Object buildEntitySpawnPacket(Object entity);

    public Object buildEntityMetadataPacket(Object entity, boolean a);

    public Object buildEntityDestroyPacket(Object entity);

    public Object BuildEntityArmorStand(Location location, String name);

    public void sendPacket(Object packet, Player player);
}
