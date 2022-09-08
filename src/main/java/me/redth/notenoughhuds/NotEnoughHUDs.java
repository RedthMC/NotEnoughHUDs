package me.redth.notenoughhuds;

import me.redth.notenoughhuds.config.NehCommand;
import me.redth.notenoughhuds.config.NehConfig;
import me.redth.notenoughhuds.gui.EditorScreen;
import me.redth.notenoughhuds.hud.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.util.ActionResult;

public class NotEnoughHUDs implements ClientModInitializer {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    private static NotEnoughHUDs instance;
    public boolean showMenu;
    public HudManager hudManager;
    public NehConfig config;
    public ComboHud comboHud;
    public EffectHud effectHud;
    public EnderchestHud enderchestHud;
    public KeystrokesHud keystrokesHud;
    public PingHud pingHud;
    public ReachHud reachHud;
    public ScoreboardHud scoreboardHud;
    public ServerHud serverHud;
    public SprintHud sprintHud;
    public TpsHud tpsHud;

    public NotEnoughHUDs() {
        instance = this;
    }

    public static NotEnoughHUDs getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        config = new NehConfig();
        hudManager = new HudManager();
        hudManager.register(new ArmorHud());
        hudManager.register(new CoordsHud());
        hudManager.register(comboHud = new ComboHud());
        hudManager.register(new CpsHud());
        hudManager.register(new DirectionHud());
        hudManager.register(new EnderchestHud());
        hudManager.register(new InventoryHud());
        hudManager.register(effectHud = new EffectHud());
        hudManager.register(new FpsHud());
        hudManager.register(keystrokesHud = new KeystrokesHud());
        hudManager.register(new PackHud());
        hudManager.register(pingHud = new PingHud());
        hudManager.register(reachHud = new ReachHud());
        hudManager.register(scoreboardHud = new ScoreboardHud());
        hudManager.register(serverHud = new ServerHud());
        hudManager.register(new TimeHud());
        hudManager.register(new TextHud());
        hudManager.register(sprintHud = new SprintHud());
        hudManager.register(tpsHud = new TpsHud());

        config.load();


        HudRenderCallback.EVENT.register(((matrix, tickDelta) -> {
            BaseHud.screenWidth = mc.getWindow().getScaledWidth();
            BaseHud.screenHeight = mc.getWindow().getScaledHeight();
            for (BaseHud hud : hudManager.getEnabledHuds()) {
                hud.renderScaled(matrix);
            }
        }));

        ClientCommandRegistrationCallback.EVENT.register(NehCommand::register);

        ClientTickEvents.START_CLIENT_TICK.register(c -> {
            if (showMenu) {
                c.setScreenAndRender(new EditorScreen(c.currentScreen));
                showMenu = false;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if (MinecraftClient.isHudEnabled() || BaseHud.isEditing()) for (BaseHud hud : hudManager.getEnabledHuds()) {
                hud.tick();
            }
        });
        AttackEntityCallback.EVENT.register(((player, world, hand, entity, hitResult) -> {
            if (reachHud.isEnabled()) reachHud.updateReach(hitResult);
            return ActionResult.PASS;
        }));
        ScreenEvents.AFTER_INIT.register((client, gui, width, height) -> {
            if (gui instanceof GenericContainerScreen chest && chest.getScreenHandler().getInventory() instanceof EnderChestInventory inv) {
                enderchestHud.onECUpdate(inv);
            }
        });
    }

    public static void onTimeUpdate() {
        if (instance.tpsHud.isEnabled()) instance.tpsHud.onTimeUpdate();
    }

    // todo: cps, server, color picker, ec

}
