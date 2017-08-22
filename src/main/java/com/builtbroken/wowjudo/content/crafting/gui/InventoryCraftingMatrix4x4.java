package com.builtbroken.wowjudo.content.crafting.gui;

import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2017.
 */
public class InventoryCraftingMatrix4x4 extends InventoryCrafting implements IInventory
{
    public final TileEntityCraftingTable table;
    public final Container hostContainer;

    public InventoryCraftingMatrix4x4(Container container, TileEntityCraftingTable table)
    {
        super(container, 4, 4);
        this.hostContainer = container;
        this.table = table;
    }

    @Override
    public int getSizeInventory()
    {
        return 16;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return slot >= this.getSizeInventory() ? null : table.getStackInSlot(slot);
    }

    public ItemStack getStackInRowAndColumn(int row, int col)
    {
        if (row >= 0 && row < 4)
        {
            int k = row + col * 4;
            return this.getStackInSlot(k);
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return table.getStackInSlotOnClosing(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int a)
    {
        ItemStack re = table.decrStackSize(slot, a);
        this.hostContainer.onCraftMatrixChanged(this);
        return re;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        table.setInventorySlotContents(slot, stack);
        this.hostContainer.onCraftMatrixChanged(this);
    }

    @Override
    public String getInventoryName()
    {
        return "inventory.matrix";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return true;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
    {
        return true;
    }
}
