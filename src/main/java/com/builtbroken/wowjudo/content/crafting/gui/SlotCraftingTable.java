package com.builtbroken.wowjudo.content.crafting.gui;

import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2017.
 */
public class SlotCraftingTable extends Slot
{
    /** The craft matrix inventory linked to this result slot. */
    private final InventoryCraftingMatrix craftMatrix;
    private final TileEntityCraftingTable craftingTable;
    /** The player that is using the GUI where this slot resides. */
    private EntityPlayer thePlayer;
    /** The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset. */
    private int amountCrafted;

    public SlotCraftingTable(EntityPlayer player, TileEntityCraftingTable craftingTable, InventoryCraftingMatrix matrix, IInventory result, int id, int x, int y)
    {
        super(result, id, x, y);
        this.thePlayer = player;
        this.craftMatrix = matrix;
        this.craftingTable = craftingTable;
    }

    @Override
    public boolean isItemValid(ItemStack p_75214_1_)
    {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount)
    {
        if (this.getHasStack())
        {
            this.amountCrafted += Math.min(amount, this.getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }

    @Override
    protected void onCrafting(ItemStack stack, int amount)
    {
        this.amountCrafted += amount;
        this.onCrafting(stack);
    }

    @Override
    protected void onCrafting(ItemStack stack)
    {
        stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amountCrafted);
        this.amountCrafted = 0;

        if (stack.getItem() == Item.getItemFromBlock(Blocks.crafting_table))
        {
            this.thePlayer.addStat(AchievementList.buildWorkBench, 1);
        }

        if (stack.getItem() instanceof ItemPickaxe)
        {
            this.thePlayer.addStat(AchievementList.buildPickaxe, 1);
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.furnace))
        {
            this.thePlayer.addStat(AchievementList.buildFurnace, 1);
        }

        if (stack.getItem() instanceof ItemHoe)
        {
            this.thePlayer.addStat(AchievementList.buildHoe, 1);
        }

        if (stack.getItem() == Items.bread)
        {
            this.thePlayer.addStat(AchievementList.makeBread, 1);
        }

        if (stack.getItem() == Items.cake)
        {
            this.thePlayer.addStat(AchievementList.bakeCake, 1);
        }

        if (stack.getItem() instanceof ItemPickaxe && ((ItemPickaxe) stack.getItem()).func_150913_i() != Item.ToolMaterial.WOOD)
        {
            this.thePlayer.addStat(AchievementList.buildBetterPickaxe, 1);
        }

        if (stack.getItem() instanceof ItemSword)
        {
            this.thePlayer.addStat(AchievementList.buildSword, 1);
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table))
        {
            this.thePlayer.addStat(AchievementList.enchantments, 1);
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.bookshelf))
        {
            this.thePlayer.addStat(AchievementList.bookcase, 1);
        }
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack)
    {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, craftMatrix);
        this.onCrafting(stack);

        for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i)
        {
            ItemStack matrixStack = this.craftMatrix.getStackInSlot(i);

            if (matrixStack != null)
            {
                boolean removed = false;

                //Attempt to remove from secondary inventory
                for (int j = TileEntityCraftingTable.SLOT_INVENTORY_START; j < TileEntityCraftingTable.SLOT_INVENTORY_END; j++)
                {
                    ItemStack tableStack = craftingTable.getStackInSlot(j);
                    if (tableStack != null && InventoryUtility.stacksMatch(tableStack, matrixStack))
                    {
                        craftingTable.decrStackSize(j, 1);
                        removed = true;
                        break;
                    }
                }

                //If not removed from secondary decrease
                if (!removed)
                {
                    this.craftMatrix.decrStackSize(i, 1);
                }

                //Add container item to grid
                if (matrixStack.getItem().hasContainerItem(matrixStack))
                {
                    ItemStack itemstack2 = matrixStack.getItem().getContainerItem(matrixStack);

                    //Fire destroyed item even it was tool
                    if (itemstack2 != null && itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage())
                    {
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, itemstack2));
                        continue;
                    }

                    if (!matrixStack.getItem().doesContainerItemLeaveCraftingGrid(matrixStack) || !this.thePlayer.inventory.addItemStackToInventory(itemstack2))
                    {
                        if (this.craftMatrix.getStackInSlot(i) == null)
                        {
                            this.craftMatrix.setInventorySlotContents(i, itemstack2);
                        }
                        else
                        {
                            //TODO add to secondary inventory first
                            this.thePlayer.dropPlayerItemWithRandomChoice(itemstack2, false);
                        }
                    }
                }
            }
        }

        //Update container
        craftMatrix.hostContainer.onCraftMatrixChanged(craftMatrix);
    }
}
