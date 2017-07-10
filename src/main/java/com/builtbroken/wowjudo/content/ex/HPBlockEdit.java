package com.builtbroken.wowjudo.content.ex;

import com.builtbroken.mc.api.edit.BlockEditResult;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IBlastEdit;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.wowjudo.content.wall.TileNodeWall;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Used to update HP of the wall tiles in the main thread
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2017.
 */
public class HPBlockEdit implements IBlastEdit
{
    World world;
    int x, y, z;
    float hp;

    public HPBlockEdit(World world, int xi, int yi, int zi, float lostHP)
    {
        this.world = world;
        this.x = xi;
        this.y = yi;
        this.z = zi;
        this.hp = lostHP;
    }

    @Override
    public double x()
    {
        return x;
    }

    @Override
    public double y()
    {
        return y;
    }

    @Override
    public double z()
    {
        return z;
    }

    @Override
    public void doDrops()
    {

    }

    @Override
    public void setBlastDirection(EnumFacing dir)
    {

    }

    @Override
    public EnumFacing getBlastDirection()
    {
        return null;
    }

    @Override
    public void setEnergy(float energy)
    {

    }

    @Override
    public float getEnergy()
    {
        return 0;
    }

    @Override
    public World world()
    {
        return world;
    }

    @Override
    public boolean hasChanged()
    {
        return true;
    }

    @Override
    public BlockEditResult place()
    {
        TileEntity tile = getTileEntity();
        if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileNodeWall)
        {
            ((TileNodeWall) ((ITileNodeHost) tile).getTileNode()).reduceHP(hp);
        }
        return null;
    }

    @Override
    public AxisAlignedBB getBounds()
    {
        return Cube.FULL.toAABB(); //TODO fix
    }

    @Override
    public Block getNewBlock()
    {
        return Blocks.air;
    }

    @Override
    public int getNewMeta()
    {
        return 0;
    }

    @Override
    public Block getBlock()
    {
        return world.getBlock(x, y, z);
    }

    @Override
    public int getBlockMetadata()
    {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public TileEntity getTileEntity()
    {
        return world.getTileEntity(x, y, z);
    }

    @Override
    public IWorldEdit logPrevBlock()
    {
        return this;
    }

    @Override
    public IWorldEdit set(Block newBlock, int newMeta)
    {
        return this;
    }
}
