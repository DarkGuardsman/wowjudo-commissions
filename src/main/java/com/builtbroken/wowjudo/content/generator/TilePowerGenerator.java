package com.builtbroken.wowjudo.content.generator;

import com.builtbroken.mc.api.tile.IPlayerUsing;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.api.tile.provider.ITankProvider;
import com.builtbroken.mc.codegen.annotations.ExternalInventoryWrapped;
import com.builtbroken.mc.codegen.annotations.MultiBlockWrapped;
import com.builtbroken.mc.codegen.annotations.TankProviderWrapped;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.lib.energy.UniversalEnergySystem;
import com.builtbroken.mc.lib.world.map.TileMapRegistry;
import com.builtbroken.mc.lib.world.radar.RadarMap;
import com.builtbroken.mc.lib.world.radar.data.RadarObject;
import com.builtbroken.mc.lib.world.radar.data.RadarTile;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/4/2017.
 */
@TileWrapped(className = "TileWrapperPowerGenerator")
@ExternalInventoryWrapped()
@TankProviderWrapped()
@MultiBlockWrapped()
public class TilePowerGenerator extends TileMachineNode<ExternalInventory> implements ITankProvider, IRotation, IGuiTile, IPlayerUsing
{
    //Supported tiles to power
    public static final List<String> supportedTiles = new ArrayList();

    //Slots
    public static final int BUCKET_INPUT_SLOT = 0;
    public static final int BUCKET_OUTPUT_SLOT = 1;

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
        tankVolumeByBuckets = config.getInt("fuel_tank_volume", "Fuel_Generator", fuelConsumedPerRun, 0, 1000000, "Volume in buckets (1000mb) of fuel to hold in the generator.");
    }

    public final FluidTank tank = new FluidTank(tankVolumeByBuckets * FluidContainerRegistry.BUCKET_VOLUME);
    public boolean turnedOn = true;

    protected boolean isPowered = false;

    private ForgeDirection dirCache;
    private Cube areaOfEffect;

    private List<EntityPlayer> players = new ArrayList();

    public TilePowerGenerator()
    {
        super("machine.power.gen", SurvivalMod.DOMAIN);
    }

    @Override
    protected ExternalInventory createInventory()
    {
        return new ExternalInventory(this, 2);
    }

    @Override
    public void update(long tick)
    {
        if (isServer())
        {
            //Clear invalid fluids
            if (tank.getFluid() != null && tank.getFluid().getFluid() != SurvivalMod.fuel)
            {
                SurvivalMod.instance.logger().info("Error clearing invalid fluid '" + tank.getFluid() + "' found in " + this);
                tank.drain(Integer.MAX_VALUE, true);
            }

            //Fill tank from input items
            if (!isFull())
            {
                ItemStack bucketStack = getInventory().getStackInSlot(BUCKET_INPUT_SLOT);
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
                                    ItemStack output = getInventory().getStackInSlot(BUCKET_OUTPUT_SLOT);
                                    if (output == null || InventoryUtility.stacksMatch(bucketStack, output) && InventoryUtility.roomLeftInSlot(getInventory(), BUCKET_OUTPUT_SLOT) >= 1)
                                    {
                                        //Get tank
                                        IFluidTank tank = getTankForFluid(fluidStack.getFluid());

                                        //Ensure tank is not null and will accept the fluid
                                        if (tank != null && tank.fill(fluidStack, false) >= fluidStack.amount)
                                        {


                                            //Decrease input
                                            getInventory().decrStackSize(BUCKET_INPUT_SLOT, 1);

                                            //Output empty container
                                            if (output == null)
                                            {
                                                output = bucketStack.copy();
                                            }
                                            else
                                            {
                                                output.stackSize++;
                                            }
                                            getInventory().setInventorySlotContents(BUCKET_OUTPUT_SLOT, output);
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
                                ItemStack output = getInventory().getStackInSlot(BUCKET_OUTPUT_SLOT);
                                if (container != null && (output == null || InventoryUtility.stacksMatch(container, output) && InventoryUtility.roomLeftInSlot(getInventory(), BUCKET_OUTPUT_SLOT) >= 1))
                                {
                                    //Get tank
                                    IFluidTank tank = getTankForFluid(fluidStack.getFluid());

                                    //Ensure tank is not null and will accept the fluid
                                    if (tank != null && tank.fill(fluidStack, false) >= fluidStack.amount)
                                    {
                                        //Fill tank
                                        tank.fill(fluidStack, true);

                                        //Decrease input
                                        getInventory().decrStackSize(BUCKET_INPUT_SLOT, 1);

                                        //Output empty container
                                        if (output == null)
                                        {
                                            output = container.copy();
                                        }
                                        else
                                        {
                                            output.stackSize++;
                                        }
                                        getInventory().setInventorySlotContents(BUCKET_OUTPUT_SLOT, output);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Only run when on
            if (turnedOn)
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

                //Power devices nearby
                if (isPowered)
                {
                    if (areaOfEffect == null)
                    {
                        areaOfEffect = new Cube(toPos().sub(powerProviderRange), toPos().add(powerProviderRange)).cropToWorld();
                    }

                    RadarMap map = TileMapRegistry.getRadarMapForDim(world().provider.dimensionId);
                    List<RadarObject> objects = map.getRadarObjects(areaOfEffect, true);
                    for (RadarObject object : objects)
                    {
                        if (object instanceof RadarTile)
                        {
                            TileEntity tileEntity = ((RadarTile) object).tile;
                            if (tileEntity != null && tileEntity != getHost())
                            {
                                String className = tileEntity.getClass().getName();
                                if (supportedTiles.contains(className))
                                {
                                    UniversalEnergySystem.fill(tileEntity, ForgeDirection.UNKNOWN, Integer.MAX_VALUE, true);
                                }
                            }
                        }
                    }
                }
            }

            //GUI update
            if (tick % 3 == 0)
            {
                Iterator<EntityPlayer> it = getPlayersUsing().iterator();
                while (it.hasNext())
                {
                    EntityPlayer next = it.next();
                    if (!(next.openContainer instanceof ContainerPowerGen))
                    {
                        it.remove();
                    }
                    //TODO if container is open, check it matches this tile
                }
                //Sync data to GUI users
                sendPacketToGuiUsers(getDescPacket());
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
            dirCache = ForgeDirection.getOrientation(world().getBlockMetadata(xi(), yi(), zi()));
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
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        tank.readFromNBT(tag);
        turnedOn = buf.readBoolean();
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        NBTTagCompound tag = new NBTTagCompound();
        tank.writeToNBT(tag);
        ByteBufUtils.writeTag(buf, tag);
        buf.writeBoolean(turnedOn);
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

    @Override
    public Collection<EntityPlayer> getPlayersUsing()
    {
        return players;
    }
}
