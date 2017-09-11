package com.builtbroken.wowjudo.content.furnace;

import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.framework.block.imp.ILightLevelListener;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.logic.TileMachineNode;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.furnace.gui.ContainerDualFurnace;
import com.builtbroken.wowjudo.content.furnace.gui.GuiDualFurnace;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/30/2017.
 */
@TileWrapped(className = "TileWrapperDualFurnace", wrappers = "ExternalInventory;MultiBlock")
public class TileDualFurnace extends TileMachineNode<ExternalInventory> implements IRotation, IGuiTile, ILightLevelListener
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
    private boolean sendDescPacket = false;
    private boolean hasRecipeForSlot1 = false;
    private boolean hasRecipeForSlot2 = false;

    public boolean hasFuel = false;
    public boolean isOn = false;
    public boolean prevOnState = false;

    public int burnTimer1;
    public int burnTimer2;

    public int burnTimerItem1;
    public int burnTimerItem2;

    public int cookTime;

    public ItemStack renderStack1;
    public ItemStack renderStack2;


    public TileDualFurnace()
    {
        super("furnace", SurvivalMod.DOMAIN);
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);
        isOn = cookTime > 0 || burnTimer1 > 0 || burnTimer2 > 0;
        if (isServer())
        {
            if (checkRecipes || ticks % 100 == 0)
            {
                //Reset
                hasRecipeForSlot2 = hasRecipeForSlot1 = false;

                //Check slot 1
                ItemStack input = getInventory().getStackInSlot(INPUT_SLOT_1);
                if (input != null)
                {
                    ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(input);
                    if (result != null)
                    {
                        ItemStack output = getInventory().getStackInSlot(OUTPUT_SLOT_1);
                        hasRecipeForSlot1 = output == null || InventoryUtility.stacksMatch(output, result) && InventoryUtility.roomLeftInSlotForStack(getInventory(), output, OUTPUT_SLOT_1) >= result.stackSize;
                    }
                }

                //Check slot 2
                input = getInventory().getStackInSlot(INPUT_SLOT_2);
                if (input != null)
                {
                    ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(input);
                    if (result != null)
                    {
                        ItemStack output = getInventory().getStackInSlot(OUTPUT_SLOT_2);
                        hasRecipeForSlot2 = output == null || InventoryUtility.stacksMatch(output, result) && InventoryUtility.roomLeftInSlotForStack(getInventory(), output, OUTPUT_SLOT_2) >= result.stackSize;
                    }
                }
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

            if (burnTimer1 > 0)
            {
                burnTimer1--;
            }

            if (burnTimer2 > 0)
            {
                burnTimer2--;
            }

            //Cook time
            if (hasRecipeForSlot1 && burnTimer1 > 0 || hasRecipeForSlot2 && burnTimer2 > 0)
            {
                isOn = true;
                cookTime++;
                if (cookTime >= MAX_COOK_TIMER)
                {
                    doOperation();
                    cookTime = 0;
                }
            }
            else
            {
                isOn = false;
                this.cookTime = 0;
            }

            //Send update packet TODO change if packet bandwidth is a problem
            if (sendDescPacket || ticks % 3 == 0)
            {
                sendDescPacket();
            }

            if (prevOnState != isOn)
            {
                world().unwrap().markBlockForUpdate(xi(), yi(), zi());
            }
        }
        else if (burnTimer1 > 0 || burnTimer2 > 0)
        {
            final Random random = world().unwrap().rand;
            //TODO if front blocked, turn off animation
            Block block = world().unwrap().getBlock(xi(), yi() + 2, zi());
            if (block == null || block.isAir(world().unwrap(), xi(), yi() + 2, zi()))
            {
                final int smokePerBurner = 3;
                for (int i = 0; i < 0 + (burnTimer1 > 0 ? smokePerBurner : 0) + (burnTimer2 > 0 ? smokePerBurner : 0); i++)
                {
                    world().spawnParticle("smoke", x(), y() + 1.3, z(), 0, 0, 0);
                }
            }
            if (random.nextFloat() > 0.8)
            {
                world().spawnParticle("flame", x() + (random.nextFloat() * 0.3 - random.nextFloat() * 0.3), y() + (random.nextFloat() * 0.3 - random.nextFloat() * 0.3), z() + (random.nextFloat() * 0.3 - random.nextFloat() * 0.3), 0, 0.01, 0);
            }
            if (ticks % 24 == 0)
            {
                world().unwrap().playSound(x(), y(), z(), "fire.fire", 0.8F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }

            if (prevOnState != isOn)
            {
                if (prevOnState)
                {
                    world().unwrap().playSound(x(), y(), z(), "random.fizz", 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
                }
                else
                {
                    world().unwrap().playSound(x(), y(), z(), "fire.ignite", 1.0F, random.nextFloat() * 0.4F + 0.8F, false);
                }
            }
        }

        prevOnState = isOn;
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

                    world().playAudio("random.fizz", x(), y(), z(), 0.5F, 2.6F + (MathHelper.rand.nextFloat() - MathHelper.rand.nextFloat()) * 0.8F);
                }
                else if (InventoryUtility.stacksMatch(result, output) && InventoryUtility.roomLeftInSlot(getInventory(), outputSlot) >= result.stackSize)
                {
                    output.stackSize += result.stackSize;
                    getInventory().setInventorySlotContents(outputSlot, output.copy());
                    getInventory().decrStackSize(inputSlot, 1);

                    world().playAudio("random.fizz", x(), y(), z(), 0.5F, 2.6F + (MathHelper.rand.nextFloat() - MathHelper.rand.nextFloat()) * 0.8F);
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
        buf.writeBoolean(hasFuel);
        if (getInventory().getStackInSlot(INPUT_SLOT_1) != null)
        {
            buf.writeBoolean(true);
            ByteBufUtils.writeItemStack(buf, getInventory().getStackInSlot(INPUT_SLOT_1));
        }
        else
        {
            buf.writeBoolean(false);
        }

        if (getInventory().getStackInSlot(INPUT_SLOT_2) != null)
        {
            buf.writeBoolean(true);
            ByteBufUtils.writeItemStack(buf, getInventory().getStackInSlot(INPUT_SLOT_2));
        }
        else
        {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        cookTime = buf.readInt();
        burnTimer1 = buf.readInt();
        burnTimerItem1 = buf.readInt();
        burnTimer2 = buf.readInt();
        burnTimerItem2 = buf.readInt();
        hasFuel = buf.readBoolean();

        if (buf.readBoolean())
        {
            renderStack1 = ByteBufUtils.readItemStack(buf);
        }
        else
        {
            renderStack1 = null;
        }

        if (buf.readBoolean())
        {
            renderStack2 = ByteBufUtils.readItemStack(buf);
        }
        else
        {
            renderStack2 = null;
        }
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if (slot == INPUT_SLOT_1 || slot == INPUT_SLOT_2)
        {
            checkRecipes = true;
            sendDescPacket = true;
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

    @Override
    public int getLightLevel()
    {
        if (cookTime > 0)
        {
            return 13;
        }
        return 0;
    }
}
