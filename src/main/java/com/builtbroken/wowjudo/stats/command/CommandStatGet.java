package com.builtbroken.wowjudo.stats.command;

import com.builtbroken.mc.core.commands.prefab.SubCommand;
import com.builtbroken.wowjudo.stats.FoodStatOverride;
import com.builtbroken.wowjudo.stats.StatEntityProperty;
import com.builtbroken.wowjudo.stats.StatHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/16/2017.
 */
public class CommandStatGet extends SubCommand
{
    public CommandStatGet()
    {
        super("get");
    }

    @Override
    public boolean handleEntityPlayerCommand(EntityPlayer player, String[] args)
    {
        return handleConsoleCommand(player, args);
    }

    protected void outputStat(EntityPlayer player, ICommandSender sender, String statName)
    {
        StatEntityProperty property = StatHandler.getPropertyForEntity(player);
        if (property != null)
        {
            if (statName.equalsIgnoreCase("all"))
            {
                printHp(sender, property);
                printArmor(sender, property);
                printAttack(sender, property);
                printFood(sender, property);
                printAir(sender, property);
                printSpeed(sender, property);
            }
            else
            {
                switch (statName)
                {
                    case "hp":
                    case "health":
                        printHp(sender, property);
                        return;
                    case "speed":
                        printSpeed(sender, property);
                        return;
                    case "damage":
                    case "attack":
                        printAttack(sender, property);
                    case "armor":
                        printArmor(sender, property);
                    case "food":
                        printFood(sender, property);
                    case "air":
                        printAir(sender, property);
                    default:
                        sender.addChatMessage(new ChatComponentText("Error: Unknown stat '" + statName + "' either its not implement or doesn't exist"));
                        return;
                }
            }
        }
        else
        {
            sender.addChatMessage(new ChatComponentText("Error: Failed to load stat handler for '" + player.getCommandSenderName() + "' this is a bug"));
        }
    }

    protected void printHp(ICommandSender sender, StatEntityProperty property)
    {
        sender.addChatMessage(new ChatComponentText("Health level set to " + property.getHpIncrease()
                + (property.entityPlayer == sender ? "" : " for " + property.entityPlayer.getCommandSenderName())
                + " resulting in +"
                + property.getHpIncrease() * StatHandler.HEALTH_SCALE
                + "hp for a total of "
                + property.entityPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue()
        ));
    }

    protected void printSpeed(ICommandSender sender, StatEntityProperty property)
    {
        sender.addChatMessage(new ChatComponentText("Speed level set to " + property.getSpeedIncrease()
                + (property.entityPlayer == sender ? "" : " for " + property.entityPlayer.getCommandSenderName())
                + " resulting in +" + String.format("%.2f", property.getSpeedIncrease() * StatHandler.SPEED_SCALE)
                + "m/s for a total of "
                + String.format("%.2f", property.entityPlayer.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()) //TODO format
        ));
    }

    protected void printAttack(ICommandSender sender, StatEntityProperty property)
    {
        sender.addChatMessage(new ChatComponentText("Melee Damage level set to " + property.getMeleeDamageIncrease()
                + (property.entityPlayer == sender ? "" : " for " + property.entityPlayer.getCommandSenderName())
                + " resulting in +" + property.getMeleeDamageIncrease() * StatHandler.DAMAGE_SCALE
                + " extra damage"
        ));
    }

    protected void printArmor(ICommandSender sender, StatEntityProperty property)
    {
        sender.addChatMessage(new ChatComponentText("Armor level set to " + property.getArmorIncrease()
                + (property.entityPlayer == sender ? "" : " for " + property.entityPlayer.getCommandSenderName())
                + " resulting in +" + property.getArmorIncrease() * StatHandler.ARMOR_DAMAGE_REDUCTION_SCALE
                + "% damage reduction"
        ));
    }

    protected void printAir(ICommandSender sender, StatEntityProperty property)
    {
        sender.addChatMessage(new ChatComponentText("Air level set to " + property.getAirIncrease()
                + (property.entityPlayer == sender ? "" : " for " + property.entityPlayer.getCommandSenderName())
                + " resulting in +" + property.getAirIncrease() * StatHandler.AIR_SCALE
                + " extra air"
        ));
    }

    protected void printFood(ICommandSender sender, StatEntityProperty property)
    {
        String message = "Food level set to " + property.getFoodAmountIncrease()
                + (property.entityPlayer == sender ? "" : " for " + property.entityPlayer.getCommandSenderName())
                + " resulting in +" + property.getFoodAmountIncrease() * StatHandler.FOOD_SCALE;
        if (property.entityPlayer.foodStats instanceof FoodStatOverride)
        {
            message += " for a total of "
                    + ((FoodStatOverride) property.entityPlayer.foodStats).maxFoodLevel;
        }
        sender.addChatMessage(new ChatComponentText(message));
    }


    @Override
    public boolean handleConsoleCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            if (sender instanceof EntityPlayer)
            {
                outputStat((EntityPlayer) sender, sender, args[0].toLowerCase());
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Error: Too few arguments"));
                printHelp(sender, 0);
            }
        }
        else if (args.length == 2)
        {
            EntityPlayer target = getPlayer(sender, args[0]);
            if (target != null)
            {
                outputStat(target, sender, args[1].toLowerCase());
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Error: Could not find player '" + args[0] + "'"));
            }
        }
        else
        {
            sender.addChatMessage(new ChatComponentText("Error: Too many arguments"));
            printHelp(sender, 0);
        }
        return true;
    }
}
