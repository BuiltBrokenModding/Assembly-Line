package dark.BasicUtilities.api;

import net.minecraftforge.common.ForgeDirection;

public class Beam {
	//might need a more complex system for this later but for now this will work
	 	public int intensity; //Beam intensity level
	    public boolean light; //Can prodcue light, might use this later
	    public ForgeDirection movDir; //Used to find the beams current direction
	  public Beam()
	    {
	    	this(0,false,ForgeDirection.UNKNOWN);
	    }
	 Beam(int i, boolean light, ForgeDirection dir)
    {
		intensity = i;
		this.light = light;
		movDir = dir;
    }
	 public static int getBeamLevel(Beam beam)
	 {
		 return beam.intensity;
	 }
    	
    	 
}
