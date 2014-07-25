package eu.blackspectrum.spawnbed.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.ps.PS;

import eu.blackspectrum.spawnbed.SpawnBed;
import eu.blackspectrum.spawnbed.entities.BedHead;
import eu.blackspectrum.spawnbed.util.Util;

public class PlayerListener implements Listener
{


	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage( final EntityDamageEvent event ) {
		if ( event.isCancelled() )
			return;
		
		Entity victim = event.getEntity();

		if ( !victim.getType().equals( EntityType.PLAYER ) )
			return;

		for ( final MetadataValue m : victim.getMetadata( "protectionTime" ) )
			if ( m.getOwningPlugin().equals( SpawnBed.instance ) )
			{
				if ( System.currentTimeMillis() <= m.asLong() )
					event.setDamage( event.getDamage() * SpawnBed.config.getDouble( "SpawnSafe.damageToProtected" ) );
				else
					victim.removeMetadata( "protectionTime", SpawnBed.instance );
				return;
			}
	}




	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity( final EntityDamageByEntityEvent event ) {

		if ( event.isCancelled() )
			return;

		Entity attacker = event.getDamager();

		if ( !event.getEntity().getType().equals( EntityType.PLAYER ) || !attacker.getType().equals( EntityType.PLAYER ) )
			return;

		for ( final MetadataValue m : attacker.getMetadata( "protectionTime" ) )
			if ( m.getOwningPlugin().equals( SpawnBed.instance ) )
			{
				if ( System.currentTimeMillis() <= m.asLong() )
					event.setDamage( event.getDamage() * SpawnBed.config.getDouble( "SpawnSafe.damageByProtected" ) );
				else
					attacker.removeMetadata( "protectionTime", SpawnBed.instance );
				return;
			}
	}




	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChangedWorld( final PlayerChangedWorldEvent event ) {
		final Player player = event.getPlayer();

		player.setMetadata(
				"protectionTime",
				new FixedMetadataValue( SpawnBed.instance, System.currentTimeMillis()
						+ SpawnBed.config.getDouble( "SpawnSafe.protectionTime" ) * 1000 ) );
	}




	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract( final PlayerInteractEvent event ) {
		if ( event.isCancelled() )
			return;

		// Check if it is a Bed
		final Block clickedBlock = event.getClickedBlock();

		if ( clickedBlock.getType() != Material.BED_BLOCK )
			return;

		// Only beds are in overworld
		if ( !clickedBlock.getWorld().equals( SpawnBed.overWorld ) )
			return;
		final boolean isRightClick = event.getAction().equals( Action.RIGHT_CLICK_BLOCK );

		// Prevent sleeping
		event.setCancelled( isRightClick );

		final BedHead bed = new BedHead( clickedBlock );
		final Player player = event.getPlayer();

		// Check if it is already used
		final Player owner = Util.getOwnerOfBed( bed );

		if ( owner != null )
		{
			if ( owner.getUniqueId().equals( player.getUniqueId() ) )
				if ( isRightClick )
				{
					if ( bed.isSpawnable( player, true ) )
						player.sendMessage( ChatColor.AQUA + "This is your SpawnBed." );
				}
				else
				{
					Util.removeBedFromMap( player );
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

		// Add bed to the map
		Util.addBedToMap( player, bed, true );
	}

}
