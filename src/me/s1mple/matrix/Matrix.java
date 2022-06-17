package me.s1mple.matrix;

import com.clanjhoo.vampire.VampireRevamp;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.google.gson.Gson;
import me.libraryaddict.disguise.LibsDisguises;
import me.s1mple.matrix.Listener.PermsListener;
import me.s1mple.matrix.Listener.SkillsListener;
import me.s1mple.matrix.Listener.VPNListener;
import me.s1mple.matrix.Protocols.WrapperPlayServerGameStateChange;
import me.s1mple.matrix.Raid.RaidListener;
import me.s1mple.matrix.Skills.Werewolf;
import me.s1mple.matrix.Util.Glow;
import me.s1mple.matrix.Util.Util;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.skills.main.SkillsPro;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;


import me.s1mple.matrix.Listener.DreamListener;


public class Matrix extends JavaPlugin {
	public static Matrix instance;
	public static ConfigManager configManager;
	public static String version = "1.10";
	public static String author = "S1mpleCrash";
	private SkillsPro skillsapi;
	private VampireRevamp revamp;
	private LibsDisguises disguise;
	private LuckPerms api;

	protected static Gson gson;

	public static MagicAPI magicAPI;

	private ProtocolManager protocolManager;
	@Override
	public void onEnable() {

		instance = this;
		configManager = new ConfigManager(this);

		this.skillsapi = ((SkillsPro) instance.getServer().getPluginManager().getPlugin("SkillsPro"));
		this.revamp = ((VampireRevamp) instance.getServer().getPluginManager().getPlugin("VampireRevamp"));
		this.api = LuckPermsProvider.get();
		protocolManager = ProtocolLibrary.getProtocolManager();
		registerGlow();
		new Werewolf();

		//ArenaManager.init(this);
		// BattlePass.init(this);
		//TournamentHandler.init(this);
		instance.getServer().getPluginManager().registerEvents(new DreamListener(), Matrix.instance);
		instance.getServer().getPluginManager().registerEvents(new SkillsListener(), Matrix.instance);
		instance.getServer().getPluginManager().registerEvents(new VPNListener(), Matrix.instance);
		instance.getServer().getPluginManager().registerEvents(new PermsListener(), Matrix.instance);
		instance.getServer().getPluginManager().registerEvents(new RaidListener(), Matrix.instance);
	}

	@Override
	public void onDisable() {
		getLogger().info("MatrixPlugin is stopping...");
		this.instance.saveConfig();
	}

	public void displayCredits(Player p) {
		WrapperPlayServerGameStateChange gamestateChange = new WrapperPlayServerGameStateChange();
		gamestateChange.setReason(4);
		gamestateChange.setValue(1);
		gamestateChange.sendPacket(p);
	}

