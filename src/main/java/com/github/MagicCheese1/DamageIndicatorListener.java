package com.github.MagicCheese1;

import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageIndicatorListener implements Listener {
  private JavaPlugin Plugin;
  private EntityHider EntityHider;
  FileConfiguration Config;

  DamageIndicatorListener(JavaPlugin plugin, EntityHider entityHider, FileConfiguration config) {
    this.Plugin = plugin;
    this.EntityHider = entityHider;
    this.Config = config;
  }

  @EventHandler
  public void entityDamageByEntity(EntityDamageByEntityEvent event) {
    // Don't show indicator if the damagee is an armor stand
    if (event.getEntity().getType() == EntityType.ARMOR_STAND)
      return;

    // Only show indicator if the damager was a player or an arrow
    if (!(event.getDamager().getType() == EntityType.PLAYER || event.getDamager().getType() == EntityType.ARROW))
      return;

    Player damager;
    Location spawnLocation;
    Random random = new Random();
    ChatColor IndicatorColor = ChatColor.getByChar(Config.getString("HitColor"));

    // ! Very unsafe! Very don't Care! :P
    // Tries random positions until it finds one that is not inside a block
    do {
      spawnLocation = event.getEntity().getLocation().add(random.nextDouble() * (1.0 + 1.0) - 1.0, 1,
          random.nextDouble() * (1.0 + 1.0) - 1.0);
    } while (!spawnLocation.getBlock().isPassable());

    // Check if the damager is an arrow. If it is use arrow.isCritical().
    // If it isn't use the custom isCritical() for direct damage.
    if (event.getDamager().getType() == EntityType.ARROW) {
      Arrow arrow = (Arrow) event.getDamager();

      // Don't show indicator if the arrow doesn't belong to a player
      if (!(arrow.getShooter() instanceof Player)) {
        return;
      }

      damager = (Player) arrow.getShooter();

      if (arrow.isCritical())
        IndicatorColor = ChatColor.getByChar(Config.getString("CriticalHitColor"));
    } else {
      damager = (Player) event.getDamager();
      if (Utility.isCritical(damager))
        IndicatorColor = ChatColor.getByChar(Config.getString("CriticalHitColor"));
    }

    // Spawn an invisible armor stand
    final ArmorStand armorStand = (ArmorStand) spawnLocation.getWorld().spawn(spawnLocation, ArmorStand.class,
        new InvisibleArmorStand(Plugin, damager, EntityHider, Config.getBoolean("ShowToDamagerOnly")));

    // Set formating
    DecimalFormat damageFormat = new DecimalFormat(Config.getString("IndicatorFormat"));

    // Set visible name
    armorStand.setCustomName(IndicatorColor + "-" + String.valueOf(damageFormat.format(event.getFinalDamage())));
    armorStand.setCustomNameVisible(true);

    // Destroy the armor stand after 3 sec
    Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin, () -> {
      armorStand.remove();
    }, 30);
  }
}