package eu.blackspectrum.spawnbed.entity;

import org.json.simple.JSONArray;

public class Chunk
{

	private int	x, y;

	public Chunk() {
	}

	public Chunk(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public void fromArray( final JSONArray list ) {
		this.x = (int) list.get( 0 );
		this.y = (int) list.get( 1 );
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	@SuppressWarnings("unchecked")
	public JSONArray toArray() {
		final JSONArray list = new JSONArray();
		list.add( this.x );
		list.add( this.y );
		return list;
	}

}