	/**
	 * Add glow effect
	 */
	public void registerGlow() {
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Glow glow = new Glow(new NamespacedKey(Matrix.instance, "glow_ench"));
			Enchantment.registerEnchantment(glow);
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String helpMessage = ChatColor.AQUA + "/matrix vpn add: " + ChatColor.BLUE + "Creates vpn key\n";
		String staffHelpMessage = ChatColor.AQUA + "/matrix vpn getip <name>: " + ChatColor.BLUE
				+ "Returns the real ip of the player.\n" + ChatColor.AQUA + "/matrix premium <name>: " + ChatColor.BLUE
				+ "Makes player's battlepass premium." + ChatColor.AQUA + "/matrix startRaid: " + ChatColor.BLUE
				+ "Toggles whether the next raid is a Pirate Raid";
		// VPN perm: matrix.vpn
		// Staff perm: matrix.staff

		if (cmd.getName().equalsIgnoreCase("matrix")) {
			if (args.length == 0) {
				if (sender.hasPermission("matrix.staff")) {
					sender.sendMessage(staffHelpMessage);
				}
				// sender.sendMessage(helpMessage);
				return true;
			} else if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("help")) {
					// sender.sendMessage(helpMessage);
					if (sender.hasPermission("matrix.staff")) {
						sender.sendMessage(staffHelpMessage);
					}
					return true;
				} else if (args[0].equalsIgnoreCase("commandspy") || (args[0].equalsIgnoreCase("cmdspy"))) {
					if (sender.hasPermission("matrix.commandspy") && sender instanceof Player) {
						if (PermsListener.playerList.contains(sender)) {
							PermsListener.playerList.remove(sender);
							sender.sendMessage(Util.color("&4Matrix Network &7>> &cCommand spy &4disabled&c!"));
						} else {
							PermsListener.playerList.add((Player) sender);
							sender.sendMessage(Util.color("&4Matrix Network &7>> &cCommand spy &4enabled&c!"));
						}
						return true;
					}
				} else if (args[0].equalsIgnoreCase("startRaid")) {
					if (sender.hasPermission("matrix.commandspy") && sender instanceof Player) {
						if (RaidListener.forceStart) {
							RaidListener.forceStart = false;
							sender.sendMessage(
									Util.color("&4Matrix Network &7>> &cNext Raid will not be a Pirate Raid"));
						} else {
							RaidListener.forceStart = true;
							sender.sendMessage(Util.color("&4Matrix Network &7>> &cNext Raid will be a Pirate Raid"));
						}
						return true;
					}
				} else if (args[0].equalsIgnoreCase("forceCredits")) {
					if (sender instanceof Player && args.length >= 2
							&& sender.hasPermission("matrix.forceCredits")) {
						displayCredits((Player) Bukkit.getPlayer(args[1]));
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "Must be a player.");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("vpn")) {
					if (args.length == 3) {
						if (sender.hasPermission("matrix.vpn")) {
							if (args[1].equalsIgnoreCase("getip")) {
								sender.sendMessage(getIpOfVpnPlayer(args[2]));
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Not enough permissions!");
							return false;
						}
					} else if (args.length == 2) {
						if (args[1].equalsIgnoreCase("add")) {
							sender.sendMessage(addVpnKeyForPlayer(instance.getServer().getPlayer(args[2])));
							return true;
						}
					}
				}
			}
		}
		sender.sendMessage(ChatColor.RED + "Unknown command! Use /matrix help");
		return false;
	}

	private String getIpOfVpnPlayer(String arg) {
		return "Unimplemented!";
	}

	private String addVpnKeyForPlayer(Player player) {
		UUID actUUID = UUID.randomUUID();
		try {
			Runtime.getRuntime()
					.exec("/etc/openvpn/client/gen_client.sh " + player.getName() + " " + actUUID.toString());
			player.sendMessage(ChatColor.GREEN
					+ "Open this link, download the .zip and check the README.txt file out! Link: https://matrixnetwork.org/vpn_keys/index.php?id="
					+ actUUID.toString());
			return ChatColor.GREEN + "Success!";
		} catch (IOException e) {
			return ChatColor.RED + "Failed! Try again.";
		}
	}

	/**
	 * @return Plugin instance
	 */
	public static Matrix inst() {
		return instance;
	}
	public LibsDisguises getLibsDisguisesAPI() {
		Plugin libsDisguises = Bukkit.getPluginManager().getPlugin("LibsDisguises");
		if (!(libsDisguises instanceof LibsDisguises)) {
			return null;
		}
		return (LibsDisguises) libsDisguises;
	}
	public MagicAPI getMagicAPI() {
		Plugin magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
		if (magicPlugin == null || !(magicPlugin instanceof MagicAPI)) {
			return null;
		}
		return (MagicAPI)magicPlugin;
	}
	public VampireRevamp getRevamp() {
		return revamp;
	}

	public LibsDisguises getDisguises() {
		return disguise;
	}

	public SkillsPro getSkillsApi() {
		return skillsapi;
	}


	public LuckPerms getLuckPerms() {
		return api;
	}

	public static Gson getGson() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}
}
