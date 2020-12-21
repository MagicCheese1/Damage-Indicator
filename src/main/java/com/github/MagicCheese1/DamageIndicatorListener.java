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
import org.bukkit.potion.PotionEffectType;

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
    // Don't show indicator if the entity taking the damage was an armor stand
    if (event.getEntity().getType() == EntityType.ARMOR_STAND)
      return;
    // Only show indicator if the damager was a player or an arrow
    if (!(event.getDamager().getType() == EntityType.PLAYER || event.getDamager().getType() == EntityType.ARROW))
      return;
    Player damager;
    Location spawnLocation;
    Random random = new Random();
    ChatColor IndicatorColor = ChatColor.getByChar(Config.getString("HitColor"));

    do {
      spawnLocation = event.getEntity().getLocation().add(random.nextDouble() * (1.0 + 1.0) - 1.0, 1,
          random.nextDouble() * (1.0 + 1.0) - 1.0);
    } while (!spawnLocation.getBlock().isPassable());

    if (event.getDamager().getType() == EntityType.ARROW) {
      Arrow arrow = (Arrow) event.getDamager();
      if (!(arrow.getShooter() instanceof Player)) {
        return;
      }
      damager = (Player) arrow.getShooter();
      if (arrow.isCritical())
        IndicatorColor = ChatColor.getByChar(Config.getString("CriticalHitColor"));
    } else {
      damager = (Player) event.getDamager();
      if (isCritical(damager))
        IndicatorColor = ChatColor.getByChar(Config.getString("CriticalHitColor"));
    }
    final ArmorStand as = (ArmorStand) spawnLocation.getWorld().spawn(spawnLocation, ArmorStand.class,
        new InvisibleArmorStand(Plugin, damager, EntityHider, Config.getBoolean("ShowToDamagerOnly")));
    DecimalFormat damageFormat = new DecimalFormat(Config.getString("IndicatorFormat"));
    as.setCustomName(IndicatorColor + "-" + String.valueOf(damageFormat.format(event.getFinalDamage())));
    as.setCustomNameVisible(true);

    Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin, () -> {
      as.remove();
    }, 30);
  }

  boolean isCritical(Player damager) {
    return damager.getFallDistance() > 0.0F
        && !damager.getLocation().getBlock().isLiquid() && !damager.getActivePotionEffects().stream()
            .filter(o -> o.getType().equals(PotionEffectType.BLINDNESS)).findFirst().isPresent()
        && damager.getVehicle() == null && !damager.isSprinting();
  }
}