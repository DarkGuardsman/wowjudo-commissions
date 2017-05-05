package com.builtbroken.wowjudo.content.generator.gui;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.mc.prefab.gui.slot.SlotOutput;
import com.builtbroken.wowjudo.content.generator.TilePowerGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class ContainerPowerGen extends ContainerBase
{
    public ContainerPowerGen(EntityPlayer player, TilePowerGenerator generator)
    {
        super(player, generator.getInventory());
        this.addSlotToContainer(new SlotFuelBucket(generator.getInventory(), TilePowerGenerator.BUCKET_INPUT_SLOT, 56, 17));
        this.addSlotToContainer(new SlotOutput(generator.getInventory(), TilePowerGenerator.BUCKET_OUTPUT_SLOT, 56, 53));
        addPlayerInventory(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex == TilePowerGenerator.BUCKET_OUTPUT_SLOT)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (slotIndex != TilePowerGenerator.BUCKET_INPUT_SLOT)
            {
                if (TilePowerGenerator.isFuelBucket(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return null;
                    }
                }
                else if (slotIndex >= 3 && slotIndex < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return null;
                    }
                }
                else if (slotIndex >= 30 && slotIndex < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
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
}
