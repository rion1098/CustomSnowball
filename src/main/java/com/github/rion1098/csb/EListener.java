package com.github.rion1098.csb;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class EListener implements Listener{
	//private String prefix = ChatColor.AQUA + "[CSB]" + ChatColor.RESET;
	//private String warnprefix = ChatColor.RED + "[CSB WARN]" + ChatColor.RESET;
	Plugin plugin = Main.getInstance();
	ScoreboardManager manager = Bukkit.getScoreboardManager();
	Scoreboard board = manager.getMainScoreboard();

	@EventHandler
	public void launchProject(ProjectileLaunchEvent event){
		if (event.getEntity().getShooter() instanceof Player) {
			if (event.getEntity() instanceof Snowball) {
				Player player = (Player)event.getEntity().getShooter();
				Snowball snowball = (Snowball)event.getEntity();

				Team team = getJoinTeam(player);
				if ( !(team == null)) {
					snowball.setMetadata("team", new FixedMetadataValue(plugin, getJoinTeam(player)));
				}
			}
		}
	}

	@EventHandler
	public void hitProjectile(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Snowball) {
			final Snowball snowball = (Snowball)event.getEntity();
			List<Entity> entities = snowball.getNearbyEntities(2, 2, 2);

			if (entities != null) {
				for (Entity entity : entities) {
					if (!(entity instanceof Player))
					continue;

					Player player = (Player) entity;
					Object playerTeam = (Object) getJoinTeam(player);
					Object ballTeam = getMetadata(snowball, "team");

					if (ballTeam != null && ballTeam.equals(playerTeam)) {
						if (Main.catchBall) {
							player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 1, 1);
							snowball.remove();
							return;
						} else {
							final Vector vector = snowball.getVelocity();

							new BukkitRunnable() {

								@Override
								public void run() {
									snowball.teleport(snowball.getLocation().add(vector.multiply(2)));
									snowball.setVelocity(vector);
								}
							}.runTaskLater(plugin, 20);
						}
					}
				}
			}
			if (Main.dropBall == true) {
				if (event.getEntity() instanceof Snowball) {
					Location loc = event.getEntity().getLocation();
					loc.getWorld().dropItem(loc, new ItemStack(Material.SNOW_BALL));
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamaged(EntityDamageEvent event){
		if ((event instanceof EntityDamageByEntityEvent)){
			EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent)event;
			Entity damageEntity = event2.getDamager();

			if ((damageEntity instanceof Snowball)){
				event.setDamage(Main.yukidama);
			}
		}
	}

	@EventHandler
	public void onEntityDeath(PlayerDeathEvent e){
		Player killer = e.getEntity().getKiller();
		killer.playSound(killer.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
	}

	private Team getJoinTeam(Player player) {
		Team team = board.getEntryTeam(player.getName());
		return team;
	}

	public void setMetadata(Metadatable object, String key, Object value) {
		object.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	public Object getMetadata(Metadatable object, String key) {
		List<MetadataValue> values = object.getMetadata(key);
		for(MetadataValue value : values){
			if (value.getOwningPlugin() == plugin) {
				return value.value();
			}
		}

		return null;
	}
}
