package spawnbed.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import spawnbed.BedHead;
import spawnbed.SpawnBed;

public class Util {
	
	
	public static Player getOwnerOfBed(final BedHead bed)
	{
		Map<UUID, BedHead> temp = SpawnBed.beds.get(bed.getWorldUid());
		if(temp == null)
			return null;
		
		for(UUID u : temp.keySet() )
			if(temp.get(u).equals(bed))
				return Bukkit.getServer().getPlayer(u);		
		
		return null;
	}
	
	public static void addBedToMap(final Player player, final BedHead bed)
	{
		UUID uid = bed.getWorldUid();
		Map<UUID, BedHead> temp = SpawnBed.beds.get(uid);
		
		if(temp == null)
			temp = new HashMap<UUID, BedHead>();		
		
		temp.put(player.getUniqueId(), bed);
		
		SpawnBed.beds.put(uid, temp);
	}

	
	public static boolean removeBedFromMap(final Player player, final UUID worldUid)
	{
		UUID playerUid = player.getUniqueId();
		
		if(SpawnBed.beds.get(worldUid) == null)
			return false;
		
		if(SpawnBed.beds.get(worldUid).containsKey(playerUid))
		{
			SpawnBed.beds.get(worldUid).remove(playerUid);
			DiscUtil.saveToDisc(worldUid, SpawnBed.beds.get(worldUid));
			return true;
		}
		else
			return false;
	}
	
	public static BedHead getBed(final Player player, final World world)
	{
		Map<UUID, BedHead> temp = SpawnBed.beds.get(world.getUID());
		
		if(temp != null)
			return temp.get(player.getUniqueId());
		else
			return null;
	}
}
