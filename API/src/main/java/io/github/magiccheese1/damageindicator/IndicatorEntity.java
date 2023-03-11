package io.github.magiccheese1.damageindicator;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Collection;

/**
 * Use {@link DamageIndicator#spawnIndicator(LivingEntity, Player, DecimalFormat, double)} to spawn an Indicator
 */
public interface IndicatorEntity {

    /**
     * Shows the Indicator to players. Can be used to respawn Indicator after it being destroyed.
     */
    void spawn();

    double getValue();

    @NotNull Location getLocation();

    @NotNull DecimalFormat getFormat();

    /**
     * This method schedules the destruction of the Indicator.
     *
     * @param tickDelay The destruction delay in ticks
     *
     * @return the created BukkitTask. use {@link BukkitTask#cancel()} to cancel destruction
     */
    BukkitTask scheduleDestroy(long tickDelay);

    /**
     * This method destroys the Indicator on the client.
     * The Indicator can be spawned again.
     */
    void destroy();

    Collection<Player> getVisibleTo();

    boolean isAlive();
}
