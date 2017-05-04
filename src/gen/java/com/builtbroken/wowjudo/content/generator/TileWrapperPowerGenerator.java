//=======================================================
//DISCLAIMER: THIS IS A GENERATED CLASS FILE
//THUS IS PROVIDED 'AS-IS' WITH NO WARRANTY
//FUNCTIONALITY CAN NOT BE GUARANTIED IN ANY WAY 
//USE AT YOUR OWN RISK 
//-------------------------------------------------------
//Built on: Rober
//=======================================================
package com.builtbroken.wowjudo.content.generator;

import com.builtbroken.wowjudo.content.generator.TilePowerGenerator;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.tile.listeners.IBlockListener;
import com.builtbroken.mc.api.tile.listeners.ITileEventListener;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.framework.block.BlockBase;
import com.builtbroken.mc.framework.logic.wrapper.TileEntityWrapper;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;
import java.util.List;
import com.builtbroken.mc.api.tile.provider.ITankProvider;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.codegen.annotations.ExternalInventoryWrapped;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class TileWrapperPowerGenerator extends TileEntityWrapper implements IMultiTileHost, IFluidHandler, IInventoryProvider, ISidedInventory
{
	public TileWrapperPowerGenerator()
	{
		super(new TilePowerGenerator());
	}

	//============================
	//==Methods:MultiBlockWrapped
	//============================


    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {
        if (getTileNode() instanceof IMultiTileHost)
        {
            ((IMultiTileHost) getTileNode()).onMultiTileAdded(tileMulti);
        }
        for (List<ITileEventListener> list : new List[]{getListeners("multiblock"), ((BlockBase) getBlockType()).listeners.get("multiblock")})
        {
            if (list != null && !list.isEmpty())
            {
                for (ITileEventListener listener : list)
                {
                    if (listener instanceof IMultiTileHost)
                    {
                        if(listener instanceof IBlockListener)
                        {
                            ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                        }
                        ((IMultiTileHost) listener).onMultiTileAdded(tileMulti);
                    }
                }
            }
        }
    }

    @Override
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (getTileNode() instanceof IMultiTileHost)
        {
            return ((IMultiTileHost) getTileNode()).onMultiTileBroken(tileMulti, source, harvest);
        }
        for (List<ITileEventListener> list : new List[]{getListeners("multiblock"), ((BlockBase) getBlockType()).listeners.get("multiblock")})
        {
            if (list != null && !list.isEmpty())
            {
                boolean b = false;
                for (ITileEventListener listener : list)
                {
                    if (listener instanceof IMultiTileHost)
                    {
                        if(listener instanceof IBlockListener)
                        {
                            ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                        }
                        if (((IMultiTileHost) listener).onMultiTileBroken(tileMulti, source, harvest))
                        {
                            b = true;
                        }
                    }
                }
                return b;
            }
        }
        return false;
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
    {
        if (getTileNode() instanceof IMultiTileHost)
        {
            ((IMultiTileHost) getTileNode()).onTileInvalidate(tileMulti);
        }
        for (List<ITileEventListener> list : new List[]{getListeners("multiblock"), ((BlockBase) getBlockType()).listeners.get("multiblock")})
        {
            if (list != null && !list.isEmpty())
            {
                for (ITileEventListener listener : list)
                {
                    if (listener instanceof IMultiTileHost)
                    {
                        if(listener instanceof IBlockListener)
                        {
                            ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                        }
                        ((IMultiTileHost) listener).onTileInvalidate(tileMulti);
                    }
                }
            }
        }
    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, float xHit, float yHit, float zHit)
    {
        boolean b = false;
        if (getTileNode() instanceof IMultiTileHost)
        {
            b = ((IMultiTileHost) getTileNode()).onMultiTileActivated(tile, player, side, xHit, yHit, zHit);
        }
        for (List<ITileEventListener> list : new List[]{getListeners("multiblock"), ((BlockBase) getBlockType()).listeners.get("multiblock")})
        {
            if (list != null && !list.isEmpty())
            {
                for (ITileEventListener listener : list)
                {
                    if (listener instanceof IMultiTileHost)
                    {
                        if(listener instanceof IBlockListener)
                        {
                            ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                        }
                        if (((IMultiTileHost) listener).onMultiTileActivated(tile, player, side, xHit, yHit, zHit))
                        {
                            b = true;
                        }
                    }
                }
            }
        }
        return b;
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {
        if (getTileNode() instanceof IMultiTileHost)
        {
            ((IMultiTileHost) getTileNode()).onMultiTileClicked(tile, player);
        }
        for (List<ITileEventListener> list : new List[]{getListeners("multiblock"), ((BlockBase) getBlockType()).listeners.get("multiblock")})
        {
            if (list != null && !list.isEmpty())
            {
                for (ITileEventListener listener : list)
                {
                    if (listener instanceof IMultiTileHost)
                    {
                        if(listener instanceof IBlockListener)
                        {
                            ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                        }
                        ((IMultiTileHost) listener).onMultiTileClicked(tile, player);
                    }
                }
            }
        }
    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        if (getTileNode() instanceof IMultiTileHost)
        {
            HashMap<IPos3D, String> map = ((IMultiTileHost) getTileNode()).getLayoutOfMultiBlock();
            if (map != null && !map.isEmpty())
            {
                return map;
            }
        }
        for (List<ITileEventListener> list : new List[]{getListeners("multiblock"), ((BlockBase) getBlockType()).listeners.get("multiblock")})
        {
            if (list != null && !list.isEmpty())
            {
                for (ITileEventListener listener : list)
                {
                    if (listener instanceof IMultiTileHost)
                    {
                        if(listener instanceof IBlockListener)
                        {
                            ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                        }
                        HashMap<IPos3D, String> map = ((IMultiTileHost) listener).getLayoutOfMultiBlock();
                        if (map != null && !map.isEmpty())
                        {
                            return map;
                        }
                    }
                }
            }
        }
        return null;
    }
    
	//============================
	//==Methods:TankProviderWrapped
	//============================


    protected IFluidTank getFluidTank(ForgeDirection from, Fluid fluid)
    {
        if (getTileNode() instanceof ITankProvider)
        {
            return ((ITankProvider) getTileNode()).getTankForFluid(from, fluid);
        }
        return null;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (resource != null)
        {
            IFluidTank tank = getFluidTank(from, resource.getFluid());
            if (tank != null)
            {
                return tank.fill(resource, doFill);
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (resource != null)
        {
            IFluidTank tank = getFluidTank(from, resource.getFluid());
            if (tank != null && tank.getFluid() != null && tank.getFluid().getFluid() == resource.getFluid())
            {
                return tank.drain(resource.amount, doDrain);
            }
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        IFluidTank tank = getFluidTank(from, null);
        if (tank != null && tank.getFluid() != null)
        {
            return tank.drain(maxDrain, doDrain);
        }
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        if (getTileNode() instanceof ITankProvider)
        {
            return ((ITankProvider) getTileNode()).canFill(from, fluid);
        }
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        if (getTileNode() instanceof ITankProvider)
        {
            return ((ITankProvider) getTileNode()).canDrain(from, fluid);
        }
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[0];
    }
    
	//============================
	//==Methods:ExternalInventoryWrapped
	//============================


    @Override
    public IInventory getInventory()
    {
        if (tile instanceof IInventoryProvider)
        {
            return ((IInventoryProvider) tile).getInventory();
        }
        return null;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if (getInventory() instanceof ISidedInventory)
        {
            return ((ISidedInventory) getInventory()).getAccessibleSlotsFromSide(side);
        }
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_)
    {
        if (getInventory() instanceof ISidedInventory)
        {
            return ((ISidedInventory) getInventory()).canInsertItem(p_102007_1_, p_102007_2_, p_102007_3_);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_)
    {
        if (getInventory() instanceof ISidedInventory)
        {
            return ((ISidedInventory) getInventory()).canInsertItem(p_102008_1_, p_102008_2_, p_102008_3_);
        }
        return false;
    }

    @Override
    public int getSizeInventory()
    {
        if (getInventory() != null)
        {
            return getInventory().getSizeInventory();
        }
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (getInventory() != null)
        {
            return getInventory().getStackInSlot(slot);
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (getInventory() != null)
        {
            return getInventory().decrStackSize(slot, amount);
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (getInventory() != null)
        {
            return getInventory().getStackInSlotOnClosing(slot);
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (getInventory() != null)
        {
            getInventory().setInventorySlotContents(slot, stack);
        }
    }

    @Override
    public String getInventoryName()
    {
        if (getInventory() != null)
        {
            return getInventory().getInventoryName();
        }
        return "inventory";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        if (getInventory() != null)
        {
            return getInventory().hasCustomInventoryName();
        }
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        if (getInventory() != null)
        {
            return getInventory().getInventoryStackLimit();
        }
        return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        if (getInventory() != null)
        {
            return getInventory().isUseableByPlayer(player);
        }
        return false;
    }

    @Override
    public void openInventory()
    {
        if (getInventory() != null)
        {
            getInventory().openInventory();
        }
    }

    @Override
    public void closeInventory()
    {
        if (getInventory() != null)
        {
            getInventory().closeInventory();
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (getInventory() != null)
        {
            return getInventory().isItemValidForSlot(slot, stack);
        }
        return false;
    }
    
}