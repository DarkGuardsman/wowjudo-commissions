package com.builtbroken.wowjudo.content.explosive.tile;

import com.builtbroken.wowjudo.JudoMod;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class BlockExplosive extends BlockContainer
{
    public BlockExplosive()
    {
        super(Material.tnt);
        setCreativeTab(JudoMod.creativeTab);
        setBlockName(JudoMod.PREFX + "c4");
        setHardness(5);
        setResistance(5);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityExplosive();
    }

    @Override
    public int getRenderType()
    {
        return ISBRExplosive.ID;
    }
}
