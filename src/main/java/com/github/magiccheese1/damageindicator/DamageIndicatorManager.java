package com.github.magiccheese1.damageindicator;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.decoration.EntityArmorStand;

public class DamageIndicatorManager implements Listener {
  FileConfiguration config;
  private JavaPlugin plugin;

  DamageIndicatorManager(JavaPlugin plugin, FileConfiguration config) {
    this.config = config;
    this.plugin = plugin;
  }

  @EventHandler
  public void entityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.isCancelled())
      return;
    // Don't show indicator if the damagee is an armor stand
    if (event.getEntity() instanceof ArmorStand)
      return;

    // Only show indicator if the damager was a player or an arrow
    if (!(event.getDamager() instanceof Player || event.getDamager() instanceof Projectile))
      return;

    Player damager;
    Location spawnLocation;
    Random random = new Random();
    DecimalFormat damageFormat = new DecimalFormat(
        ChatColor.translateAlternateColorCodes('&', config.getString("IndicatorFormat")));

    // Tries random positions until it finds one that is not inside a block
    int tries = 0;
    do {
      tries++;
      spawnLocation = event.getEntity().getLocation().add(random.nextDouble() * (1.0 + 1.0) - 1.0, 1,
          random.nextDouble() * (1.0 + 1.0) - 1.0);
      if (tries > 20) {
        spawnLocation = event.getEntity().getLocation();
        break;
      }
    } while (!spawnLocation.getBlock().isEmpty() && !spawnLocation.getBlock().isLiquid()); // In previous
                                                                                           // versions of
    // this plugin I used
    // .isPassable() but that's
    // not compatible with older
    // versions of Minecraft.

    // Check if the damager is an arrow. If it is use arrow.isCritical().
    // If it isn't use the custom isCritical() for direct damage.
    if (event.getDamager() instanceof AbstractArrow) {
      AbstractArrow arrow = (AbstractArrow) event.getDamager();

      // Don't show indicator if the arrow doesn't belong to a player
      if (!(arrow.getShooter() instanceof Player)) {
        return;
      }

      damager = (Player) arrow.getShooter();

      if (arrow.isCritical())
        damageFormat = new DecimalFormat(
            ChatColor.translateAlternateColorCodes('&', config.getString("CriticalIndicatorFormat")));
    } else {
      if (!(event.getDamager() instanceof Player))
        return;
      damager = (Player) event.getDamager();
      if (Utility.isCritical(damager))
        damageFormat = new DecimalFormat(
            ChatColor.translateAlternateColorCodes('&', config.getString("CriticalIndicatorFormat")));
    }
    // // Destroy the armor stand after 3 sec
    // toBeRemovedArmorstands.add(armorStand);
    // Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
    // armorStand.remove();
    // toBeRemovedArmorstands.remove(armorStand);
    // }, 30);

    // Setup the armorstand and its metadata
    WorldServer worldServer = ((CraftWorld) spawnLocation.getWorld()).getHandle();
    EntityArmorStand armorstand = new EntityArmorStand(worldServer, spawnLocation.getX(), spawnLocation.getY(),
        spawnLocation.getZ());
    armorstand.setMarker(true);
    armorstand.setInvisible(true);
    armorstand.setCustomName(new ChatMessage(String.valueOf(damageFormat.format(event.getFinalDamage()))));
    armorstand.setCustomNameVisible(true);
    // Create entity spawn packet
    var packet = new PacketPlayOutSpawnEntityLiving(armorstand);
    // Create entity Metadata packet
    var packet2 = new PacketPlayOutEntityMetadata(armorstand.getId(), armorstand.getDataWatcher(), true);
    // Send the packets to the player
    ((CraftPlayer) damager).getHandle().b.sendPacket(packet);
    ((CraftPlayer) damager).getHandle().b.sendPacket(packet2);

    new BukkitRunnable() {
      @Override
      public void run() {
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(armorstand.getId());
        ((CraftPlayer) damager).getHandle().b.sendPacket(destroy);
      }
    }.runTaskLater(plugin, 30L);
  }

}