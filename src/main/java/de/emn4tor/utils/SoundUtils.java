package de.emn4tor.utils;

/*
 *  @author: Emn4tor
 *  @created: 29.03.2025
 */


import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class SoundUtils {

    public static void playWaystoneCreateSound(Location location) {
        location.getWorld().playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 0.5f);
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
    }

    public static void spawnWaystoneCreateParticles(Location location) {
        Location particleLocation = location.clone().add(0.5, 0.5, 0.5);
        location.getWorld().spawnParticle(Particle.ENCHANT, particleLocation, 50, 0.5, 0.5, 0.5, 0.5);
        location.getWorld().spawnParticle(Particle.END_ROD, particleLocation, 20, 0.5, 0.5, 0.5, 0.1);
    }

    public static void playWaystoneDiscoverSound(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
    }

    public static void playWaystoneTeleportSound(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
        location.getWorld().playSound(location, Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 1.5f);
    }

    public static void spawnWaystoneTeleportParticles(Location location) {
        Location particleLocation = location.clone().add(0, 1, 0);
        location.getWorld().spawnParticle(Particle.PORTAL, particleLocation, 50, 0.5, 1, 0.5, 0.1);
        location.getWorld().spawnParticle(Particle.REVERSE_PORTAL, particleLocation, 20, 0.5, 1, 0.5, 0.05);
    }
}