package com.builtbroken.wowjudo.content.crafting;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.client.IIconCallBack;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.tile.entity.TileEntityInv;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.crafting.gui.ContainerCraftingTable;
import com.builtbroken.wowjudo.content.crafting.gui.GuiCraftingTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

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

    static
    {
        for (ForgeDirection direction : new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST})
        {
            HashMap<IPos3D, String> map = new HashMap();
            map.put(new Pos(direction.offsetX, 0, direction.offsetZ), EnumMultiblock.INVENTORY.getTileName() + "#renderblock=true");
            STR_MAPS[direction.ordinal() - 2] = map;
        }
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
            if (getLayoutOfMultiBlock().containsKey(new Pos(this).sub(new Pos((TileEntity) tileMulti))))
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
            Pos pos = new Pos((TileEntity) tileMulti).sub(new Pos(this));

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
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, IPos3D hit)
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
}
