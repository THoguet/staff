package fr.nessar;

import org.bukkit.Material;

public class Template {
	private Material item;
	private String name;
	private String message;
	private long duration;
	private PunishType pType;

	public Template(Material item, String name, String message, PunishType pType, long duration) {
		this.item = item;
		this.name = name;
		this.message = message;
		this.duration = duration;
		this.pType = pType;
	}

	public Template(String item, String name, String message, int pType, long duration) {
		new Template(Material.getMaterial(item), name, message, PunishType.valueOf(pType), duration);
	}

	public Material getItem() {
		return item;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	public long getDuration() {
		return duration;
	}

	public PunishType getpType() {
		return pType;
	}

}
