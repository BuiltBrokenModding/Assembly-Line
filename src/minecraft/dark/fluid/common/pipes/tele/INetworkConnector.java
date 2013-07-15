package dark.fluid.common.pipes.tele;

import dark.fluid.api.INetworkPipe;

/** Used by IFluidNetworkPart to signal this block is remotely connected to another network. It will
 * cause that network to seak out all other connected network and try to merge them */
public interface INetworkConnector extends INetworkPipe
{
	/** gets the pipes frequency */
	public int getFrequency();

	public void setFrequency(int id);

	/** gets the pipes owner */
	public String getOwner();

	public void setOwner(String username);
}
