package io.github.magiccheese1.damageindicator;

import io.github.magiccheese1.damageindicator.packetManager.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Collection;


/**
 * Object for representing Indicators.
 * use {@link DamageIndicator#spawnIndicator(LivingEntity, Player, DecimalFormat, double)} for spawning a new one.
 * IndicatorEntity is not an actual minecraft entity. DamageIndicator uses fake entities that only exist in the clients
 * world.
 */
public class IndicatorEntity {
    private final Plugin plugin;
    private final PacketManager packetManager;
    private final Location location;
    private final double value;
    private final DecimalFormat format;

    private final Collection<Player> visibleTo;
    private Object armorstandEntity;
    private boolean alive = false;

    /**
     * Use {@link #spawn()} to show indicator to players.
     * Creating a IndicatorEntity with
     * {@link DamageIndicator#spawnIndicator(LivingEntity, Player, DecimalFormat, double)} preferred.
     */
    public IndicatorEntity(Plugin plugin, PacketManager packetManager, Location location, double value,
                           DecimalFormat format,
                           Collection<Player> visibleTo) {
        this.plugin = plugin;
        this.packetManager = packetManager;
        this.location = location;
        this.value = value;
        this.format = format;
        this.visibleTo = visibleTo;
    }

    /**
     * Use {@link #spawn()} to show indicator to players.
     * Creating a IndicatorEntity with
     * {@link DamageIndicator#spawnIndicator(LivingEntity, Player, DecimalFormat, double)} preferred.
     */
    public void spawn() {
        armorstandEntity = packetManager.buildEntityArmorStand(getLocation(), getFormat().format(value));

        Object entitySpawnPacket = packetManager.buildEntitySpawnPacket(armorstandEntity);
        Object entityMetadataPacket = packetManager.buildEntityMetadataPacket(armorstandEntity, false);

        packetManager.sendPacket(entitySpawnPacket, visibleTo);
        packetManager.sendPacket(entityMetadataPacket, visibleTo);

        alive = true;
    }

    public double getValue() {
        return value;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public DecimalFormat getFormat() {
        return format;
    }

    public void teleport(Location location) {
        //...
    }


    /**
     * This method schedules the destruction of the Indicator.
     *
     * @param tickDelay The destruction delay in ticks
     *
     * @return the created BukkitTask. use {@link BukkitTask#cancel()} to cancel destruction
     */
    public BukkitTask scheduleDestroy(long tickDelay) {
        return Bukkit.getScheduler().runTaskLater(this.plugin, this::destroy, tickDelay);
    }

    /**
     * This method destroys the Indicator on the client.
     * The Indicator can be spawned again.
     */
    public void destroy() {
        final Object entityDestroyPacket = packetManager.buildEntityDestroyPacket(armorstandEntity);

        packetManager.sendPacket(entityDestroyPacket, visibleTo);

        alive = false;
    }

    public Collection<Player> getVisibleTo() {
        return visibleTo;
    }

    public boolean isAlive() {
        return alive;
    }
}
