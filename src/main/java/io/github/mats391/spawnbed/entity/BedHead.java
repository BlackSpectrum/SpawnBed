package io.github.mats391.spawnbed.entity;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Bed;
import org.json.simple.JSONArray;

public class BedHead
{

	private final int		x, y, z;
	private final BlockFace	orientation;
	private final UUID		worldUid;

	public BedHead(Block block) {
		final Bed bed = (Bed) block.getState().getData();
		if ( !bed.isHeadOfBed() )
			block = block.getRelative( bed.getFacing() );

		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		this.orientation = bed.getFacing();
		this.worldUid = block.getWorld().getUID();
	}

	public BedHead(final JSONArray arr, final World world) {
		this.x = ( (Long) arr.get( 0 ) ).intValue();
		this.y = ( (Long) arr.get( 1 ) ).intValue();
		this.z = ( (Long) arr.get( 2 ) ).intValue();
		this.orientation = BlockFace.valueOf( (String) arr.get( 3 ) );
		this.worldUid = world.getUID();
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
		if ( this.worldUid == null )
		{
			if ( other.worldUid != null )
				return false;
		}
		else if ( !this.worldUid.equals( other.worldUid ) )
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
		return new Location( Bukkit.getWorld( this.worldUid ), this.x, this.y, this.z );
	}

	public BlockFace getOrientation() {
		return this.orientation;
	}

	public Location getSpawnLocation() {
		return this.getSpawnLocation( false );
	}

	public Location getSpawnLocation( final boolean force ) {
		// /Gets the block above the feet of the bed
		final Block bedFeet = Bukkit.getWorld( this.worldUid ).getBlockAt( this.x, this.y, this.z )
				.getRelative( this.orientation.getOppositeFace() ).getRelative( BlockFace.UP );

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

	public UUID getWorldUid() {
		return this.worldUid;
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
		result = prime * result + ( this.worldUid == null ? 0 : this.worldUid.hashCode() );
		result = prime * result + this.x;
		result = prime * result + this.y;
		result = prime * result + this.z;
		return result;
	}

	public boolean isSpawnable() {
		return this.getSpawnLocation( false ) != null;
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
