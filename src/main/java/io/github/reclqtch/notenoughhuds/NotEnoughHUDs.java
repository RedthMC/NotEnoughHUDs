package io.github.reclqtch.notenoughhuds;

import io.github.reclqtch.notenoughhuds.config.NehConfig;
import io.github.reclqtch.notenoughhuds.gui.EditorScreen;
import io.github.reclqtch.notenoughhuds.hud.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = "notenoughhuds", version = "1.0", clientSideOnly = true)
public class NotEnoughHUDs {
    public static final Minecraft mc = Minecraft.getMinecraft();
    private static NotEnoughHUDs instance;
    public KeyBinding guiKey;
    public HudManager hudManager;
    public NehConfig config;
    public ComboHud comboHud;
    public KeystrokesHud keystrokesHud;
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
        ClientRegistry.registerKeyBinding(guiKey = new KeyBinding("key.notenoughhuds.open_editor", 0x36, "key.category.misc")); // rshift

        config = new NehConfig();
        hudManager = new HudManager();
        hudManager.register(new ArmorHud());
        hudManager.register(new CoordsHud());
        hudManager.register(comboHud = new ComboHud());
        hudManager.register(new CpsHud());
        hudManager.register(new DirectionHud());
        hudManager.register(new EffectHud());
        hudManager.register(new FpsHud());
        hudManager.register(keystrokesHud = new KeystrokesHud());
        hudManager.register(new InventoryHud());
        hudManager.register(new EnderChestHud());
        hudManager.register(new PackHud());
        hudManager.register(pingHud = new PingHud());
        hudManager.register(reachHud = new ReachHud());
        hudManager.register(new ScoreboardHud());
        hudManager.register(serverHud = new ServerHud());
        hudManager.register(new TimeHud());
        hudManager.register(new TextHud());
        hudManager.register(sprintHud = new SprintHud());
        hudManager.register(tpsHud = new TpsHud());

        config.load();
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
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            if (guiKey.isPressed()) {
                mc.displayGuiScreen(new EditorScreen(mc.currentScreen));
            }
        } else if (mc.theWorld != null || BaseHud.isEditing()) for (BaseHud hud : hudManager.getEnabledHuds()) {
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
    public void onHitEntity(AttackEntityEvent e) {
        if (reachHud.isEnabled())
            reachHud.updateReach(e);
    }

    @SubscribeEvent
    public void onHurtEntity(LivingHurtEvent e) {
        if (comboHud.isEnabled())
            comboHud.updateCombo(e);
    }

    @SuppressWarnings("unused")
    public static void onTimeUpdate() {
        instance.tpsHud.onTimeUpdate();
    }

    //todo: condition
}
