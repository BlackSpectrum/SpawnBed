package spawnbed.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import spawnbed.BedHead;

public class DiscUtil {
	
	
	private static final JSONParser parser = new JSONParser();
	
	
	public static void saveToDisc(final World w, final Map<UUID, BedHead> m)
	{		
		saveToDisc(w.getUID(), m);
	}
	
	@SuppressWarnings("unchecked")
	public static void saveToDisc(final UUID uuid, final Map<UUID, BedHead> m)
	{		
		
		if(m == null)
			return;
		try {
			JSONObject obj = new JSONObject();
			
			for(UUID uid : m.keySet())
			{
				obj.put(uid, m.get(uid).toArray());
			}

			
			File f = new File("plugins" + File.separator + "BSPSpawnBed" + File.separator + "db" + File.separator + uuid + ".json");
			if(!f.exists())
				f.createNewFile();
			
			FileWriter file = new FileWriter(f);
			file.write(obj.toJSONString());
			file.flush();
			file.close();
		
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static Map<UUID, BedHead> loadFromDisc(final World w)
	{
		JSONObject obj = null;
		try {
			
			///If no file exists for the world, return null
			File f = new File("plugins" + File.separator + "BSPSpawnBed" + File.separator + "db" + File.separator + w.getUID() + ".json");
			if(!f.exists())
				return null;
				
			
			///Else read the file
			obj = (JSONObject) parser.parse(new FileReader(f));
		
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(obj == null)
			return null;
		
		Map<UUID, BedHead> ret = new HashMap<UUID, BedHead>();
		
		for(Object o : obj.keySet())
		{
			ret.put(UUID.fromString((String) o), new BedHead((JSONArray) obj.get(o), w));
		}
		
		if(ret.size() > 0)
			return ret;
		else
			return null;
		
		
			
	}

}
