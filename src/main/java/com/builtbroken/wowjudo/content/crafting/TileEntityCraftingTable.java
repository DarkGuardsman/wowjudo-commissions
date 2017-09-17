package com.builtbroken.wowjudo.content.crafting;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.abstraction.world.IWorld;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.client.IIconCallBack;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.handler.TileTaskTickHandler;
import com.builtbroken.mc.framework.multiblock.EnumMultiblock;
import com.builtbroken.mc.framework.multiblock.MultiBlockHelper;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.tile.entity.TileEntityInv;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.crafting.gui.ContainerCraftingTable;
import com.builtbroken.wowjudo.content.crafting.gui.GuiCraftingTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2017.
 */
public class TileEntityCraftingTable extends TileEntityInv<ExternalInventory> implements IGuiTile, IMultiTileHost, IIconCallBack
{
    public static final int SLOT_CRAFTING_START = 0;
    public static final int SLOT_CRAFTING_END = 15;
    public static final int SLOT_INVENTORY_START = SLOT_CRAFTING_END + 1;
    public static final int SLOT_INVENTORY_END = SLOT_INVENTORY_START + 18;

    private static final HashMap[] STR_MAPS = new HashMap[4];
    protected boolean _destroyingStructure = false;

    private boolean init = false;

    static
    {
        HashMap<IPos3D, String> map = new HashMap();
        map.put(new Pos(1, 0, 0), EnumMultiblock.INVENTORY.getTileName());
        STR_MAPS[0] = map;

        map = new HashMap();
        map.put(new Pos(-1, 0, 0), EnumMultiblock.INVENTORY.getTileName());
        STR_MAPS[1] = map;

        map = new HashMap();
        map.put(new Pos(0, 0, -1), EnumMultiblock.INVENTORY.getTileName());
        STR_MAPS[2] = map;

        map = new HashMap();
        map.put(new Pos(0, 0, 1), EnumMultiblock.INVENTORY.getTileName());
        STR_MAPS[3] = map;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!init)
        {
            init = true;
            MultiBlockHelper.buildMultiBlock(worldObj, this, true, true);
            TileTaskTickHandler.INSTANCE.addTileToBeRemoved(this); //Removes from tick list
        }
    }

    @Override
    public boolean canUpdate()
    {
        return !init; //Only ticks once
    }

    @Override
    protected ExternalInventory createInventory()
    {
        return new ExternalInventory(this, SLOT_INVENTORY_END + 1);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerCraftingTable(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiCraftingTable(player, this);
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {
        if (tileMulti instanceof TileEntity)
        {
            if (getLayoutOfMultiBlock().containsKey(toPos().sub(new Pos((TileEntity) tileMulti))))
            {
                tileMulti.setHost(this);
            }
        }
    }

    @Override
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (!_destroyingStructure && tileMulti instanceof TileEntity)
        {
            Pos pos = new Pos((TileEntity) tileMulti).sub(toPos());

            if (getLayoutOfMultiBlock().containsKey(pos))
            {
                MultiBlockHelper.destroyMultiBlockStructure(this, harvest, false, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
    {

    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, float xHit, float yHit, float zHit)
    {
        if (!worldObj.isRemote)
        {
            player.openGui(SurvivalMod.instance, 0, worldObj, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {

    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        int meta = getBlockMetadata();
        if (meta > 1)
        {
            return STR_MAPS[meta - 2];
        }
        return new HashMap();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconForSide(IBlockAccess world, int x, int y, int z, int side)
    {
        return SurvivalMod.blockCraftingTable.getIcon(side, world.getBlockMetadata(x, y, z));
    }

    @Override
    public IWorld world()
    {
        return Engine.minecraft.getWorld(worldObj.provider.dimensionId);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 1, zCoord + 2);
    }
}
