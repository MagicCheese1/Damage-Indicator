package io.github.magiccheese1.damageindicator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Objects;

public interface DamageIndicator {

    /**
     * Provides the shared singleton instance of the damage indicator interface.
     *
     * @return the instance.
     */
    @NotNull
    static DamageIndicator instance() {
        return Objects.requireNonNull(
            Bukkit.getServicesManager().load(DamageIndicator.class),
            "Failed to load DamageIndicator implementation"
        );
    }

    /**
     * Function for spawning a DamageIndicator.
     *
     * @param location where to spawn the Indicator.
     * @param credit   the player that has dealt the damage. (see ShowToDamagerOnly in config)
     * @param format   the format to show the damage in.
     * @param value    the amount of damage that was dealt.
     * @param lifespan how long the Indicator will be visible for. If lifespan is 0, it will not be destroyed
     *                 automatically. NOTICE: The Indicator is not guaranteed to be visible for anyone who rejoined
     *                 the server or was far away when it was spawned. The Indicators are not meant to work like
     *                 holograms.
     *
     * @return the indicator object.
     */
    @NotNull
    IndicatorEntity spawnIndicator(@NotNull Location location,
                                   @Nullable Player credit,
                                   @NotNull DecimalFormat format,
                                   double value,
                                   long lifespan);

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
     * @return the indicator object.
     */
    @NotNull
    IndicatorEntity spawnIndicator(@NotNull LivingEntity entity,
                                   @Nullable Player credit,
                                   @NotNull DecimalFormat format,
                                   double value,
                                   long lifespan);

    /**
     * Function for spawning a DamageIndicator.
     *
     * @param entity The entity that is used for finding a valid indicator location.
     * @param credit The player that has dealt the damage. (see ShowToDamagerOnly in config)
     * @param format The format to show the damage in.
     * @param value  The amount of damage that was dealt.
     *
     * @return the indicator object.
     */
    @NotNull
    IndicatorEntity spawnIndicator(@NotNull LivingEntity entity,
                                   @Nullable Player credit,
                                   @NotNull DecimalFormat format,
                                   double value);

    /**
     * Function for spawning a DamageIndicator.
     *
     * @param location Where to spawn the Indicator.
     * @param credit   The credit that has dealt the damage. (see ShowToDamagerOnly in config)
     * @param format   The format to show the damage in.
     * @param value    The amount of damage that was dealt.
     *
     * @return the indicator object.
     */
    @NotNull
    IndicatorEntity spawnIndicator(@NotNull Location location,
                                   @Nullable Player credit,
                                   @NotNull DecimalFormat format,
                                   double value);

    DecimalFormat getDamageFormat();

    DecimalFormat getCriticalDamageFormat();

    DecimalFormat getPoisonDamageFormat();
}
