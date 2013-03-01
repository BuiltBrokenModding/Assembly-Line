package hydraulic.core.path;

import hydraulic.core.implement.ILiquidConnectionProvider;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

/**
 * A class that allows flexible path finding in Minecraft Blocks.
 * 
 * @author Calclavia
 * 
 */
public class Pathfinder
{
	public interface IPathCallBack
	{
		/**
		 * Is this a valid node to search for?
		 * 
		 * @return
		 */
		public boolean isValidNode(Pathfinder finder, ForgeDirection direction, ILiquidConnectionProvider provider, ILiquidConnectionProvider node);

		/**
		 * Called when looping through nodes.
		 * 
		 * @param finder
		 * @param provider
		 * @return True to stop the path finding operation.
		 */
		public boolean onSearch(Pathfinder finder, ILiquidConnectionProvider provider);
	}

	/**
	 * A pathfinding call back interface used to call back on paths.
	 */
	public IPathCallBack callBackCheck;

	/**
	 * A list of nodes that the pathfinder went through.
	 */
	public List<ILiquidConnectionProvider> iteratedNodes;

	/**
	 * The results and findings found by the pathfinder.
	 */
	public List results;

	public Pathfinder(IPathCallBack callBack)
	{
		this.callBackCheck = callBack;
		this.clear();
	}

	public boolean findNodes(ILiquidConnectionProvider provider)
	{
		TileEntity[] connectedBlocks = provider.getAdjacentConnections();

		this.iteratedNodes.add(provider);

		if (this.callBackCheck.onSearch(this, provider))
		{
			return false;
		}

		for (int i = 0; i < connectedBlocks.length; i++)
		{
			TileEntity connectedBlock = connectedBlocks[i];

			if (connectedBlock instanceof ILiquidConnectionProvider)
			{
				if (!iteratedNodes.contains(connectedBlock))
				{
					if (this.callBackCheck.isValidNode(this, ForgeDirection.getOrientation(i), provider, (ILiquidConnectionProvider) connectedBlock))
					{
						if (!this.findNodes((ILiquidConnectionProvider) connectedBlock))
						{
							return false;
						}

					}
				}
			}
		}

		return true;
	}

	/**
	 * Called to execute the pathfinding operation.
	 */
	public Pathfinder init(ILiquidConnectionProvider provider)
	{
		this.findNodes(provider);
		return this;
	}

	public Pathfinder clear()
	{
		this.iteratedNodes = new ArrayList<ILiquidConnectionProvider>();
		this.results = new ArrayList();
		return this;
	}
}
