package com.builtbroken.wowjudo.content.crafting;

import com.builtbroken.mc.prefab.inventory.InventoryIterator;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.campfire.TileEntityCampfire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2017.
 */
public class BlockCraftingTable extends BlockContainer
{
    public BlockCraftingTable()
    {
        super(Material.wood);
        setCreativeTab(SurvivalMod.creativeTab);
        setBlockTextureName(SurvivalMod.PREFX + "craftingTable");
        setBlockName(SurvivalMod.PREFX + "craftingTable");
        setHardness(1);
        setResistance(1);
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
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityCraftingTable();
    }
}
