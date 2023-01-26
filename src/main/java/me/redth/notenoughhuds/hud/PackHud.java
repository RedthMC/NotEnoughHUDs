package me.redth.notenoughhuds.hud;

import com.google.common.collect.Lists;
import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class PackHud extends BaseHud implements IResourceManagerReloadListener {
    public static final ResourceLocation PLACEHOLDER = new ResourceLocation("textures/misc/unknown_pack.png");
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    private static List<ResourcePackRepository.Entry> packs = Collections.emptyList();

    public PackHud() {
        super("pack");
        options.add(textShadow);
        options.add(backgroundColor);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        packs = Lists.reverse(mc.getResourcePackRepository().getRepositoryEntries());
    }

    @Override
    public void render() {
        if (packs.isEmpty()) return;
        drawBackground(backgroundColor);
        int x = 2;
        int y = 2;
        for (ResourcePackRepository.Entry pack : packs) {
            try {
                pack.bindTexturePackIcon(mc.getTextureManager());
            } catch (Throwable e) {
                mc.getTextureManager().bindTexture(PLACEHOLDER);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
            GlStateManager.disableBlend();
            x += 34;
            drawString(shrinkWithEllipse(pack.getResourcePackName(), 156), x, y + 1, 0xFFFFFFFF, textShadow.get());

            List<String> desc = mc.fontRendererObj.listFormattedStringToWidth(pack.getTexturePackDescription(), 156);

            if (desc.size() > 0) drawString(desc.get(0), x, y + 11, 0xFFFFFFFF, textShadow.get());
            if (desc.size() > 1) drawString(desc.get(1), x, y + 21, 0xFFFFFFFF, textShadow.get());
        }
    }

    public static String shrinkWithEllipse(String text, int width) {
        if (mc.fontRendererObj.getStringWidth(text) < width) return text;
        text = mc.fontRendererObj.trimStringToWidth(text, width - mc.fontRendererObj.getStringWidth("..."));
        return text + "...";
    }

    @Override
    protected int getWidth() {
        return packs.isEmpty() ? 0 : 190;
    }

    @Override
    protected int getHeight() {
        if (packs.isEmpty()) return 0;
        return packs.size() * 36;
    }

}
