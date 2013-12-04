package dark.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraft.tileentity.TileEntity;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BlockTileEntityInfo
{
    /** Array of tile entities this tile uses. Is a parallel array in combo with tileEntitiesName */
    Class<? extends TileEntity>[] tileEntities() default {};

    /** Array of tile entities names that are used to register the tile entity. Is a parallel array
     * in combo with tileEntities */
    String[] tileEntitiesNames() default {};
}
