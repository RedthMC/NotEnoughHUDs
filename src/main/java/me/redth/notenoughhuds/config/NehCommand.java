package me.redth.notenoughhuds.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.redth.notenoughhuds.NotEnoughHUDs;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class NehCommand {
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("notenoughhuds")
                .executes(context -> execute())
                .then(ClientCommandManager.argument("args", StringArgumentType.string())
                        .suggests((context, builder) -> CompletableFuture.completedFuture(builder.suggest("reload").build()))
                        .executes(context -> execute(context.getSource(), StringArgumentType.getString(context, "args")))));
        dispatcher.register(ClientCommandManager.literal("neh")
                .executes(context -> execute())
                .then(ClientCommandManager.argument("args", StringArgumentType.string())
                        .suggests((context, builder) -> CompletableFuture.completedFuture(builder.suggest("reload").build()))
                        .executes(context -> execute(context.getSource(), StringArgumentType.getString(context, "args")))));
    }

    public static int execute() {
        neh.showMenu = true;
        return 1;
    }

    public static int execute(FabricClientCommandSource source, String args) {
        if ("reload".equals(args)) {
            neh.config.load();
            source.sendFeedback(Text.literal("\u00a78[\u00a7aNEH\u00a78] \u00a7aConfig Reloaded!"));
            return 1;
        }
        return execute();
    }
}
