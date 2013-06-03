package dark.fluid.common.telepipe;

import hydraulic.fluidnetwork.IFluidNetworkPart;

import java.util.List;

/**
 * Used by IFluidNetworkPart to signal this block is remotely connected to another network. It will
 * cause that network to seak out all other connected network and try to merge them
 * 
 */
public interface INetworkConnector extends IFluidNetworkPart
{
	/**
	 * gets the pipes frequency
	 */
	public int getFrequency();

	public void setFrequency(int id);

	/**
	 * gets the pipes owner
	 */
	public String getOwner();

	public void setOwner(String username);

	/**
	 * gets a list off all INetworkConnector this pipe shares frequency and owner with
	 */
	public List<INetworkConnector> getConnectedParts();
}
