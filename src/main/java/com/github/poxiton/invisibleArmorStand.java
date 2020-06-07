package com.github.poxiton;

import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Consumer;

public class invisibleArmorStand implements Consumer<ArmorStand> {
  @Override
  public void accept (ArmorStand as) {
    as.setVisible(false);
    as.setInvulnerable(true);
    as.setSmall(true);
    as.setRemoveWhenFarAway(true);
    as.setGravity(false);
    as.setCollidable(false);
    as.setMarker(true);
  }
}