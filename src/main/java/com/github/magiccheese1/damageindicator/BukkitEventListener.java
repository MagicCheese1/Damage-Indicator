package com.github.magiccheese1.damageindicator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.magiccheese1.damageindicator.versions.PacketManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitEventListener implements Listener {
  FileConfiguration config;
  private JavaPlugin plugin;
  private PacketManager packetManager;

  BukkitEventListener(JavaPlugin plugin, FileConfiguration config, PacketManager packetManager) {
    this.config = config;
    this.plugin = plugin;
    this.packetManager = packetManager;
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
    try {
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
    } catch (NoClassDefFoundError e) {
      if (event.getDamager() instanceof Arrow) {
        Arrow arrow = (Arrow) event.getDamager();

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
    }
    // figure out who should see the indicator
    List<Player> packetRecipients = new ArrayList<Player>();
    packetRecipients.add(damager);
    if (!config.getBoolean("ShowToDamagerOnly")) {
      for (Entity nearbyEntity : damager.getNearbyEntities(16, 16, 16)) {
        if (nearbyEntity instanceof Player nearbyPlayer)
          packetRecipients.add(nearbyPlayer);
      }
    }
    Object indicatorEntity = packetManager.BuildEntityArmorStand(spawnLocation,
        String.valueOf(damageFormat.format(event.getFinalDamage())));

    Object entitySpawnPacket = packetManager.buildEntitySpawnPacket(indicatorEntity);
    Object entityMetadataPacket = packetManager.buildEntityMetadataPacket(indicatorEntity, true);

    packetManager.sendPacket(entitySpawnPacket, damager);
    packetManager.sendPacket(entityMetadataPacket, damager);

    new BukkitRunnable() {
      @Override
      public void run() {
        Object entityDestroyPacket = packetManager.buildEntityDestroyPacket(indicatorEntity);

        for (Player recipient : packetRecipients) {
          packetManager.sendPacket(entityDestroyPacket, recipient);
        }
      }
    }.runTaskLater(plugin, 30L);
  }
}
