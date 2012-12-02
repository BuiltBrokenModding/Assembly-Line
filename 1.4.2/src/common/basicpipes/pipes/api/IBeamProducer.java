package basicpipes.pipes.api;

import net.minecraftforge.common.ForgeDirection;

public interface IBeamProducer {
	
		/**
		 * onProduceLiquid  
		 * block.
		 * @param type - the type of liquid 
		 * @param regInt - requested beam intensity 
		 * @param side - The side 
		 * @return New Beam - Return a vol of liquid type that is produced
		 */
		public int createNewBeam(int type, int reqInt, ForgeDirection side);
		/**
		 * canProduceLiquid  
		 * block.
		 * @param type - the type of liquid 
		 * @param side - The side 
		 * @return boolean - True if can, false if can't produce liquid of type or on that side
		 * Also used for connection rules of pipes'
		 */
		public boolean canCreateBeam(int type, ForgeDirection side);
	
}
