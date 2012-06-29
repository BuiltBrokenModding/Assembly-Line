package net.minecraft.src;
import net.minecraft.src.eui.steam.RenderPipeWater;
import net.minecraft.src.forge.*;
import net.minecraft.src.universalelectricity.UEBlockRenderer;
import net.minecraft.src.universalelectricity.UniversalElectricity;
import net.minecraft.src.universalelectricity.components.UniversalComponents;
public class mod_UtilityBlocks extends NetworkMod {

	public static Block totum = new net.minecraft.src.Utility.BlockTotum(210).setBlockName("totum");
	@Override
	public String getVersion() {
		// TODO change each update
		return "V0.0.1";
	}

	@Override
	public void load() {
		UniversalElectricity.registerAddon(this, "0.3.1");
		ModLoader.registerBlock(totum,net.minecraft.src.eui.ItemMachine.class);
		ModLoader.registerTileEntity(net.minecraft.src.Utility.TileEntityHealer.class, "healer", new UEBlockRenderer());
		ModLoader.addShapelessRecipe(new ItemStack(totum, 1,0), new Object[] { new ItemStack(Block.dirt,1)});
	}

}
