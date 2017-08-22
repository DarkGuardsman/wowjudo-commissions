package com.builtbroken.wowjudo.content.generator.gui;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.mc.prefab.gui.slot.SlotEnergyItem;
import com.builtbroken.mc.prefab.gui.slot.SlotOutput;
import com.builtbroken.wowjudo.content.generator.TilePowerGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class ContainerPowerGen extends ContainerBase<TilePowerGenerator>
{
    public ContainerPowerGen(EntityPlayer player, TilePowerGenerator generator)
    {
        super(player, generator);
        //Bucket slots
        this.addSlotToContainer(new SlotFuelBucket(generator.getInventory(), TilePowerGenerator.BUCKET_INPUT_SLOT, 16, 17));
        this.addSlotToContainer(new SlotOutput(generator.getInventory(), TilePowerGenerator.BUCKET_OUTPUT_SLOT, 16, 53));

        //Charging slots
        for (int slot = TilePowerGenerator.CHARGE_SLOT_START; slot <= TilePowerGenerator.CHARGE_SLOT_END; slot++)
        {
            this.addSlotToContainer(new SlotEnergyItem(generator.getInventory(), slot, 50, 17 + (slot - TilePowerGenerator.CHARGE_SLOT_START) * 18));
        }

        //Inventory
        addPlayerInventory(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        final int playerInventoryEnd = 38;
        final int playerHotBarStart = playerInventoryEnd - 9;
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex == TilePowerGenerator.BUCKET_OUTPUT_SLOT)
            {
                if (!this.mergeItemStack(itemstack1, 2, playerInventoryEnd, true))
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
                else if (slotIndex >= 2 && slotIndex < playerHotBarStart)
                {
                    if (!this.mergeItemStack(itemstack1, playerHotBarStart, playerInventoryEnd, false))
                    {
                        return null;
                    }
                }
                else if (slotIndex >= playerHotBarStart && slotIndex < playerInventoryEnd && !this.mergeItemStack(itemstack1, 2, playerHotBarStart, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 2, playerInventoryEnd, false))
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
}
