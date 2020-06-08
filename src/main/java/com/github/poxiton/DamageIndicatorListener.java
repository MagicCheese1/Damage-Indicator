package com.github.poxiton;

import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class DamageIndicatorListener implements Listener {
  private JavaPlugin plugin;

  DamageIndicatorListener(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void entityDamageByEntity(EntityDamageByEntityEvent event) {
    // Don't show indicator if the entity taking the damage was an armor stand
    if (event.getEntity().getType() == EntityType.ARMOR_STAND)
      return;
    // Only show indicator if the damager was a player or an arrow
    if (!(event.getDamager().getType() == EntityType.PLAYER || event.getDamager().getType() == EntityType.ARROW))
      return;
    Location spawnLocation;
    Random random = new Random();
    do {
      spawnLocation = event.getEntity().getLocation().add(random.nextDouble() * (1.0 + 1.0) - 1.0, 1,
          random.nextDouble() * (1.0 + 1.0) - 1.0);
    } while (!spawnLocation.getBlock().isPassable());
    final ArmorStand as = (ArmorStand) spawnLocation.getWorld().spawn(spawnLocation, ArmorStand.class,
        new invisibleArmorStand());
    ChatColor IndicatorColor = ChatColor.GRAY;
    if (event.getDamager().getType() == EntityType.ARROW) {
      Arrow arrow = (Arrow) event.getDamager();
      if (!(arrow.getShooter() instanceof Player)) {
        return;
      }
      if (arrow.isCritical())
        IndicatorColor = ChatColor.DARK_RED;
    } else {
      Player Damager = (Player) event.getDamager();
      if (isCritical(Damager))
        IndicatorColor = ChatColor.DARK_RED;
    }
    DecimalFormat damageFormat = new DecimalFormat("0.##");
    as.setCustomName(IndicatorColor + "-" + String.valueOf(damageFormat.format(event.getDamage())));
    as.setCustomNameVisible(true);

    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
      as.remove();
    }, 30);
  }

  boolean isCritical(Player damager) {
    return damager.getFallDistance() > 0.0F
        && !damager.isOnGround() && !damager.getLocation().getBlock().isLiquid() && !damager.getActivePotionEffects()
            .stream().filter(o -> o.getType().equals(PotionEffectType.BLINDNESS)).findFirst().isPresent()
        && damager.getVehicle() == null && !damager.isSprinting();
  }
}