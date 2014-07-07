package spawnbed;

import org.json.simple.JSONArray;

public class Chunk {
	
	private int x, y;
	
	public Chunk(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Chunk()
	{	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray toArray()
	{
		JSONArray list = new JSONArray();
		list.add(x);
		list.add(y);
		return list;
	}
	
	public void fromArray(JSONArray list)
	{
		x = (int) list.get(0);
		y = (int) list.get(1);
	}

}
