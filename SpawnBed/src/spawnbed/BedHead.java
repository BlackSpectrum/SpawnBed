package spawnbed;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Bed;
import org.json.simple.JSONArray;


public class BedHead {
	
	private final int x, y, z; 
	private final BlockFace orientation;
	private final UUID worldUid;
	
	
	public BedHead(Block block)
	{
		Bed bed = ((Bed)block.getState().getData());
		if(!bed.isHeadOfBed() )
			block = block.getRelative(bed.getFacing());
		
		
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		this.orientation = bed.getFacing();
		this.worldUid = block.getWorld().getUID();
	}
	

	public BedHead(JSONArray arr, World world)
	{			
		this.x =  ((Long) arr.get(0)).intValue();
		this.y = ((Long) arr.get(1)).intValue();
		this.z = ((Long) arr.get(2)).intValue();
		this.orientation = BlockFace.valueOf((String) arr.get(3));
		this.worldUid = world.getUID();
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray toArray()
	{
		
		JSONArray arr = new JSONArray();
		arr.add(x);
		arr.add(y);
		arr.add(z);
		arr.add(orientation.name());
		
		return arr;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
	
	public BlockFace getOrientation(){
		return orientation;
	}
	
	public UUID getWorldUid(){
		return worldUid;
	}
	
	public Location getLocation(){
		return new Location(Bukkit.getWorld(worldUid), x, y, z);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((orientation == null) ? 0 : orientation.hashCode());
		result = prime * result
				+ ((worldUid == null) ? 0 : worldUid.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BedHead other = (BedHead) obj;
		if (orientation != other.orientation)
			return false;
		if (worldUid == null) {
			if (other.worldUid != null)
				return false;
		} else if (!worldUid.equals(other.worldUid))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
	

}
