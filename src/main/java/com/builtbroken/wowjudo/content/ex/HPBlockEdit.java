package com.builtbroken.wowjudo.content.ex;

import com.builtbroken.mc.api.edit.BlockEditResult;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IBlastEdit;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.data.Direction;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.wowjudo.content.wall.TileNodeWall;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Used to update HP of the wall tiles in the main thread
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2017.
 */
public class HPBlockEdit implements IBlastEdit
{
    public final World world;
    public final BlockPos pos;
    public final float hp;

    public HPBlockEdit(World world, BlockPos pos, float lostHP)
    {
        this.world = world;
        this.pos = pos;
        this.hp = lostHP;
    }

    @Override
    public double x()
    {
        return pos.xi();
    }

    @Override
    public double y()
    {
        return pos.yi();
    }

    @Override
    public double z()
    {
        return pos.zi();
    }

    @Override
    public void doDrops()
    {

    }

    @Override
    public void setBlastDirection(Direction dir)
    {

    }

    @Override
    public Direction getBlastDirection()
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
    public World oldWorld()
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
        return pos.getBlock(world);
    }

    @Override
    public int getBlockMetadata()
    {
        return pos.getBlockMetadata(world);
    }

    @Override
    public TileEntity getTileEntity()
    {
        return pos.getTileEntity(world);
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

    @Override
    public int hashCode()
    {
        int result = 17;
        result = 31 * result + (oldWorld() != null && oldWorld().provider != null ? oldWorld().provider.dimensionId : 0);
        result = 31 * result + xi();
        result = 31 * result + yi();
        result = 31 * result + zi();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof IBlastEdit)
        {
            return ((IBlastEdit) obj).oldWorld() == world
                    && ((IBlastEdit) obj).xi() == xi()
                    && ((IBlastEdit) obj).yi() == yi()
                    && ((IBlastEdit) obj).zi() == zi();
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "HPBlockEdit[" + world.provider.dimensionId + ", " + pos + ", " + hp + "]";
    }
}
