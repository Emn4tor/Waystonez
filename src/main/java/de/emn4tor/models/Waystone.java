package de.emn4tor.models;

/*
 *  @author: Emn4tor
 *  @created: 29.03.2025
 */

import org.bukkit.Location;

import java.util.UUID;

public class Waystone {

    private int id;
    private String name;
    private Location location;
    private UUID ownerUUID;

    public Waystone(String name, Location location, UUID ownerUUID) {
        this.name = name;
        this.location = location;
        this.ownerUUID = ownerUUID;
    }

    public Waystone(int id, String name, Location location, UUID ownerUUID) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.ownerUUID = ownerUUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public String getLocationString() {
        return String.format("%s, %d, %d, %d",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }
}

