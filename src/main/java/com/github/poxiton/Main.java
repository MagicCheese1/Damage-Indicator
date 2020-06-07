package com.github.poxiton;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;
import org.bukkit.*;

public class Main extends JavaPlugin implements Listener {

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    event.setJoinMessage(null);
    player.sendMessage(ChatColor.GOLD + "Witaj na serwerze " + player.getDisplayName());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    event.setQuitMessage(null);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (command.getName().equals("info")) {
      if(sender instanceof Player) {
        Collection<? extends Player> playersOnline = getServer().getOnlinePlayers();
        sender.sendMessage(ChatColor.GOLD + "Graczy online na serwerze: " + ChatColor.GREEN + playersOnline.size());
      } else {
        System.out.println("Musisz byc graczem");
      }
    }

    return false;
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

