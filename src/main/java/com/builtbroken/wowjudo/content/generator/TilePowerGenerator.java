package com.builtbroken.wowjudo.content.generator;

import com.builtbroken.mc.api.tile.IPlayerUsing;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.api.tile.provider.ITankProvider;
import com.builtbroken.mc.codegen.annotations.ExternalInventoryWrapped;
import com.builtbroken.mc.codegen.annotations.MultiBlockWrapped;
import com.builtbroken.mc.codegen.annotations.TankProviderWrapped;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
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
    public static final List<String> supportedFluids = new ArrayList();
    public static final List<String> supportedTiles = new ArrayList();

    public static final int BUCKET_INPUT_SLOT = 0;
    public static final int BUCKET_OUTPUT_SLOT = 1;

    public static int fuelConsumedPerTick = 1;
    public static int powerProviderRange = 100;
    public static int tankVolumeByBuckets = 100;

    static
    {
        supportedFluids.add("fuel");
        supportedTiles.add("com.builtbroken.armory.content.sentry.tile.TileSentry");
        supportedTiles.add("com.builtbroken.armory.content.sentry.tile.TileSentryClient");
    }

    protected boolean isPowered = false;

    public final FluidTank tank = new FluidTank(tankVolumeByBuckets * FluidContainerRegistry.BUCKET_VOLUME);

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
                        if (fluidStack != null && fluidStack.getFluid() == SurvivalMod.fuel)
                        {
                            int room = tank.getCapacity() - tank.getFluidAmount();
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

            //Cycle power
            isPowered = false;

            //Consume full, update power state
            if (hasFuel())
            {
                if (tank.getFluidAmount() > fuelConsumedPerTick)
                {
                    isPowered = true;
                }
                tank.drain(fuelConsumedPerTick, true);
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
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        tank.readFromNBT(tag);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        NBTTagCompound tag = new NBTTagCompound();
        tank.writeToNBT(tag);
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        if (nbt.hasKey("fluidTank"))
        {
            tank.readFromNBT(nbt.getCompoundTag("fluidTank"));
        }
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
