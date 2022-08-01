package me.s1mple.matrix.Raid;

import me.crashcringle.cringlebosses.CringleBoss;
import me.s1mple.matrix.Matrix;
import me.s1mple.matrix.MatrixMethods;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.Material;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.skills.data.managers.SkilledPlayer;

import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;

public class RaidListener implements Listener {
	int num = 1;
	public static boolean forceStart = false;
	Raid pirateRaid;
	Location pirateLocation;

	@EventHandler
	public void onStartRaid(RaidTriggerEvent event) {

		// Check if player with Bad Omen is above level 60
		if (SkilledPlayer.getSkilledPlayer(event.getPlayer()).getLevel() >= 50 || forceStart) {
			// If the player is above level 60 then there is a 1/5 chance that a Pirate Raid
			// occurs
			num = forceStart ? 5 : (int) (Math.random() * 100) + 1;

			if (num % 5 == 0) {

				// Depending on the result of the player's previous raid, broadcast a message
				// accordingly.
				if (event.getPlayer().hasPermission("matrix.raid.Pirate.Lose"))
					Bukkit.broadcastMessage("§ePirates have returned to finish what they started §b"
							+ event.getPlayer().getName() + "§e!");
				else if (event.getPlayer().hasPermission("matrix.raid.Pirate.Win"))
					Bukkit.broadcastMessage("§eThe Pirates have returned with a vengeance for §b"
							+ event.getPlayer().getName() + "§e!");
				else
					Bukkit.broadcastMessage("§ePirates are invading §b" + event.getPlayer().getName() + "§e!");

				// We want max levels for the raid so that all possible entities spawn
				event.getRaid().setBadOmenLevel(5);

				// If the custom raid was force started we can now turn this flag off as the
				// raid is already beginning
				forceStart = false;

				pirateRaid = event.getRaid();
				pirateLocation = event.getRaid().getLocation();

				// They need some tunes for the raid
				playPigStep(event.getRaid(), event.getPlayer());
				BukkitScheduler scheduler = Matrix.inst().getServer().getScheduler();
				scheduler.scheduleSyncDelayedTask(Matrix.inst(), new Runnable() {
					@Override
					public void run() {
						playPigStep(event.getRaid(), event.getPlayer());
					}
				}, 2980L);
			}
		}
	}

	/**
	 * This method plays pigstep for everyone in a 40 block radius of the player who
	 * initiated the raid
	 * 
	 * @param raid
	 * @param player
	 */
	public void playPigStep(Raid raid, Player player) {
		player.playSound(raid.getLocation(), Sound.MUSIC_DISC_PIGSTEP, SoundCategory.MUSIC, 100, 1);
		for (Entity e : player.getNearbyEntities(40, 40, 40)) {
			if (e instanceof Player) {
				((Player) e).playSound(raid.getLocation(), Sound.MUSIC_DISC_PIGSTEP, SoundCategory.MUSIC, 100, 1);
			}
		}

	}

	@EventHandler
	public void onEndRaid(RaidStopEvent event) {
		if (num % 5 == 0) {
			for (UUID heroes : event.getRaid().getHeroes()) {
				Player player = Bukkit.getPlayer(heroes);
				if (player != null) {
					if (event.getRaid().getStatus() == Raid.RaidStatus.VICTORY) {
						pirateRaidRewards(player);
					}
					else {
						pirateRaidConsequence(player);
					}
				}
			}
			num = 1;
		}
	}

