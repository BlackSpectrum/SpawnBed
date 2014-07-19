package eu.blackspectrum.spawnbed.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import eu.blackspectrum.spawnbed.SpawnBed;
import eu.blackspectrum.spawnbed.entity.BedHead;
import eu.blackspectrum.spawnbed.util.Util;

public class BlockListener implements Listener
{

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak( final BlockBreakEvent event ) {
		if ( event.isCancelled() )
			return;

		final Block block = event.getBlock();

		// Only consider BED_BLOCK
		if ( !block.getType().equals( Material.BED_BLOCK ) )
			return;

		// Only blocks in overworld
		if ( !block.getWorld().equals( SpawnBed.overWorld ) )
			return;

		// Create bed for that block
		final BedHead bed = new BedHead( block );

		// Find owner
		final Player owner = Util.getOwnerOfBed( bed );

		// If no owner end
		if ( owner == null )
			return;

		// Remove bed
		Util.removeBedFromMap( owner );
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode( final EntityExplodeEvent event ) {
		if ( event.isCancelled() )
			return;

		if ( SpawnBed.excludedWorlds.contains( event.getLocation().getWorld().getUID() ) )
			return;

		// Iterate all destroyed blocks
		for ( final Block block : event.blockList() )
		{
			// Only consider BED_BLOCK
			if ( !block.getType().equals( Material.BED_BLOCK ) )
				continue;

			// Create bed for that block
			final BedHead bed = new BedHead( block );

			Util.checkOwnerAndRemove( bed );
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

			Util.checkOwnerAndRemove( bed );

			to.breakNaturally();
		}

	}

}
