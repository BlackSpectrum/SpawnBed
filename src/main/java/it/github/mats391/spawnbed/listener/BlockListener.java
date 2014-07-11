package it.github.mats391.spawnbed.listener;

import it.github.mats391.spawnbed.SpawnBed;
import it.github.mats391.spawnbed.entity.BedHead;
import it.github.mats391.spawnbed.util.Util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockListener implements Listener
{

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak( final BlockBreakEvent event ) {
		if ( event.isCancelled() )
			return;

		final Block block = event.getBlock();

		// /Only consider BED_BLOCK
		if ( !block.getType().equals( Material.BED_BLOCK ) )
			return;

		if ( SpawnBed.excludedWorlds.contains( block.getLocation().getWorld().getUID() ) )
			return;

		// /Create bed for that block
		final BedHead bed = new BedHead( block );

		// /Find owner
		final Player owner = Util.getOwnerOfBed( bed );

		// /If no owner end
		if ( owner == null )
			return;

		// /Remove bed
		Util.removeBedFromMap( owner, bed.getWorldUid() );
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode( final EntityExplodeEvent event ) {
		if ( event.isCancelled() )
			return;

		if ( SpawnBed.excludedWorlds.contains( event.getLocation().getWorld().getUID() ) )
			return;

		// /Iterate all destroyed blocks
		for ( final Block block : event.blockList() )
		{
			// /Only consider BED_BLOCK
			if ( !block.getType().equals( Material.BED_BLOCK ) )
				continue;

			// /Create bed for that block
			final BedHead bed = new BedHead( block );

			// /Find owner
			final Player owner = Util.getOwnerOfBed( bed );

			// /If no owner continue
			if ( owner == null )
				continue;

			// /Remove bed
			Util.removeBedFromMap( owner, bed.getWorldUid() );
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onLiquidFromTo( final BlockFromToEvent event ) {
		if ( event.isCancelled() )
			return;

		if ( !event.getBlock().isLiquid() )
			return;

		final Block to = event.getToBlock();

		// /If bed destroy and check if used by anyone
		if ( to.getType().equals( Material.BED_BLOCK ) )
		{

			// /Create bed for that block
			final BedHead bed = new BedHead( to );

			// /Find owner
			final Player owner = Util.getOwnerOfBed( bed );

			// /If no owner end
			if ( owner == null )
				return;

			// /Remove bed
			Util.removeBedFromMap( owner, bed.getWorldUid() );

			to.breakNaturally();
		}

	}

}
