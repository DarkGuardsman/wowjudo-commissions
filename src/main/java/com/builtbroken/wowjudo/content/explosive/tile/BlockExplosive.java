package com.builtbroken.wowjudo.content.explosive.tile;

import com.builtbroken.wowjudo.SurvivalMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class BlockExplosive extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    public IIcon top;

    @SideOnly(Side.CLIENT)
    public IIcon bottom;

    @SideOnly(Side.CLIENT)
    public IIcon det;

    public BlockExplosive()
    {
        super(Material.tnt);
        setCreativeTab(SurvivalMod.creativeTab);
        setBlockName(SurvivalMod.PREFX + "c4");
        setHardness(5);
        setResistance(5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(SurvivalMod.PREFX + "C4_block_side");
        this.top = reg.registerIcon(SurvivalMod.PREFX + "C4_block_topBottom");
        this.bottom = reg.registerIcon(SurvivalMod.PREFX + "C4_block_topDet");
        this.det = reg.registerIcon(SurvivalMod.PREFX + "C4_block_det");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (meta > 5)
        {
            return det;
        }
        if (side == meta || ForgeDirection.getOrientation(side).getOpposite().ordinal() == meta)
        {
            return top;
        }
        return this.blockIcon;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta)
        {
            case 0:
                return AxisAlignedBB.getBoundingBox(
                        x + ISBRExplosive.c4Size, y + 1 - ISBRExplosive.c4Height, z + ISBRExplosive.c4Size,
                        x + 1 - ISBRExplosive.c4Size, y + 1, z + 1 - ISBRExplosive.c4Size);
            case 1:
                return AxisAlignedBB.getBoundingBox(
                        x + ISBRExplosive.c4Size, y + ISBRExplosive.c4Height, z + ISBRExplosive.c4Size,
                        x + 1 - ISBRExplosive.c4Size, y, z + 1 - ISBRExplosive.c4Size);
            case 2:
                return AxisAlignedBB.getBoundingBox(
                        x + ISBRExplosive.c4Size, y + ISBRExplosive.c4Size, z + 1 - ISBRExplosive.c4Height,
                        x + 1 - ISBRExplosive.c4Size, y + 1 - ISBRExplosive.c4Size, z + 1);
            case 3:
                return AxisAlignedBB.getBoundingBox(
                        x + ISBRExplosive.c4Size, y + ISBRExplosive.c4Size, z,
                        x + 1 - ISBRExplosive.c4Size, y + 1 - ISBRExplosive.c4Size, z + ISBRExplosive.c4Height);
            case 4:
                return AxisAlignedBB.getBoundingBox(
                        x + 1 - ISBRExplosive.c4Height, y + ISBRExplosive.c4Size, z + ISBRExplosive.c4Size,
                        x + 1, y + 1 - ISBRExplosive.c4Size, z + 1 - ISBRExplosive.c4Size);
            case 5:
                return AxisAlignedBB.getBoundingBox(
                        x, y + ISBRExplosive.c4Size, z + ISBRExplosive.c4Size,
                        x + ISBRExplosive.c4Height, y + 1 - ISBRExplosive.c4Size, z + 1 - ISBRExplosive.c4Size);
        }
        return AxisAlignedBB.getBoundingBox((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
    {
        //TODO keep track of owner
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float xHit, float yHit, float zHit, int meta)
    {
        return side;
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

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
}
