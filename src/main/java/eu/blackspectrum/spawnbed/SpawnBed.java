package eu.blackspectrum.spawnbed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import eu.blackspectrum.spawnbed.entity.BedHead;
import eu.blackspectrum.spawnbed.listener.BlockListener;
import eu.blackspectrum.spawnbed.listener.PlayerListener;
import eu.blackspectrum.spawnbed.util.DiscUtil;

public class SpawnBed extends JavaPlugin
{

	public static String				pluginName;

	public static Map<UUID, BedHead>	beds			= new HashMap<UUID, BedHead>();
	public static ArrayList<UUID>		excludedWorlds	= new ArrayList<UUID>();
	public static FileConfiguration		config;
	public static World					overWorld;

	@Override
	public void onDisable() {
		DiscUtil.saveToDisc( beds );
	}

	@Override
	public void onEnable() {

		this.setUpConfig();

		pluginName = this.getName();
		overWorld = getServer().getWorld( config.getString( "overWorld" ) );

		new File( "plugins" + File.separator + "BSPSpawnBed" + File.separator + "db" ).mkdir();

		Map<UUID, BedHead> loaded = DiscUtil.loadFromDisc();
		if ( loaded != null )
			beds.putAll( loaded );

		final PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents( new PlayerListener(), this );
		pm.registerEvents( new BlockListener(), this );
	}

	private void setUpConfig() {
		config = this.getConfig();

		config.set( "overWorld", config.getString( "overWorld", "world" ) );

		this.saveConfig();
	}

}
