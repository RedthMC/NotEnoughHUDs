package me.redth.notenoughhuds;

import io.netty.channel.ChannelPipeline;
import me.redth.notenoughhuds.config.NehCommand;
import me.redth.notenoughhuds.config.NehConfig;
import me.redth.notenoughhuds.gui.EditorScreen;
import me.redth.notenoughhuds.hud.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;


@Mod(modid = "notenoughhuds", version = "1.0.5", acceptedMinecraftVersions = "1.8.9", clientSideOnly = true)
public class NotEnoughHUDs {
    public static final Minecraft mc = Minecraft.getMinecraft();
    private static NotEnoughHUDs instance;
    public boolean showScreen;
    public HudManager hudManager;
    public NehConfig config;
    public EnderChestHud ecHud;
    public PackHud packHud;
    public PingHud pingHud;
    public ReachHud reachHud;
    public ServerHud serverHud;
    public SprintHud sprintHud;
    public TpsHud tpsHud;

    public NotEnoughHUDs() {
        instance = this;
    }

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
        hudManager.register(new KeystrokesHud());
        hudManager.register(new InventoryHud());
        hudManager.register(ecHud = new EnderChestHud());
        hudManager.register(packHud = new PackHud());
        hudManager.register(pingHud = new PingHud());
        hudManager.register(reachHud = new ReachHud());
        hudManager.register(new ScoreboardHud());
        hudManager.register(serverHud = new ServerHud());
        hudManager.register(new TimeHud());
        hudManager.register(new TextHud());
        hudManager.register(sprintHud = new SprintHud());
        hudManager.register(tpsHud = new TpsHud());

        config.load();

        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(packHud);
    }


    @SubscribeEvent
    public void onHudRender(RenderGameOverlayEvent.Post e) {
        if (e.type == RenderGameOverlayEvent.ElementType.ALL) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.disableAlpha();

            BaseHud.screenWidth = e.resolution.getScaledWidth();
            BaseHud.screenHeight = e.resolution.getScaledHeight();
            for (BaseHud hud : hudManager.getEnabledHuds()) {
                hud.renderScaled();
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.enableAlpha();
        }
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
        if (mc.theWorld != null || BaseHud.isEditing()) for (BaseHud hud : hudManager.getEnabledHuds()) {
            hud.tick();
        }
    }

    @SubscribeEvent
    public void onInput(InputEvent e) {
        if (sprintHud.isEnabled())
            sprintHud.onInput();
    }

    @SubscribeEvent
    public void onMouse(MouseEvent e) {
        CpsHud.updateCps(e);
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post e) {
        if (ecHud.isEnabled()) {
            ecHud.onInitGui(e);
        }
    }

    @SubscribeEvent
    public void onHitEntity(AttackEntityEvent e) {
        if (reachHud.isEnabled())
            reachHud.updateReach(e);
    }

    //todo: snapping
}
