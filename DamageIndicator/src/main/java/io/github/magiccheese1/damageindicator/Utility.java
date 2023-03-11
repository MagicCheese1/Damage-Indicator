package io.github.magiccheese1.damageindicator;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;

/**
 * Provides basic utility functionality.
 */
public class Utility {

    /**
     * Determine if the direct hit was a critical hit
     *
     * @param damager - The damaging player
     */
    @SuppressWarnings("deprecation")
    public static boolean isCritical(Player damager) {
        return damager.getAttackCooldown() > 0.9F && damager.getFallDistance() > 0.0F
            && !damager.isOnGround() && !damager.isInWater() && damager.getActivePotionEffects().stream()
            .noneMatch(o -> o.getType().equals(PotionEffectType.BLINDNESS))
            && damager.getVehicle() == null && !damager.isSprinting();
    }

    public static int poisonArrowEffectDuration(PotionData basePotionData) {
        if (basePotionData.isUpgraded()) return 54;
        if (basePotionData.isExtended()) return 225;
        return 112;
    }

    public static int poisonLingeringPotionEffectDuration(PotionData basePotionData) {
        if (basePotionData.isUpgraded()) return 108;
        if (basePotionData.isExtended()) return 450;
        return 225;
    }
}
