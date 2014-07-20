package eu.blackspectrum.spawnbed.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import eu.blackspectrum.spawnbed.SpawnBed;
import eu.blackspectrum.spawnbed.entities.BedHead;

public class Util
{


	public static void addBedToMap( final Player player, final BedHead bed, final boolean verbose ) {

		SpawnBed.beds.put( player.getUniqueId(), bed );
		DiscUtil.saveToDisc( SpawnBed.beds );

		if ( verbose )
			player.sendMessage( ChatColor.AQUA + "SpawnBed set." );

	}




	public static boolean checkOwnerAndRemove( final BedHead bed ) {
		final Player owner = getOwnerOfBed( bed );

		if ( owner != null )
		{
			removeBedFromMap( owner );
			return true;
		}
		else
			return false;
	}




	public static BedHead getBed( final Player player, final boolean verbose ) {

		final BedHead retBedHead = SpawnBed.beds.get( player.getUniqueId() );

		if ( verbose && retBedHead == null )
			player.sendMessage( ChatColor.AQUA + "You have no Spawnbed." );

		return retBedHead;
	}




	public static Player getOwnerOfBed( final BedHead bed ) {

		for ( final UUID u : SpawnBed.beds.keySet() )
			if ( SpawnBed.beds.get( u ).equals( bed ) )
				return Bukkit.getServer().getPlayer( u );

		return null;
	}




	public static boolean removeBedFromMap( final Player player ) {
		final UUID playerUid = player.getUniqueId();

		if ( SpawnBed.beds.containsKey( playerUid ) )
		{
			SpawnBed.beds.remove( playerUid );
			DiscUtil.saveToDisc( SpawnBed.beds );
			return true;
		}
		else
			return false;
	}
}
