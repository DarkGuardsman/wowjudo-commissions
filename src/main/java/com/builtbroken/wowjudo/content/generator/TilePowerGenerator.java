package com.builtbroken.wowjudo.content.generator;

import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.api.tile.provider.ITankProvider;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.energy.UniversalEnergySystem;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.logic.TileMachineNode;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.generator.gui.ContainerPowerGen;
import com.builtbroken.wowjudo.content.generator.gui.GuiPowerGen;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/4/2017.
 */
@TileWrapped(className = "TileWrapperPowerGenerator", wrappers = "ExternalInventory;TankProvider;MultiBlock")
public class TilePowerGenerator extends TileMachineNode<ExternalInventory> implements ITankProvider, IRotation, IGuiTile
{
    //Supported tiles to power
    public static final List<String> supportedTiles = new ArrayList();

    //Slots
    public static final int BUCKET_INPUT_SLOT = 0;
    public static final int BUCKET_OUTPUT_SLOT = 1;
    public static final int CHARGE_SLOT_START = 2;
    public static final int CHARGE_SLOT_END = 4;

    //Fuel Settings
    public static int fuelConsumedPerRun = 1;
    public static int delayBetweenRuns = 20;

    //Power Setting
    public static int powerProviderRange = 100;

    //Tank volume setting
    public static int tankVolumeByBuckets = 100;

    public static void init(Configuration config)
    {
        String[] tiles = config.getStringList("supported_tiles", "Fuel_Generator", new String[]{"com.builtbroken.armory.content.sentry.tile.TileSentry", "com.builtbroken.armory.content.sentry.tile.TileSentryClient"}, "Notes which tiles the generator should power, dev debug tool can be used to get tile class names.");

        for (String tile : tiles)
        {
            supportedTiles.add(tile.trim());
        }

        fuelConsumedPerRun = config.getInt("fuel_consumed_per_cycle", "Fuel_Generator", fuelConsumedPerRun, 1, 1000000, "How much fuel is consumed each time the generator cycles.");
        delayBetweenRuns = config.getInt("fuel_consumption_cycle_time", "Fuel_Generator", delayBetweenRuns, 1, 1000000, "How long in ticks (20 ticks a second) between fuel cycles.");
        powerProviderRange = config.getInt("power_range", "Fuel_Generator", powerProviderRange, 10, 1000000, "Range in meters (blocks) to power machines.");
        tankVolumeByBuckets = config.getInt("fuel_tank_volume", "Fuel_Generator", tankVolumeByBuckets, 0, 1000000, "Volume in buckets (1000mb) of fuel to hold in the generator.");
    }

    public final FluidTank tank = new FluidTank(tankVolumeByBuckets * FluidContainerRegistry.BUCKET_VOLUME);
    public boolean turnedOn = true;

    protected boolean isPowered = false;

    //Cache of facing direction
    private ForgeDirection dirCache;

    //Area this tile provides power
    private Cube powerArea;

    public TilePowerGenerator()
    {
        super("machine.power.gen", SurvivalMod.DOMAIN);
    }

    @Override
    protected ExternalInventory createInventory()
    {
        return new ExternalInventory(this, 5); // 2 slots for buckets, 3 slots for charging
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        powerArea = new Cube(toPos().sub(powerProviderRange), toPos().add(powerProviderRange));
    }

    @Override
    public void update(long tick)
    {
        super.firstTick();
        super.update(tick);
        if (isServer())
        {
            //Clear invalid fluids
            if (tank.getFluid() != null && tank.getFluid().getFluid() != SurvivalMod.fuel)
            {
                SurvivalMod.instance.logger().info("Error clearing invalid fluid '" + tank.getFluid() + "' found in " + this);
                tank.drain(Integer.MAX_VALUE, true);
            }

            //Drain items of fluid
            drainFluidContainers(BUCKET_INPUT_SLOT, BUCKET_OUTPUT_SLOT);

            //Only run when on
            if (turnedOn)
            {
                doFuelConsumption(tick);

                //Power devices nearby
                if (isPowered)
                {
                    triggerPowerEffects(tick);
                    chargeTiles();
                    chargeItems();
                }
            }
        }
    }

