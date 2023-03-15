package me.redth.notenoughhuds.config;

import com.google.common.collect.ImmutableList;
import me.redth.notenoughhuds.NotEnoughHUDs;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class NehCommand extends CommandBase {
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();

    @Override
    public String getCommandName() {
        return "notenoughhuds";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/notenoughhuds [reload]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 1 && "reload".equals(args[0])) {

            neh.config.load();
            sender.addChatMessage(new ChatComponentText("\u00a78[\u00a7aNEH\u00a78] \u00a7aConfig Reloaded!"));
            sender.addChatMessage(new ChatComponentText(Minecraft.getMinecraft().thePlayer.getNBTTagCompound().toString()));
        } else NotEnoughHUDs.showScreen = true;
    }

    @Override
    public List<String> getCommandAliases() {
        return ImmutableList.of("neh");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return ImmutableList.of("reload");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
