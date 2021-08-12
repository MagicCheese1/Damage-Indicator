package com.github.magiccheese1.damageindicator.versions.v1_16_R3;

import org.bukkit.Bukkit;

public class PacketManagerFactory1_16_R3 {

    public static PacketManager1_16_R3 make() {
        try {
            return new PacketManager1_16_R3(
                getNMSClass("PacketPlayOutSpawnEntityLiving").getConstructor(getNMSClass("EntityLiving")),
                getNMSClass("PacketPlayOutEntityMetadata")
                    .getConstructor(int.class, getNMSClass("DataWatcher"), boolean.class),
                getNMSClass("PacketPlayOutDestoryEntity").getConstructor(int[].class),
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

    private static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3]
            + "." + className);
    }

    private static Class<?> getCBClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3]
            + "." + className);
    }

}
