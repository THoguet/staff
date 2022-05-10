package fr.nessar;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerSave implements Serializable {
	private String ip;
	private boolean isAllowFlight;
	private String displayName;
	private String name;
	private float exhaustion;
	private float flyspeed;
	private float walkspeed;
	private boolean isflying;
	private boolean isSneaking;
	private boolean isSprinting;
	private double health;
	private double maxhealth;
	private int xp;
	private Collection<PotionEffect> potionEffects;
	private int gamemode;
	private int food;
	private String playeruuid;
	private double x;
	private double y;
	private double z;
	private String world;

	public PlayerSave(Player p) {
		this.ip = p.getAddress().toString();
		this.isAllowFlight = p.getAllowFlight();
		this.displayName = p.getDisplayName();
		this.name = p.getName();
		this.exhaustion = p.getExhaustion();
		this.flyspeed = p.getFlySpeed();
		this.walkspeed = p.getWalkSpeed();
		this.isflying = p.isFlying();
		this.isSneaking = p.isSneaking();
		this.isSprinting = p.isSprinting();
		this.health = p.getHealth();
		this.maxhealth = p.getMaxHealth();
		this.xp = p.getTotalExperience();
		this.potionEffects = p.getActivePotionEffects();
		this.gamemode = p.getGameMode().ordinal();
		this.food = p.getFoodLevel();
		this.playeruuid = p.getUniqueId().toString();
		this.x = p.getLocation().getX();
		this.y = p.getLocation().getY();
		this.z = p.getLocation().getZ();
		this.world = p.getLocation().getWorld().getName();
	}

	public String getGamemodeStr() {
		switch (this.gamemode) {
			case 1:
				return "survie";
			case 0:
				return "créatif";
			case 2:
				return "Aventure";
			case 3:
				return "spéctateur";
			default:
				return "Inconnu";
		}
	}

	public boolean isOnline() {
		return Bukkit.getPlayer(UUID.fromString(this.playeruuid)) != null;
	}

	public boolean isAllowFlight() {
		return isAllowFlight;
	}

	public boolean isSneaking() {
		return this.isSneaking;
	}

	public boolean isSprinting() {
		return this.isSprinting;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public String getWorld() {
		return this.world;
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
	}

	public boolean isFlying() {
		return this.isflying;
	}

	public String getDisplayName() {
		return displayName;
	}

	public float getExhaustion() {
		return exhaustion;
	}

	public float getFlyspeed() {
		return flyspeed;
	}

	public int getFood() {
		return food;
	}

	public int getGamemode() {
		return gamemode;
	}

	public double getHealth() {
		return health;
	}

	public String getIp() {
		return ip;
	}

	public double getMaxhealth() {
		return maxhealth;
	}

	public String getName() {
		return name;
	}

	public String getPlayeruuid() {
		return playeruuid;
	}

	public Collection<PotionEffect> getPotionEffects() {
		return potionEffects;
	}

	public float getWalkspeed() {
		return walkspeed;
	}

	public int getXp() {
		return xp;
	}

	public String getUniqueId() {
		return this.playeruuid;
	}
}
