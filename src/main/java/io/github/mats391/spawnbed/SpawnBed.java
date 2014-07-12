package io.github.mats391.spawnbed;

import io.github.mats391.spawnbed.entity.BedHead;
import io.github.mats391.spawnbed.listener.BlockListener;
import io.github.mats391.spawnbed.listener.PlayerListener;
import io.github.mats391.spawnbed.util.DiscUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnBed extends JavaPlugin
{

	public static String						pluginName;

	// / Map<WorldName, Map<PlayerName, BedHead>>
	public static Map<UUID, Map<UUID, BedHead>>	beds			= new HashMap<UUID, Map<UUID, BedHead>>();
	public static ArrayList<UUID>				excludedWorlds	= new ArrayList<UUID>();
	public static FileConfiguration				config;

	@Override
	public void onDisable() {
		for ( final UUID u : beds.keySet() )
			DiscUtil.saveToDisc( u, beds.get( u ) );
	}

	@Override
	public void onEnable() {

		this.setUpConfig();

		pluginName = this.getName();

		new File( "plugins" + File.separator + "BSPSpawnBed" + File.separator + "db" ).mkdir();

		for ( final World w : Bukkit.getWorlds() )
			beds.put( w.getUID(), DiscUtil.loadFromDisc( w ) );

		final PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents( new PlayerListener(), this );
		pm.registerEvents( new BlockListener(), this );
	}

	private void setUpConfig() {
		config = this.getConfig();

		config.set( "overworld", config.getString( "overworld", "world" ) );

		final ArrayList<String> list = new ArrayList<String>();
		list.add( "world_nether" );
		list.add( "world_the_end" );

		config.set( "excludeWorlds", config.getList( "excludeWorlds", list ) );

		for ( final Object obj : config.getList( "excludeWorlds" ) )
			excludedWorlds.add( Bukkit.getWorld( (String) obj ).getUID() );

		this.saveConfig();
	}

}
