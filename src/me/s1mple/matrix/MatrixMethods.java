package me.s1mple.matrix;

import com.elmakers.mine.bukkit.api.entity.EntityData;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.utility.CompatibilityLib;
import com.elmakers.mine.bukkit.utility.ConfigurationUtils;
import com.google.gson.stream.JsonReader;
import com.magmaguy.elitemobs.ChatColorConverter;
import com.magmaguy.elitemobs.config.custombosses.CustomBossesConfig;
import com.magmaguy.elitemobs.config.custombosses.CustomBossesConfigFields;
import com.magmaguy.elitemobs.mobconstructor.EliteEntity;
import com.magmaguy.elitemobs.mobconstructor.SuperMobConstructor;
import com.magmaguy.elitemobs.mobconstructor.custombosses.CustomBossEntity;
import com.magmaguy.elitemobs.mobconstructor.mobdata.aggressivemobs.EliteMobProperties;
import com.magmaguy.elitemobs.powers.meta.ElitePower;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;
import org.bukkit.event.entity.CreatureSpawnEvent;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import static me.s1mple.matrix.Matrix.getGson;
public class MatrixMethods {
	static LuckPerms api;
	
	/**
	 * @author CrashCringle
	 * 
	 * @Description This method is a simplified way of removing
	 * Permissions to players via LuckPerms
	 * 
	 * @param player Player who will lose permission
	 * @param permission Permission to remove from the player as a string
	 */
	public static void removePermission(Player player, String permission) {
		api.getUserManager().getUser(player.getUniqueId()).data()
		.remove(Node.builder(permission).build());
		api.getUserManager().saveUser(api.getUserManager().getUser(player.getUniqueId()));

	}
	/**
	 * @author CrashCringle
	 * 
	 * @Description This method is a simplified way of adding 
	 * Permissions to players via LuckPerms
	 * 
	 * @param player Player who will receive permission
	 * @param permission Permission to give to the player as a string
	 */
	public static void addPermission(Player player, String permission) {
		api.getUserManager().getUser(player.getUniqueId()).data()
		.add(Node.builder(permission).build());
		api.getUserManager().saveUser(api.getUserManager().getUser(player.getUniqueId()));

	}
	
	/**
	 * @author CrashCringle
	 * 
	 * @Description Shortened way for sending Console commands
	 * 
	 * @param command The command to send from console as a String
	 */
	public static void ConsoleCmd(String command) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

}
