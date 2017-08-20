package com.builtbroken.wowjudo.content.crafting;

import com.builtbroken.mc.lib.helper.BlockUtility;
import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/12/2017.
 */
public class ItemBlockCraftingTable extends ItemBlock
{
    public ItemBlockCraftingTable(Block block)
    {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        metadata = BlockUtility.determineRotation(player.rotationYaw);

        ForgeDirection direction = ForgeDirection.UNKNOWN;
        switch (metadata)
        {
            //North
            case 2:
                direction = ForgeDirection.EAST;
                break;
            //South
            case 3:
                direction = ForgeDirection.WEST;
                break;
            //West
            case 4:
                direction = ForgeDirection.NORTH;
                break;
            //East
            case 5:
                direction = ForgeDirection.SOUTH;
                break;
        }

        //Can't place if we do not have a second open space
        if(!new Pos(x, y, z).add(direction).isReplaceable(world))
        {
            return false;
        }

        if (!world.setBlock(x, y, z, field_150939_a, metadata, 3))
        {
            return false;
        }

        if (world.getBlock(x, y, z) == field_150939_a)
        {
            field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
            field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
        }
        return true;
    }
}
