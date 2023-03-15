package me.redth.notenoughhuds;

import io.netty.channel.ChannelPipeline;
import me.redth.notenoughhuds.config.NehCommand;
import me.redth.notenoughhuds.config.NehConfig;
import me.redth.notenoughhuds.gui.EditorScreen;
import me.redth.notenoughhuds.hud.*;
import me.redth.notenoughhuds.util.HudManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;


@Mod(modid = "notenoughhuds", version = "1.0.7", acceptedMinecraftVersions = "1.8.9", clientSideOnly = true)
public class NotEnoughHUDs {
    public static final Minecraft mc = Minecraft.getMinecraft();

    @Mod.Instance
    private static NotEnoughHUDs instance;

    public static boolean showScreen;
    public HudManager hudManager;
    public NehConfig config;
    public PingHud pingHud;
    public KeystrokesHud keystrokesHud;
    public ReachHud reachHud;
    public SprintHud sprintHud;
    public TpsHud tpsHud;

    public static NotEnoughHUDs getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new NehCommand());

        config = new NehConfig();
        hudManager = new HudManager();
        hudManager.register(new ArmorHud());
        hudManager.register(new CoordsHud());
        hudManager.register(new CpsHud());
        hudManager.register(new DirectionHud());
        hudManager.register(new EffectHud());
        hudManager.register(new FpsHud());
        hudManager.register(keystrokesHud = new KeystrokesHud());
        hudManager.register(new InventoryHud());
        hudManager.register(pingHud = new PingHud());
        hudManager.register(reachHud = new ReachHud());
        hudManager.register(new ScoreboardHud());
        hudManager.register(new SpeedHud());
        hudManager.register(new TimeHud());
        hudManager.register(new TextHud());
        hudManager.register(sprintHud = new SprintHud());
        hudManager.register(tpsHud = new TpsHud());

        config.load();
    }


    @SubscribeEvent
    public void onHudRender(RenderGameOverlayEvent.Post e) {
        if (e.type != RenderGameOverlayEvent.ElementType.ALL) return;
        Hud.screenWidth = e.resolution.getScaledWidth();
        Hud.screenHeight = e.resolution.getScaledHeight();
        hudManager.renderHuds();
    }

    @SubscribeEvent
    public void onServerJoined(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        ChannelPipeline pipeline = e.manager.channel().pipeline();
        if (pipeline.get("neh_handler") == null && pipeline.get("packet_handler") != null) {
            try {
                pipeline.addBefore("packet_handler", "neh_handler", new PacketHandler());
            } catch (Throwable ignored) {
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) return;
        if (showScreen) {
            mc.displayGuiScreen(new EditorScreen(mc.currentScreen));
            showScreen = false;
        }
        for (Hud hud : hudManager.getEnabledHuds()) {
            hud.shouldTick = true;
        }
    }

    @SubscribeEvent
    public void onInput(InputEvent e) {

        if (e instanceof InputEvent.MouseInputEvent) {
            if (!Mouse.getEventButtonState()) return;
            firstPress(Mouse.getEventButton() - 100);
        } else if (e instanceof InputEvent.KeyInputEvent) {
            if (!Keyboard.getEventKeyState()) return;
            firstPress(Keyboard.getEventKey());

        }

    }

    public void firstPress(int keycode) {
        CpsHud.updateCps(keycode);
        keystrokesHud.onInput(keycode);
        sprintHud.onInput(keycode);
    }

    @SubscribeEvent
    public void onHitEntity(AttackEntityEvent e) {
        reachHud.updateReach(e);
    }

}
