package com.github.magiccheese1.damageindicator;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;

public class InvisibleArmorStand implements Consumer<ArmorStand> {
  Player damager;
  private EntityHider entityHider;
  private Plugin plugin;
  private boolean showToDamagerOnly;

  InvisibleArmorStand(Plugin plugin, Player damager, EntityHider entityHider, boolean showToDamagerOnly) {
    this.plugin = plugin;
    this.damager = damager;
    this.entityHider = entityHider;
    this.showToDamagerOnly = showToDamagerOnly;
  }

  @Override
  public void accept(ArmorStand as) {
    if(showToDamagerOnly) {
    plugin.getServer().getOnlinePlayers().forEach(p -> {
      if (p != damager)
        entityHider.toggleEntity(p, as);
    });
  }
    as.setVisible(false);
    as.setInvulnerable(true);
    as.setSmall(true);
    as.setRemoveWhenFarAway(true);
    as.setGravity(false);
    as.setCollidable(false);
    as.setMarker(true);
  }
}

