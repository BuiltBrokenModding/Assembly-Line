package basicpipes.pipes.api;


import net.minecraft.src.Block;
/**
 * System too easily reference a liquid type and its info
 * @author Rseifert
 *
 */
public enum Liquid {
	// -1 == null || unused
	STEAM("Steam",false,true,-1,-1),
	WATER("Water",false,false,Block.waterStill.blockID,Block.waterMoving.blockID),	
	LAVA("Lava",false,false,Block.lavaStill.blockID,Block.lavaMoving.blockID),
	OIL("Oil",true,false,-1,-1),//BasicComponents.oilStill.blockID,BasicComponents.oilMoving.blockID),
	Fuel("Fuel",true,false,-1,-1),	
	Air("Air",false,true,0,-1),	
	Methain("Methain",true,true,-1,-1),
	BioFuel("BioFuel",true,false,-1,-1),
	Coolent("Coolent",false,false,-1,-1),
	NukeWaste("NukeWaste",false,false,-1,-1),
	Ether("Ether",false,false,-1,-1),
	DEFUALT("Empty",false,false,-1,-1);
 	public final boolean flamable;//can it catch on fire, not used but might be
    public final boolean isGas;//is it a gas, used to find if it floats
    public final int Still;//if there is a block of still liquid linked to this
    public final int Moving;//if there is a block of moving liquid linked to this
    public final String lName;
private Liquid(String name,boolean flame,boolean gas,int block, int Moving)
{
	this.flamable = flame;
	this.isGas = gas;
	this.Still = block;
	this.Moving = Moving;
	this.lName = name;
}
/**
 * Only use this if you are converting from the old system 
 * Or have a special need for it
 * @param id of liquid
 * @return Liquid Object
 */
	 public static Liquid getLiquid(int id)
	    {
	        if (id >= 0 && id < Liquid.values().length)
	        {
	            return Liquid.values()[id];
	        }
	        return DEFUALT;
	    }
public static Liquid getLiquidByBlock(int bBlock) {
	for(int i = 0; i < Liquid.values().length;i++)
	{
		Liquid selected = Liquid.getLiquid(i);
		if(bBlock == selected.Still)
		{
			return selected;
		}
	}
	return Liquid.DEFUALT;
}
}
