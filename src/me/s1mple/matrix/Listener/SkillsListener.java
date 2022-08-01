package me.s1mple.matrix.Listener;


import com.clanjhoo.vampire.VampireAPI;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.event.AbilityDamageEntityEvent;
import com.projectkorra.projectkorra.event.AbilityProgressEvent;
import com.projectkorra.projectkorra.event.AbilityStartEvent;
import me.s1mple.matrix.Matrix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.skills.api.SkillsAPI;
import org.skills.data.managers.SkilledPlayer;
import org.skills.managers.HealthAndEnergyManager;
import org.skills.types.SkillManager;
import com.clanjhoo.vampire.event.VampireTypeChangeEvent;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;

import java.util.Objects;

public class SkillsListener implements Listener{
		LuckPerms api = Matrix.inst().getLuckPerms();

		@EventHandler
		public void firebendingListener(AbilityDamageEntityEvent event) {
			if (event.getSource() != null)
				if ((event.getAbility().getElement() == Element.FIRE) && VampireAPI.isVampire(event.getSource())) {
					event.getSource().damage(event.getDamage());
					event.getSource().setFireTicks(80);
					event.setCancelled(true);
					event.setDamage(0);
				}
		}

	   @EventHandler
		public void vampCmdListener(VampireTypeChangeEvent event) {
		   Player player = event.getUplayer().getPlayer();

			if(event.isVampire()) {
				SkilledPlayer.getSkilledPlayer(player).setActiveSkill(SkillManager.getSkill("Vampire"));
				event.getUplayer().getPlayer().sendRawMessage("§cYou have unlocked the §4Vampire§c skill tree. §4Do /skills improve");
				api.getUserManager().getUser(player.getUniqueId()).data().add(Node.builder("-skills.select.*").build());
				api.getUserManager().getUser(player.getUniqueId()).data().add(Node.builder("-bending.ability.fire.*").build());

				HealthAndEnergyManager.updateStats(player);
			}
			else {
				SkilledPlayer.getSkilledPlayer(player).setActiveSkill(SkillManager.getSkill("Arbalist"));
				event.getUplayer().getPlayer().sendRawMessage("§cYou have been removed from the Vampire skill tree!");
				api.getUserManager().getUser(player.getUniqueId()).data().remove(Node.builder("-skills.select.*").build());
				api.getUserManager().getUser(player.getUniqueId()).data().remove(Node.builder("-bending.ability.fire.*").build());
				HealthAndEnergyManager.updateStats(player);

			}
			
		    api.getUserManager().saveUser(Objects.requireNonNull(api.getUserManager().getUser(player.getUniqueId())));
		}

}
