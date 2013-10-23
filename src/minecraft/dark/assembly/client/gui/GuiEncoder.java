package dark.assembly.client.gui;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import universalelectricity.prefab.TranslationHelper;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.machine.encoder.ContainerEncoder;
import dark.assembly.common.machine.encoder.IInventoryWatcher;
import dark.assembly.common.machine.encoder.ItemDisk;
import dark.assembly.common.machine.encoder.TileEntityEncoder;
import dark.core.common.DarkMain;
import dark.core.network.PacketHandler;
import dark.core.prefab.ModPrefab;

@SideOnly(Side.CLIENT)
public class GuiEncoder extends GuiContainer implements IInventoryWatcher
{
    private static final int MAX_COMMANDS = 6;

    private int containerWidth;
    private int containerHeight;
    private TileEntityEncoder tileEntity;
    private ArrayList<String> commands;

    // list stuff
    private int minCommand;
    private int selCommand;

    private GuiButton encodeButton;
    private GuiButton clearButton;

    public GuiEncoder(InventoryPlayer playerInventory, TileEntityEncoder tileEntity)
    {
        super(new ContainerEncoder(playerInventory, tileEntity));
        this.ySize = 237;
        this.tileEntity = tileEntity;
        tileEntity.setWatcher(this);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.allowUserInput = true;
        Keyboard.enableRepeatEvents(true);

        this.containerWidth = (this.width - this.xSize) / 2;
        this.containerHeight = (this.height - this.ySize) / 2;

        this.encodeButton = new GuiButton(0, containerWidth + (xSize - 25), containerHeight + 128 + ContainerEncoder.Y_OFFSET, 18, 20, "Code");
        this.clearButton = new GuiButton(1, containerWidth + (xSize - 43), containerHeight + 128 + ContainerEncoder.Y_OFFSET, 18, 20, "Erase");

        this.buttonList.add(encodeButton);
        this.buttonList.add(clearButton);

        this.commands = new ArrayList<String>();
        this.minCommand = 0;
        this.selCommand = -1;
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {

    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        glColor4f(1, 1, 1, 1);
        glDisable(GL_LIGHTING);
        this.fontRenderer.drawString(TranslationHelper.getLocal("tile.encoder.name"), 68, 8 + ContainerEncoder.Y_OFFSET, 4210752);
        this.fontRenderer.drawString("Disk:", 56, 28 + ContainerEncoder.Y_OFFSET, 4210752);
    }

    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        super.mouseClicked(x, y, button);

    }

    @Override
    protected void keyTyped(char character, int keycode)
    {
        if (character != 'e' && character != 'E')
        {
            super.keyTyped(character, keycode);
        }

        if (keycode == Keyboard.KEY_ESCAPE)
        {
            this.mc.thePlayer.closeScreen();
        }
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        this.mc.renderEngine.bindTexture(new ResourceLocation(AssemblyLine.instance.DOMAIN, ModPrefab.GUI_DIRECTORY + "gui_encoder.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(containerWidth, containerHeight + ContainerEncoder.Y_OFFSET, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void inventoryChanged()
    {
        // TODO Auto-generated method stub

    }
}
