package dark.assembly.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import dark.assembly.common.imprinter.ContainerImprinter;
import dark.assembly.common.imprinter.TileEntityImprinter;
import dark.assembly.common.machine.encoder.ContainerEncoder;
import dark.assembly.common.machine.encoder.TileEntityEncoder;
import dark.assembly.common.machine.processor.ContainerProcessor;
import dark.assembly.common.machine.processor.TileEntityProcessor;

public class CommonProxy implements IGuiHandler
{
    public static final int GUI_IMPRINTER = 1;
    public static final int GUI_ENCODER = 2;
    public static final int GUI_PROCESSOR = 3;

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
                case GUI_PROCESSOR:
                {
                    return new ContainerProcessor(player.inventory, (TileEntityProcessor) tileEntity);
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
}
