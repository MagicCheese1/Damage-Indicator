package io.github.magiccheese1.damageindicator;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public interface DamageIndicator {
    /**
     * Function for spawning a DamageIndicator.
     *
     * @param location Where to spawn the Indicator.
     * @param credit   The player that has dealt the damage. (see ShowToDamagerOnly in config)
     * @param format   The format to show the damage in.
     * @param value    The amount of damage that was dealt.
     * @param lifespan How long the Indicator will be visible for. If lifespan is 0, it will not be destroyed
     *                 automatically. NOTICE: The Indicator is not guaranteed to be visible for anyone who rejoined
     *                 the server or was far away when it was spawned. The Indicators are not meant to work like
     *                 holograms.
     *
     * @return the Indicator Object.
     */
    IndicatorEntity spawnIndicator(Location location, @Nullable Player credit, DecimalFormat format,
                                   double value, long lifespan);

    /**
     * Function for spawning a DamageIndicator.
     *
     * @param entity   The entity that is used for finding a valid indicator location.
     * @param credit   The player that has dealt the damage. (see ShowToDamagerOnly in config)
     * @param format   The format to show the damage in.
     * @param value    The amount of damage that was dealt.
     * @param lifespan How long the Indicator will be visible for. If lifespan is 0, it will not be destroyed
     *                 automatically. NOTICE: The Indicator is not guaranteed to be visible for anyone who rejoined
     *                 the server or was far away when it was spawned. The Indicators are not meant to work like
     *                 holograms.
     *
     * @return the Indicator Object.
     */
    IndicatorEntity spawnIndicator(LivingEntity entity, Player credit, DecimalFormat format, double value,
                                   long lifespan);

    /**
     * Function for spawning a DamageIndicator.
     *
     * @param entity The entity that is used for finding a valid indicator location.
     * @param credit The player that has dealt the damage. (see ShowToDamagerOnly in config)
     * @param format The format to show the damage in.
     * @param value  The amount of damage that was dealt.
     *
     * @return the Indicator Object.
     */
    IndicatorEntity spawnIndicator(LivingEntity entity, Player credit, DecimalFormat format, double value);

    /**
     * Function for spawning a DamageIndicator.
     *
     * @param location Where to spawn the Indicator.
     * @param credit   The credit that has dealt the damage. (see ShowToDamagerOnly in config)
     * @param format   The format to show the damage in.
     * @param value    The amount of damage that was dealt.
     *
     * @return the Indicator Object.
     */
    IndicatorEntity spawnIndicator(Location location, Player credit, DecimalFormat format, double value);

    DecimalFormat getDamageFormat();

    DecimalFormat getCriticalDamageFormat();

    DecimalFormat getPoisonDamageFormat();
}