    protected void triggerPowerEffects(long tick)
    {
        if (tick % 90 == 0)
        {
            //TODO translate to center of machine
            world().playAudio("wjsurvialmod:wjPowerGenerator.tick", x(), y() + 1f, z(), 1, 1); //TODO config for volume
        }
    }

    protected void chargeTiles()
    {
        powerArea.getTilesInArea(world().unwrap()).forEach(tileEntity -> chargeTile(tileEntity));
    }

    protected void chargeTile(TileEntity tileEntity)
    {
        if (tileEntity != null && tileEntity != getHost())
        {
            String className = tileEntity.getClass().getName();
            if (supportedTiles.contains(className))
            {
                UniversalEnergySystem.fill(tileEntity, ForgeDirection.UNKNOWN, Integer.MAX_VALUE, true);
            }
        }
    }

    protected void chargeItems()
    {
        //Loop to charge all items
        for (int slot = CHARGE_SLOT_START; slot <= CHARGE_SLOT_END; slot++)
        {
            //Get item
            ItemStack stack = getInventory().getStackInSlot(slot);

            //Only work on items that can handle energy
            if (stack != null && UniversalEnergySystem.isHandler(stack, ForgeDirection.UNKNOWN))
            {
                UniversalEnergySystem.chargeItem(stack, Integer.MAX_VALUE, true);
            }
        }
    }

    protected void doFuelConsumption(long tick)
    {
        //Only update power cycle every so many ticks
        if (tick % delayBetweenRuns == 0)
        {
            //Cycle power
            isPowered = false;

            //Consume full, update power state
            if (hasFuel())
            {
                if (tank.getFluidAmount() > fuelConsumedPerRun)
                {
                    isPowered = true;
                }
                tank.drain(fuelConsumedPerRun, true);
            }
        }
    }

