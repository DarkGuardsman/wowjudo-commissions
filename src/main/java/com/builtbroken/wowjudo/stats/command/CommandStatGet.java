package com.builtbroken.wowjudo.stats.command;

import com.builtbroken.mc.core.commands.prefab.SubCommand;
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
                sender.addChatMessage(new ChatComponentText("Health level set to " + property.getHpIncrease()
                        + (player == sender ? "" : " for " + player.getCommandSenderName())
                        + " resulting in +"
                        + property.getHpIncrease() * StatHandler.HEALTH_SCALE
                        + "hp for a total of "
                        + player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue()


                ));
                sender.addChatMessage(new ChatComponentText("Speed level set to " + property.getSpeedIncrease()
                        + (player == sender ? "" : " for " + player.getCommandSenderName())
                        + " resulting in +" + property.getSpeedIncrease() * StatHandler.SPEED_SCALE
                        + "m/s for a total of "
                        + player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue() //TODO format

                ));
            }
            else
            {
                switch (statName)
                {
                    case "hp":
                    case "health":
                        sender.addChatMessage(new ChatComponentText("Health level set to " + property.getHpIncrease()
                                + (player == sender ? "" : " for " + player.getCommandSenderName())));
                        return;
                    case "speed":
                        sender.addChatMessage(new ChatComponentText("Speed level set to " + property.getSpeedIncrease()
                                + (player == sender ? "" : " for " + player.getCommandSenderName())));
                        return;
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
