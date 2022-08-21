package io.github.reclqtch.notenoughhuds.hud;

import com.google.common.collect.ImmutableList;
import io.github.reclqtch.notenoughhuds.config.option.NehBoolean;
import io.github.reclqtch.notenoughhuds.config.option.NehColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackHud extends BaseHud {
    public static final ResourceLocation PLACEHOLDER = new ResourceLocation("textures/misc/unknown_pack.png");
    public static final List<DefaultPack> DEFAULT_PACK = ImmutableList.of(new DefaultPack());
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehColor nameColor = new NehColor("show_name", "FFFFFFFF");
    private static List<DefaultPack> packs = Collections.emptyList();

    public PackHud() {
        super("pack");
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(nameColor);
    }

    @Override
    public void tick() {
        packs = getPacks();
        super.tick();
    }

    public static List<DefaultPack> getPacks() {
        List<ResourcePackRepository.Entry> packs = mc.getResourcePackRepository().getRepositoryEntries();
        if (isEditing() && packs.isEmpty()) return DEFAULT_PACK;
        return packs.stream().collect(ArrayList::new, (l, e) -> l.add(new Pack(e)), List::addAll);
    }

    @Override
    public void render() {
        if (packs.isEmpty()) return;
        drawBg(backgroundColor);
        int x = 2;
        int y = 2;
        for (DefaultPack pack : packs) {
            try {
                pack.bindTexture();
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
            drawString(pack.getName(), x, y + getHeight() / 2.0F - 4.0F, nameColor.asInt(), textShadow.get());
        }
    }

    @Override
    protected int getWidth() {
        if (packs.isEmpty()) return 0;
        int i = 0;
        for (DefaultPack pack : packs) {
            int j = mc.fontRendererObj.getStringWidth(pack.getName());
            if (i < j) i = j;
        }
        return i + 36;
    }

    @Override
    protected int getHeight() {
        if (packs.isEmpty()) return 0;
        return packs.size() * 36;
    }

    public static class DefaultPack {
        public String getName() {
            return "Default";
        }

        public void bindTexture() {
            mc.getTextureManager().bindTexture(PLACEHOLDER);
        }
    }

    public static class Pack extends DefaultPack {
        public final ResourcePackRepository.Entry pack;

        public Pack(ResourcePackRepository.Entry pack) {
            this.pack = pack;
        }

        @Override
        public String getName() {
            return pack.getResourcePackName();
        }

        @Override
        public void bindTexture() {
            pack.bindTexturePackIcon(mc.getTextureManager());
        }
    }
}
