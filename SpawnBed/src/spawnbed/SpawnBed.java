package spawnbed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import spawnbed.listeners.BlockListener;
import spawnbed.listeners.PlayerListener;
import spawnbed.utils.DiscUtil;

public class SpawnBed extends JavaPlugin{
	
	public static final Logger log = Logger.getLogger("Minecraft");
	
	/// Map<WorldName, Map<PlayerName, BedHead>>
	public static Map<UUID, Map<UUID, BedHead>> beds =  new HashMap<UUID, Map<UUID, BedHead>>();
	public static ArrayList<UUID> excludedWorlds = new ArrayList<UUID>();
	public static FileConfiguration config;

	
	@Override
	public void onEnable() {
		
		setUpConfig();
		
		new File("plugins" + File.separator + "SpawnBed" + File.separator + "db").mkdir();
		
		for(World w : Bukkit.getWorlds())
		{
			beds.put(w.getUID(), DiscUtil.loadFromDisc(w));
		}
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new BlockListener(), this);
	}
	
	public void onDisable(){
		for(UUID u : beds.keySet())
		{
			DiscUtil.saveToDisc(u, beds.get(u));
		}
	}
	
	
	private void setUpConfig()
	{
		config = this.getConfig();
		
		config.set("overworld", config.getString("overworld", "world"));
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("world_nether");
		list.add("world_the_end");
		
		config.set("excludeWorlds", config.getList("excludeWorlds", list));
		
		for(Object obj : config.getList("excludeWorlds"))
		{
			excludedWorlds.add( Bukkit.getWorld((String) obj).getUID() );
		}
		
		
		this.saveConfig();
	}
	

}
