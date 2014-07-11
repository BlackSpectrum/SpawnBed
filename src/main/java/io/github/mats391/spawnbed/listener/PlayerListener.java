package io.github.mats391.spawnbed.listener;

import io.github.mats391.spawnbed.SpawnBed;
import io.github.mats391.spawnbed.entity.BedHead;
import io.github.mats391.spawnbed.util.DiscUtil;
import io.github.mats391.spawnbed.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.ps.PS;

public class PlayerListener implements Listener
{

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract( final PlayerInteractEvent event ) {
		if ( event.isCancelled() )
			return;

		// /Check if it is a Bed
		final Block clickedBlock = event.getClickedBlock();

		if ( clickedBlock.getType() != Material.BED_BLOCK )
			return;

		if ( clickedBlock.getWorld().getEnvironment().equals( Environment.NETHER )
				|| clickedBlock.getWorld().getEnvironment().equals( Environment.THE_END ) )
			return;

		if ( SpawnBed.excludedWorlds.contains( clickedBlock.getLocation().getWorld().getUID() ) )
			return;

		final boolean isRightClick = event.getAction().equals( Action.RIGHT_CLICK_BLOCK );

		// /Prevent sleeping
		event.setCancelled( isRightClick );

		final BedHead bed = new BedHead( clickedBlock );
		final Player player = event.getPlayer();

		// /Check if it is already used
		final Player owner = Util.getOwnerOfBed( bed );

		if ( owner != null )
		{
			if ( owner.getUniqueId().equals( player.getUniqueId() ) )
				if ( isRightClick )
				{
					final Location respawn = bed.getSpawnLocation();
					if ( respawn == null )
						player.sendMessage( ChatColor.AQUA + "Your SpawnBed is buried." );
					else
						player.sendMessage( ChatColor.AQUA + "This is your SpawnBed." );
				}
				else
				{
					Util.removeBedFromMap( player, clickedBlock.getWorld().getUID() );
					player.sendMessage( ChatColor.AQUA + "No longer respawning here." );
				}
			else if ( isRightClick )
				player.sendMessage( ChatColor.AQUA + "This bed is already occupied by " + owner.getDisplayName() + "." );

			return;
		}

		if ( !isRightClick )
			return;

		final UPlayer uplayer = UPlayer.get( player );

		if ( !FPerm.BUILD.has( uplayer, PS.valueOf( clickedBlock.getLocation() ), false ) )
		{
			player.sendMessage( ChatColor.AQUA + "Your SpawnBed must be in territory, where you can build in." );
			return;
		}

		// /Add bed to the map
		Util.addBedToMap( player, bed );
		player.sendMessage( ChatColor.AQUA + "SpawnBed set." );

		DiscUtil.saveToDisc( bed.getWorldUid(), SpawnBed.beds.get( bed.getWorldUid() ) );
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn( final PlayerRespawnEvent event ) {
		final Player player = event.getPlayer();
		World world = player.getWorld();

		// Id died in nether use overworld
		if ( world.getEnvironment().equals( Environment.NETHER ) || world.getEnvironment().equals( Environment.THE_END ) )
			world = Bukkit.getWorld( SpawnBed.config.getString( "overworld" ) );

		BedHead bed = Util.getBed( player, world );

		// If no bed found in the overworld use respawn world
		if ( bed == null )
			world = event.getRespawnLocation().getWorld();

		if ( SpawnBed.excludedWorlds.contains( world.getUID() ) )
			return;

		bed = Util.getBed( player, event.getRespawnLocation().getWorld() );

		// If still no bed, use default respawn
		if ( bed == null )
			return;

		final UPlayer uplayer = UPlayer.get( player );

		if ( !FPerm.BUILD.has( uplayer, PS.valueOf( bed.getLocation() ), false ) )
		{
			player.sendMessage( ChatColor.AQUA + "Your SpawnBed is no longer in territory, where you can build in." );
			Util.removeBedFromMap( player, world.getUID() );
			return;
		}

		final Location respawn = bed.getSpawnLocation();
		if ( respawn == null )
		{
			player.sendMessage( ChatColor.AQUA + "Your SpawnBed is buried." );
			return;
		}

		event.setRespawnLocation( respawn );
	}

}
