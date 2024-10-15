package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.IllegalFormatException;

public class Debug extends CommandBase {
    @Override
    public String getCommandName() {return "srmdebug";}

    @Override
    public String getCommandUsage(ICommandSender sender) {return "/srmdebug <option> <value>";}

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(!sender.getName().contains("_Wyan")){
            ChatUtils.sendChatMessage("§eAre you sure you want to do this?");
        }else{
            ChatUtils.sendChatMessage("§2This is Wyan!");
        }

        if(args.length == 0){
            return;
        } else if (args.length == 1) {
            ChatUtils.sendChatMessage("§cPlease assign a value");

        }else if(args.length == 2){
            try{
                Field field = Main.class.getDeclaredField(args[0]);
                String type = field.getAnnotatedType().getType().getTypeName();
                Object currentValue = field.get(null);
                ChatUtils.sendChatMessage(type);
                if(type.equals("int")){
                    field.set(null, Integer.valueOf(args[1]));
                }else if(type.equals("float")){
                    field.set(null, Float.valueOf(args[1]));
                }else if(type.equals("boolean")){
                    field.set(null, Boolean.valueOf(args[1]));
                }else if (type.equals("double")){
                    field.set(null, Double.valueOf(args[1]));
                }else if (type.equals("String")){
                    field.set(null, args[1]);
                }
                ChatUtils.sendChatMessage("§bChanged ["+args[0]+"] from "+currentValue+" to "+args[1]);


            }catch(NoSuchFieldException e){
                ChatUtils.sendChatMessage("§cInvalid argument: " + args[0]);
            }catch(IllegalAccessException e){
                ChatUtils.sendChatMessage("§cIllegal access (Most likely private");
            }catch(IllegalFormatException e ){
             ChatUtils.sendChatMessage("§cWrong type");
            }catch(Exception e){
                LogUtils.error(e);
                ChatUtils.sendChatMessage("§cSomething went wrong... Command [/srmdebug "+args[0]+ " "+ args[1]+"]");
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {return 0;}
}
