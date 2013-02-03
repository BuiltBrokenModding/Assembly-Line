package assemblyline.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector3;
import assemblyline.client.model.ModelArmbot;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.armbot.TileEntityArmbot;

public class RenderArmbot extends TileEntitySpecialRenderer
{
	public static final ModelArmbot MODEL = new ModelArmbot();
	public static final String TEXTURE = "armbot.png";

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float var8)
	{
		if (tileEntity instanceof TileEntityArmbot)
		{
			String cmdText = ((TileEntityArmbot) tileEntity).getCommandDisplayText();
			if (cmdText != null && !cmdText.isEmpty())
			{
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				MovingObjectPosition objectPosition = player.rayTrace(8, 1);

				if (objectPosition != null)
				{
					if (objectPosition.blockX == tileEntity.xCoord && (objectPosition.blockY == tileEntity.yCoord || objectPosition.blockY == tileEntity.yCoord + 1) && objectPosition.blockZ == tileEntity.zCoord)
					{
						RenderHelper.renderFloatingText(cmdText, (float) x + 0.5f, ((float) y) + 0.25f, (float) z + 0.5f, 0xFFFFFF);
					}
				}
			}

			this.bindTextureByName(AssemblyLine.TEXTURE_PATH + TEXTURE);
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
			GL11.glScalef(1.0F, -1F, -1F);

			MODEL.render(0.0625f, ((TileEntityArmbot) tileEntity).renderYaw, ((TileEntityArmbot) tileEntity).renderPitch);

			// debug render
			/*
			 * GL11.glEnable(GL11.GL_BLEND); GL11.glEnable(GL11.GL_ALPHA_TEST);
			 * GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); GL11.glColor4f(1f,
			 * 1f, 1f, 0.25f); MODEL.render(0.0625f, ((TileEntityArmbot) tileEntity).rotationYaw,
			 * ((TileEntityArmbot) tileEntity).rotationPitch); GL11.glColor4f(1f, 1f, 1f, 1f);
			 */

			Vector3 handPos = ((TileEntityArmbot) tileEntity).getHandPosition();
			handPos.subtract(new Vector3(tileEntity));
			GL11.glPushMatrix();
			GL11.glRotatef(180, 0, 0, 1);

			for (ItemStack itemStack : ((TileEntityArmbot) tileEntity).getGrabbedItems())
			{
				// Items don't move right, so we render them manually
				if (itemStack != null)
				{
					RenderItem renderItem = ((RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class));
					RenderEngine renderEngine = Minecraft.getMinecraft().renderEngine;
					renderItem.renderItemIntoGUI(this.getFontRenderer(), renderEngine, itemStack, 0, 0);

				}
			}
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
	}

}