    /**
     * Drains the fluid item in the input slot and moves it to the output slot
     *
     * @param inputSlot
     * @param outputSlot
     */
    protected void drainFluidContainers(int inputSlot, int outputSlot)
    {
        //TODO document and move to helper class for reuse
        //Fill tank from input items
        if (!isFull())
        {
            ItemStack bucketStack = getInventory().getStackInSlot(inputSlot);
            if (bucketStack != null)
            {
                if (bucketStack.getItem() instanceof IFluidContainerItem)
                {
                    FluidStack fluidStack = ((IFluidContainerItem) bucketStack.getItem()).getFluid(bucketStack);
                    if (bucketStack.stackSize == 1 && fluidStack != null && fluidStack.getFluid() == SurvivalMod.fuel)
                    {
                        int room = tank.getCapacity() - tank.getFluidAmount();

                        if (room > 0)
                        {
                            //Fill tank
                            int filled = tank.fill(fluidStack, true);

                            //Drain bucket
                            ((IFluidContainerItem) bucketStack.getItem()).drain(bucketStack, filled, true);

                            //If empty, eject to output slot
                            if (((IFluidContainerItem) bucketStack.getItem()).getFluid(bucketStack) == null)
                            {
                                ItemStack output = getInventory().getStackInSlot(outputSlot);
                                if (output == null || InventoryUtility.stacksMatch(bucketStack, output) && InventoryUtility.roomLeftInSlot(getInventory(), outputSlot) >= 1)
                                {
                                    //Get tank
                                    IFluidTank tank = getTankForFluid(fluidStack.getFluid());

                                    //Ensure tank is not null and will accept the fluid
                                    if (tank != null && tank.fill(fluidStack, false) >= fluidStack.amount)
                                    {


                                        //Decrease input
                                        getInventory().decrStackSize(inputSlot, 1);

                                        //Output empty container
                                        if (output == null)
                                        {
                                            output = bucketStack.copy();
                                        }
                                        else
                                        {
                                            output.stackSize++;
                                        }
                                        getInventory().setInventorySlotContents(outputSlot, output);
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        //TODO if containers are stacked, eject containers
                    }
                }
                else if (FluidContainerRegistry.isFilledContainer(bucketStack))
                {
                    FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(bucketStack);
                    if (fluidStack != null && fluidStack.getFluid() == SurvivalMod.fuel)
                    {
                        int room = tank.getCapacity() - tank.getFluidAmount();
                        if (room >= fluidStack.amount) //Ensure can take entire bucket to prevent issues
                        {
                            ItemStack container = FluidContainerRegistry.drainFluidContainer(bucketStack.copy());
                            ItemStack output = getInventory().getStackInSlot(outputSlot);
                            if (container != null && (output == null || InventoryUtility.stacksMatch(container, output) && InventoryUtility.roomLeftInSlot(getInventory(), outputSlot) >= 1))
                            {
                                //Get tank
                                IFluidTank tank = getTankForFluid(fluidStack.getFluid());

                                //Ensure tank is not null and will accept the fluid
                                if (tank != null && tank.fill(fluidStack, false) >= fluidStack.amount)
                                {
                                    //Fill tank
                                    tank.fill(fluidStack, true);

                                    //Decrease input
                                    getInventory().decrStackSize(inputSlot, 1);

                                    //Output empty container
                                    if (output == null)
                                    {
                                        output = container.copy();
                                    }
                                    else
                                    {
                                        output.stackSize++;
                                    }
                                    getInventory().setInventorySlotContents(outputSlot, output);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean hasFuel()
    {
        return tank.getFluid() != null && tank.getFluid().getFluid() == SurvivalMod.fuel;
    }

    public boolean isFull()
    {
        return hasFuel() && tank.getFluidAmount() >= tank.getCapacity();
    }

    @Override
    public IFluidTank getTankForFluid(Fluid fluid)
    {
        if (fluid != null && fluid == SurvivalMod.fuel)
        {
            return tank;
        }
        return null;
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

    public static boolean isFuelBucket(ItemStack stack)
    {
        if (stack.getItem() instanceof IFluidContainerItem)
        {
            FluidStack fluidStack = ((IFluidContainerItem) stack.getItem()).getFluid(stack);
            if (fluidStack != null && fluidStack.getFluid() == SurvivalMod.fuel)
            {
                return true;
            }
        }
        else if (FluidContainerRegistry.isFilledContainer(stack))
        {
            FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
            if (fluidStack != null && fluidStack.getFluid() == SurvivalMod.fuel)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isServer())
            {
                if (id == 1)
                {
                    turnedOn = buf.readBoolean();
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public void setOnState(boolean state)
    {
        turnedOn = state;
        if (isClient())
        {
            sendPacketToServer(getHost().getPacketForData(1, state));
        }
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        turnedOn = buf.readBoolean();
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        tank.readFromNBT(tag);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeBoolean(turnedOn);
        NBTTagCompound tag = new NBTTagCompound();
        tank.writeToNBT(tag);
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    protected void writeGuiPacket(EntityPlayer player, ByteBuf buf)
    {
        super.writeGuiPacket(player, buf);
        writeDescPacket(player, buf);
    }

    @Override
    protected void readGuiPacket(EntityPlayer player, ByteBuf buf)
    {
        super.readGuiPacket(player, buf);
        readDescPacket(player, buf);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        if (nbt.hasKey("fluidTank"))
        {
            tank.readFromNBT(nbt.getCompoundTag("fluidTank"));
        }
        turnedOn = nbt.hasKey("turnedOn") ? nbt.getBoolean("turnedOn") : true;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        if (tank.getFluidAmount() > 0)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tank.writeToNBT(tag);
            nbt.setTag("fluidTank", tag);
        }
        nbt.setBoolean("turnedOn", turnedOn);
        return nbt;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerPowerGen(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiPowerGen(player, this);
    }

    @Override
    protected String getClassDisplayName()
    {
        return "TilePowerGenerator";
    }
}
