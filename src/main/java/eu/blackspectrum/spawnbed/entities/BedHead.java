package eu.blackspectrum.spawnbed.entities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;
import org.json.simple.JSONArray;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.ps.PS;

import eu.blackspectrum.spawnbed.SpawnBed;
import eu.blackspectrum.spawnbed.util.Util;

public class BedHead
{


	private final int		x, y, z;
	private final BlockFace	orientation;




	public BedHead(Block block) {
		final Bed bed = (Bed) block.getState().getData();
		if ( !bed.isHeadOfBed() )
			block = block.getRelative( bed.getFacing() );

		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		this.orientation = bed.getFacing();
	}




	public BedHead(final JSONArray arr) {
		this.x = ( (Long) arr.get( 0 ) ).intValue();
		this.y = ( (Long) arr.get( 1 ) ).intValue();
		this.z = ( (Long) arr.get( 2 ) ).intValue();
		this.orientation = BlockFace.valueOf( (String) arr.get( 3 ) );
	}




	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( this.getClass() != obj.getClass() )
			return false;
		final BedHead other = (BedHead) obj;
		if ( this.orientation != other.orientation )
			return false;
		if ( this.x != other.x )
			return false;
		if ( this.y != other.y )
			return false;
		if ( this.z != other.z )
			return false;
		return true;
	}




	public Location getLocation() {
		return new Location( SpawnBed.overWorld, this.x, this.y, this.z );
	}




	public BlockFace getOrientation() {
		return this.orientation;
	}




	public Location getSpawnLocation() {
		return this.getSpawnLocation( false );
	}




	public Location getSpawnLocation( final boolean force ) {
		// /Gets the block above the feet of the bed
		final Block bedFeet = SpawnBed.overWorld.getBlockAt( this.x, this.y, this.z ).getRelative( this.orientation.getOppositeFace() )
				.getRelative( BlockFace.UP );

		if ( force )
			return bedFeet.getLocation().add( 0.5d, 0, 0.5d );

		for ( final BlockFace face : new BlockFace[] { BlockFace.EAST, BlockFace.NORTH_EAST, BlockFace.NORTH, BlockFace.NORTH_WEST,
				BlockFace.WEST, BlockFace.SOUTH_WEST, BlockFace.SOUTH, BlockFace.SOUTH_EAST } )
		{
			final Block block = bedFeet.getRelative( face );
			if ( block.getType().isSolid() )
				continue;

			if ( !block.getRelative( BlockFace.DOWN ).getType().isSolid() )
				return block.getLocation().add( 0.5d, -0.5d, 0.5d );

			if ( !block.getRelative( BlockFace.UP ).getType().isSolid() )
				return block.getLocation().add( 0.5d, 0, 0.5d );
		}

		return null;
	}




	public int getX() {
		return this.x;
	}




	public int getY() {
		return this.y;
	}




	public int getZ() {
		return this.z;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( this.orientation == null ? 0 : this.orientation.hashCode() );
		result = prime * result + this.x;
		result = prime * result + this.y;
		result = prime * result + this.z;
		return result;
	}




	/**
	 *
	 * @param player
	 *            Player of this SpawnBed
	 * @param verbose
	 *            Should the player be notified?
	 * @return true if spawnable
	 */
	public boolean isSpawnable( final Player player, final boolean verbose ) {

		if ( this.getSpawnLocation() == null )
		{
			if ( verbose )
				player.sendMessage( ChatColor.AQUA + "Your SpawnBed is buried." );
			return false;
		}
		final UPlayer uplayer = UPlayer.get( player );

		if ( !FPerm.BUILD.has( uplayer, PS.valueOf( this.getLocation() ), false ) )
		{
			if ( verbose )
				player.sendMessage( ChatColor.AQUA + "Your SpawnBed is no longer in territory, where you can build in." );

			Util.removeBedFromMap( player );
			return false;
		}

		return true;
	}




	@SuppressWarnings("unchecked")
	public JSONArray toArray() {

		final JSONArray arr = new JSONArray();
		arr.add( this.x );
		arr.add( this.y );
		arr.add( this.z );
		arr.add( this.orientation.name() );

		return arr;
	}

}
