package com.github.magiccheese1.damageindicator.versions.v1_17_R1;

import org.bukkit.Bukkit;

public class PacketManagerFactory1_17_R1 {

    public static PacketManager1_17_R1 make() {
        try {
            return new PacketManager1_17_R1(
                getMojangClass("world.entity.Entity").getMethod("getId"),
                getMojangClass("world.entity.Entity").getMethod("getDataWatcher"),
                getCBClass("entity.CraftEntity").getMethod("getHandle"),
                getMojangClass("world.entity.Entity").getMethod("getBukkitEntity"),
                getCBClass("CraftWorld").getMethod("getHandle"),
                getMojangClass("server.network.PlayerConnection")
                    .getMethod("sendPacket", getMojangClass("network.protocol.Packet")),
                getMojangClass("server.level.EntityPlayer").getField("b")
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create version specific server accessor", e);
        }
    }

    private static Class<?> getMojangClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft." + className);
    }

    private static Class<?> getCBClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3]
            + "." + className);
    }

}
