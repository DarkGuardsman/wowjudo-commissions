package com.builtbroken.wowjudo.content.furnace;

import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.logic.TileMachineNode;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.furnace.gui.ContainerDualFurnace;
import com.builtbroken.wowjudo.content.furnace.gui.GuiDualFurnace;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/30/2017.
 */
@TileWrapped(className = "TileWrapperDualFurnace", wrappers = "ExternalInventory;MultiBlock")
public class TileDualFurnace extends TileMachineNode<ExternalInventory> implements IRotation, IGuiTile
{
    public static final int INVENTORY_SIZE = 6;

    public static final int INPUT_SLOT_1 = 0;
    public static final int INPUT_SLOT_2 = 1;
    public static final int FUEL_SLOT_1 = 2;
    public static final int FUEL_SLOT_2 = 3;
    public static final int OUTPUT_SLOT_1 = 4;
    public static final int OUTPUT_SLOT_2 = 5;

    public static final int MAX_COOK_TIMER = 200;

    private ForgeDirection dirCache;

    private boolean burnBothSlots = false;

    private boolean checkRecipes = true;
    private boolean hasRecipeForSlot1 = false;
    private boolean hasRecipeForSlot2 = false;

    public int burnTimer1;
    public int burnTimer2;

    public int burnTimerItem1;
    public int burnTimerItem2;

    public int cookTime;

    public TileDualFurnace()
    {
        super("furnace", SurvivalMod.DOMAIN);
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);

        if (isServer())
        {
            if (checkRecipes)
            {
                //Check slot 1
                ItemStack input = getInventory().getStackInSlot(INPUT_SLOT_1);
                hasRecipeForSlot1 = input != null && FurnaceRecipes.smelting().getSmeltingResult(input) != null;

                //Check slot 2
                input = getInventory().getStackInSlot(INPUT_SLOT_2);
                hasRecipeForSlot2 = input != null && FurnaceRecipes.smelting().getSmeltingResult(input) != null;
            }

            //Consume fuel
            if (hasRecipeForSlot1)
            {
                if (burnTimer1 <= 0)
                {
                    ItemStack fuel = getInventory().getStackInSlot(FUEL_SLOT_1);
                    if (fuel != null)
                    {
                        burnTimerItem1 = TileEntityFurnace.getItemBurnTime(fuel);
                        if (burnTimerItem1 > 0)
                        {
                            getInventory().decrStackSize(FUEL_SLOT_1, 1);
                            burnTimer1 += burnTimerItem1;
                        }
                    }
                }
            }

            //Consume fuel
            if (hasRecipeForSlot2)
            {
                if (burnTimer2 <= 0)
                {
                    ItemStack fuel = getInventory().getStackInSlot(FUEL_SLOT_2);
                    if (fuel != null)
                    {
                        burnTimerItem2 = TileEntityFurnace.getItemBurnTime(fuel);
                        if (burnTimerItem2 > 0)
                        {
                            getInventory().decrStackSize(FUEL_SLOT_2, 1);
                            burnTimer2 += burnTimerItem2;
                        }
                    }
                }
            }

            //Cook time
            if (hasRecipeForSlot1 && burnTimer1 > 0 || hasRecipeForSlot2 && burnTimer2 > 0)
            {
                cookTime++;
                if (cookTime >= MAX_COOK_TIMER)
                {
                    doOperation();
                    cookTime = 0;
                }
            }
            else
            {
                this.cookTime = 0;
            }

            //Send update packet TODO change if packet bandwidth is a problem
            if (ticks % 3 == 0)
            {
                sendDescPacket();
            }
        }
    }

    /**
     * Called to run the smelt operation
     */
    protected void doOperation()
    {
        //Check slot 0
        if (burnTimer1 > 0)
        {
            processSlot(INPUT_SLOT_1, OUTPUT_SLOT_1);
        }

        //Check slot 1
        if (burnTimer2 > 0)
        {
            processSlot(INPUT_SLOT_2, OUTPUT_SLOT_2);
        }
    }

    /**
     * Called to run the smelt operation on a single slot
     *
     * @param inputSlot  - slot to take items from
     * @param outputSlot - slot to place items inside
     */
    protected void processSlot(int inputSlot, int outputSlot)//TODO change output slot to an array of output slots
    {
        ItemStack input = getInventory().getStackInSlot(inputSlot);
        if (input != null)
        {
            //Copy input and change size to prevent issues
            input = input.copy();
            input.stackSize = 1;

            //Get result
            ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(input);
            if (result != null)
            {
                //Output
                ItemStack output = getInventory().getStackInSlot(outputSlot);
                if (output == null)
                {
                    getInventory().setInventorySlotContents(outputSlot, result.copy());
                    getInventory().decrStackSize(inputSlot, 1);
                }
                else if (InventoryUtility.stacksMatch(result, output) && InventoryUtility.roomLeftInSlot(getInventory(), outputSlot) >= result.stackSize)
                {
                    output.stackSize += result.stackSize;
                    getInventory().setInventorySlotContents(outputSlot, output.copy());
                    getInventory().decrStackSize(inputSlot, 1);
                }
            }
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        buf.writeInt(cookTime);
        buf.writeInt(burnTimer1);
        buf.writeInt(burnTimerItem1);
        buf.writeInt(burnTimer2);
        buf.writeInt(burnTimerItem2);
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        cookTime = buf.readInt();
        burnTimer1 = buf.readInt();
        burnTimerItem1 = buf.readInt();
        burnTimer2 = buf.readInt();
        burnTimerItem2 = buf.readInt();
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if (slot == INPUT_SLOT_1 || slot == INPUT_SLOT_2)
        {
            checkRecipes = true;
        }
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (dirCache == null)
        {
            dirCache = ForgeDirection.getOrientation(world().unwrap().getBlockMetadata(xi(), yi(), zi()));
        }
        return dirCache;
    }

    @Override
    protected ExternalInventory createInventory()
    {
        return new ExternalInventory(this, INVENTORY_SIZE);
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (slot == INPUT_SLOT_1 || slot == INPUT_SLOT_2)
        {
            return FurnaceRecipes.smelting().getSmeltingResult(stack) != null; //TODO cache
        }
        else if (slot == FUEL_SLOT_1 || slot == FUEL_SLOT_2)
        {
            return TileEntityFurnace.isItemFuel(stack); //TODO cache
        }
        return canStore(stack, side);
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        return slot == OUTPUT_SLOT_1 || slot == OUTPUT_SLOT_2;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerDualFurnace(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiDualFurnace(player, this);
    }
}
