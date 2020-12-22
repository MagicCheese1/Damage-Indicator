package com.github.MagicCheese1;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Utility {

  /**
   * Determine if the direct hit was a critical hit
   * 
   * @param damager - The damaging player
   */
  static boolean isCritical(Player damager) {
    return damager.getFallDistance() > 0.0F
        && !damager.getLocation().getBlock().isLiquid() && !damager.getActivePotionEffects().stream()
            .filter(o -> o.getType().equals(PotionEffectType.BLINDNESS)).findFirst().isPresent()
        && damager.getVehicle() == null && !damager.isSprinting();
  }
}
