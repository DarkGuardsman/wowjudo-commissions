package com.builtbroken.wowjudo.content.crafting;

import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.prefab.inventory.InventoryIterator;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import com.builtbroken.wowjudo.SurvivalMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2017.
 */
public class BlockCraftingTable extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    public IIcon top_icon;

    public BlockCraftingTable()
    {
        super(Material.wood);
        setCreativeTab(SurvivalMod.creativeTab);
        setBlockTextureName(SurvivalMod.PREFX + "crafting_side");
        setBlockName(SurvivalMod.PREFX + "craftingTable");
        setHardness(1);
        setResistance(1);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        super.registerBlockIcons(reg);
        this.top_icon = reg.registerIcon(SurvivalMod.PREFX + "crafting_top");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (side == 1)
        {
            return top_icon;
        }
        return this.blockIcon;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 2 || meta > 5)
        {
            InventoryUtility.dropBlockAsItem(world, x, y, z, true);
            return;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityCraftingTable)
        {
            MultiBlockHelper.buildMultiBlock(world, (IMultiTileHost) tile, true, true);
        }
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
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityCraftingTable)
        {
            InventoryIterator it = new InventoryIterator(((TileEntityCraftingTable) tile).getInventory(), true);
            while (it.hasNext())
            {
                InventoryUtility.dropItemStack(world, x + 0.5, y + 0.5, z + 0.5, it.next(), 0, 0);
                it.remove();
            }
            MultiBlockHelper.destroyMultiBlockStructure((IMultiTileHost) tile, true, true, false);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityCraftingTable();
    }
}
