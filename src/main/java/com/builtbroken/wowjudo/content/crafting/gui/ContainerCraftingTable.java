package com.builtbroken.wowjudo.content.crafting.gui;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
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
        for (int row = 0; row < 4; ++row)
        {
            for (int col = 0; col < 4; ++col)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, col + row * 4, 30 + col * 18, 13 + row * 18));
            }
        }
        this.addSlotToContainer(new SlotCraftingTable(player, this.craftMatrix, this.craftResult, 0, 124, 39));
        for (int row = 0; row < 2; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                this.addSlotToContainer(new Slot(table, col + row * 9 + 16, 8 + col * 18, 94 + row * 18));
            }
        }
        addPlayerInventory(player, 8, 135);

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
        //Constant to mark player inventory, based on crafting table
        final int playerInventoryStart = craftingTable.getSizeInventory();
        final int playerInventoryEnd = craftingTable.getSizeInventory() + 36;
        //final int playerToolbarStart = craftingTable.getSizeInventory() + 27;

        ItemStack itemstack = null;
        final Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            //From output to player inventory
            if (slotIndex == 0)
            {
                if (!this.mergeItemStack(itemstack1, playerInventoryStart, playerInventoryEnd, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            //From player inventory to secondary inventory
            else if (slotIndex >= TileEntityCraftingTable.SLOT_INVENTORY_START && slotIndex <= TileEntityCraftingTable.SLOT_INVENTORY_END)
            {
                if (!this.mergeItemStack(itemstack1, playerInventoryStart, playerInventoryEnd, false))
                {
                    return null;
                }
            }
            //From secondary to player inventory
            else if (slotIndex >= playerInventoryStart && slotIndex <= playerInventoryEnd)
            {
                if (!this.mergeItemStack(itemstack1, TileEntityCraftingTable.SLOT_INVENTORY_START, TileEntityCraftingTable.SLOT_INVENTORY_END, false))
                {
                    return null;
                }
            }
            //Default for anything else, merge to player inventory
            else if (!this.mergeItemStack(itemstack1, playerInventoryStart, playerInventoryEnd, false))
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
    public boolean func_94530_a(ItemStack stack, Slot slot)
    {
        return slot.inventory != this.craftResult && super.func_94530_a(stack, slot);
    }
}