	@EventHandler
	public void onRaid(RaidSpawnWaveEvent event) {
		System.out.println(num);
		if (num % 5 == 0) {
			pirateRaidWaves(event);
		}
	}
	public void pirateRaidConsequence(Player player) {
		Bukkit.broadcastMessage("§ePirates have successfully pillaged our heroes!");
		player.sendMessage("§eThe Pirates have stolen your cheese!");
		MatrixMethods.removePermission(player, "matrix.raid.Pirate.Win");
		MatrixMethods.addPermission(player, "matrix.raid.Pirate.Lose");
		MatrixMethods.ConsoleCmd("eco take " + player.getName() + " 10%");
	}
	public void pirateRaidRewards(Player player) {
			player.sendMessage("§eYou have survived the Pirate Invasion");
			int reward = (int) (Math.random() * 100) + 1;
			if (reward > 75)
				MatrixMethods.ConsoleCmd("em give " + player.getName() + " unbind_scroll.yml");
			if (reward % 10 == 0)
				MatrixMethods.ConsoleCmd(
						"mgive " + player.getName() + " PirateParrotEgg " + (int) (Math.random() * 10) + 1);
			if (reward % 15 == 0)
				MatrixMethods.ConsoleCmd("mgive " + player.getName() + " ChemicalX 1");
			if (reward % 20 == 0)
				MatrixMethods.ConsoleCmd("mgive " + player.getName() + " BadDayArrow "
						+ (int) (Math.random() * 100));
			if (reward % 30 == 0)
				MatrixMethods.ConsoleCmd(
						"mgive " + player.getName() + " chaojuice " + (int) (Math.random() * 4));
			if (reward % 5 == 0)
				MatrixMethods.ConsoleCmd("mgive " + player.getName() + " Enchanted_Golden_Apple "
						+ (int) (Math.random() * 10));
			if (reward % 2 == 0 )
				MatrixMethods.ConsoleCmd(
						"mgive " + player.getName() + " ChaosSoul " + (int) (((Math.random() * 2) + 1)));
			MatrixMethods.addPermission(player, "matrix.raid.Pirate.Win");
			MatrixMethods.removePermission(player, "matrix.raid.Pirate.Lose");
	}
	/**
	 * This method is called within the onRaid event to
	 * turn the raid into a Pirate Raid. It handles the mob spawns
	 * and disguises
	 * @param event The given wave of raiders
	 */
	public void pirateRaidWaves(RaidSpawnWaveEvent event) {
		PlayerDisguise playerDisguise;
		for (Raider raider : event.getRaiders()) {
			if (raider.getHealth() <= 40) {
				raider.setMaxHealth(300);
				raider.setHealth(300);
			}
			switch (raider.getType()) {
				case VINDICATOR:
					CringleBoss.spawnMagicMob("PirateBrute", raider.getLocation());
					playerDisguise = new PlayerDisguise("_Tommy");
					playerDisguise.setName("Pirate Runt");
					playerDisguise.setEntity(raider);
					playerDisguise.startDisguise();
					break;
				case PILLAGER:
					MatrixMethods.ConsoleCmd("mm m spawn Pirate 1 " + raider.getWorld().getName() + "," + raider.getLocation().getX() + ","
							+ raider.getLocation().getY() + "," + raider.getLocation().getZ());
					CringleBoss.spawnMagicMob("Pirate", raider.getLocation());
					playerDisguise = new PlayerDisguise("MatrixPirateGirl");
					playerDisguise.setName("Pirate Archer");
					playerDisguise.setEntity(raider);
					playerDisguise.startDisguise();
					break;
				case EVOKER:
					CringleBoss.spawnMagicMob("PirateWizard", raider.getLocation());
					playerDisguise = new PlayerDisguise("MatrixPirateGoblin");
					playerDisguise.setName("Pirate Evoker");
					playerDisguise.setEntity(raider);
					playerDisguise.startDisguise();
					break;
				case WITCH:
					CringleBoss.spawnMagicMob("PirateWizard", raider.getLocation());
					playerDisguise = new PlayerDisguise("FemalePirate");
					playerDisguise.setName("Alchemist");
					playerDisguise.setEntity(raider);
					FlagWatcher watcher = playerDisguise.getWatcher();
					watcher.setItemInMainHand(new ItemStack(Material.POTION));
					watcher.setItemInOffHand(new ItemStack(Material.LINGERING_POTION));
					playerDisguise.startDisguise();
					break;
				case ILLUSIONER:
					CringleBoss.spawnMagicMob("PirateGhost", raider.getLocation());
					playerDisguise = new PlayerDisguise("ChaosPirateGhost");
					playerDisguise.setName("Pirate Ghost");
					playerDisguise.setEntity(raider);
					playerDisguise.startDisguise();
					break;
				case RAVAGER:
					CringleBoss.spawnMagicMob("PirateGhost", raider.getLocation());
					CringleBoss.spawnMagicMob("PirateLurker", event.getRaid().getLocation());
					break;
				default:
					playerDisguise = new PlayerDisguise("_Tommy");
					playerDisguise.setName("Pirate Runt");
					playerDisguise.setEntity(raider);
					playerDisguise.startDisguise();
					break;
			}
		}
	}
}
