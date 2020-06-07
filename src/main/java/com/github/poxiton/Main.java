package com.github.poxiton;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void entityDamage(EntityDamageEvent event) {
    if(event.getEntity().getType() == EntityType.ARMOR_STAND) return;
    Location entityLocation = event.getEntity().getLocation();
    final ArmorStand as = (ArmorStand) entityLocation.getWorld().spawn(entityLocation.add(Math.random(), 1, Math.random()), ArmorStand.class, new invisibleArmorStand());

    as.setCustomName(ChatColor.GRAY + "-" + String.valueOf((int) event.getDamage()));
    as.setCustomNameVisible(true);

    Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
      as.remove();
    }, 20);
    }

}

