package fr.nessar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerSave {
	String ip;
	boolean isAllowFlight;
	String displayName;
	String name;
	float exhaustion;
	float flyspeed;
	float walkspeed;
	boolean isflying;
	boolean isSneaking;
	boolean isSprinting;
	double health;
	double maxhealth;
	int xp;
	// Collection<PotionEffect> potionEffects;
	int gamemode;
	int food;
	String playeruuid;

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
		// this.potionEffects = p.getActivePotionEffects();
		this.gamemode = p.getGameMode().ordinal();
		this.food = p.getFoodLevel();
		this.playeruuid = p.getUniqueId().toString();
	}

	public boolean isOnline() {
		return Bukkit.getPlayer(this.playeruuid) != null;
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

	// public Collection<PotionEffect> getPotionEffects() {
	// return potionEffects;
	// }

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
