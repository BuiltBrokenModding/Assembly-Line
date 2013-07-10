package assemblyline.common;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import assemblyline.common.imprinter.ContainerImprinter;
import assemblyline.common.imprinter.TileEntityImprinter;
import assemblyline.common.machine.encoder.ContainerEncoder;
import assemblyline.common.machine.encoder.TileEntityEncoder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{
	public static final int GUI_IMPRINTER = 1;
	public static final int GUI_ENCODER = 2;

	public void preInit()
	{

	}

	public void init()
	{

	}

	private void extractZipToLocation(File zipFile, String sourceFolder, String destFolder)
	{
		try
		{

			File destFile = new File(FMLCommonHandler.instance().getMinecraftServerInstance().getFile("."), destFolder);
			String destinationName = destFile.getAbsolutePath();
			byte[] buf = new byte[1024];
			ZipInputStream zipinputstream = null;
			ZipEntry zipentry;
			zipinputstream = new ZipInputStream(new FileInputStream(zipFile));

			zipentry = zipinputstream.getNextEntry();
			while (zipentry != null)
			{
				// for each entry to be extracted
				String zipentryName = zipentry.getName();
				if (!zipentryName.startsWith(sourceFolder))
				{
					zipentry = zipinputstream.getNextEntry();
					continue;
				}

				String entryName = destinationName + zipentryName.substring(Math.min(zipentryName.length(), sourceFolder.length() - 1));
				entryName = entryName.replace('/', File.separatorChar);
				entryName = entryName.replace('\\', File.separatorChar);
				int n;
				FileOutputStream fileoutputstream;
				File newFile = new File(entryName);
				if (zipentry.isDirectory())
				{
					if (!newFile.mkdirs())
					{
						break;
					}
					zipentry = zipinputstream.getNextEntry();
					continue;
				}

				fileoutputstream = new FileOutputStream(entryName);

				while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
				{
					fileoutputstream.write(buf, 0, n);
				}

				fileoutputstream.close();
				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();

			}// while

			zipinputstream.close();
		}
		catch (Exception e)
		{
			System.out.println("Error while loading AssemblyLine Lua libraries: ");
			e.printStackTrace();
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			switch (ID)
			{
				case GUI_IMPRINTER:
					return new ContainerImprinter(player.inventory, (TileEntityImprinter) tileEntity);
				case GUI_ENCODER:
				{
					if (tileEntity != null && tileEntity instanceof TileEntityEncoder)
						return new ContainerEncoder(player.inventory, (TileEntityEncoder) tileEntity);
				}
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	public boolean isCtrKeyDown()
	{
		return false;
	}

	/** Renders a laser beam from one power to another by a set color for a set time
	 * 
	 * @param world - world this laser is to be rendered in
	 * @param position - start vector3
	 * @param target - end vector3
	 * @param color - color of the beam
	 * @param age - life of the beam in 1/20 secs */
	public void renderBeam(World world, Vector3 position, Vector3 target, Color color, int age)
	{
	}
}
