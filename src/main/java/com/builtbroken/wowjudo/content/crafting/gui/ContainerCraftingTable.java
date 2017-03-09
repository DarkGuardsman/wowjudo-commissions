package com.builtbroken.wowjudo.content.crafting.gui;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class ContainerCraftingTable extends ContainerBase
{
    TileEntityCraftingTable craftingTable;

    public InventoryCrafting craftMatrix;
    public IInventory craftResult = new InventoryCraftResult();

    public ContainerCraftingTable(EntityPlayer player, TileEntityCraftingTable table)
    {
        super(player, table);
        this.craftingTable = table;
        craftMatrix = new InventoryCraftingMatrix(this, table);
        int slot = 0;
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                addSlotToContainer(new Slot(craftMatrix, slot++, i * 18 + 30, j * 18 + 17));
            }
        }
        this.addSlotToContainer(new SlotCraftingTable(player, this.craftMatrix, this.craftResult, 0, 124, 35));
        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(table, slot++, 8 + j * 18, 90 + i * 18));
            }
        }
        addPlayerInventory(player, 8, 90 + 3 * 18);

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, craftingTable.getWorldObj()));
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex >= craftingTable.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, TileEntityCraftingTable.SLOT_INVENTORY_START, craftingTable.getSizeInventory(), false))
                {
                    return null;
                }
            }
            //From inventory
            else if (!this.mergeItemStack(itemstack1, craftingTable.getSizeInventory(), craftingTable.getSizeInventory() + 27 + 9, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean func_94530_a(ItemStack p_94530_1_, Slot p_94530_2_)
    {
        return p_94530_2_.inventory != this.craftResult && super.func_94530_a(p_94530_1_, p_94530_2_);
    }
}
