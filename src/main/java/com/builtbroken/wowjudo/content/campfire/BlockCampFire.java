package com.builtbroken.wowjudo.content.campfire;

import com.builtbroken.mc.prefab.inventory.InventoryIterator;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.explosive.tile.ISBRExplosive;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class BlockCampFire extends BlockContainer
{
    public BlockCampFire()
    {
        super(Material.rock);
        setCreativeTab(SurvivalMod.creativeTab);
        setBlockName(SurvivalMod.PREFX + "campFire");
        setHardness(1);
        setResistance(1);
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xf, float yf, float zf)
    {
        if (!world.isRemote)
        {
            player.openGui(SurvivalMod.instance, 0, world, x, y, z);
        }
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return AxisAlignedBB.getBoundingBox(
                x + ISBRExplosive.pixel, y + ISBRExplosive.pixel * 6, z + ISBRExplosive.pixel,
                x + 1 - ISBRExplosive.pixel, y, z + 1 - ISBRExplosive.pixel);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.cobblestone.getIcon(0, 0);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityCampfire)
        {
            InventoryIterator it = new InventoryIterator(((TileEntityCampfire) tile).getInventory(), true);
            while (it.hasNext())
            {
                InventoryUtility.dropItemStack(world, x + 0.5, y + 0.5, z + 0.5, it.next(), 0, 0);
                it.remove();
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityCampfire();
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
}
