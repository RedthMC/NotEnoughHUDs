package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;

public class PackHud extends BaseHud {
    public static final Identifier PLACEHOLDER = new Identifier("textures/misc/unknown_pack.png");
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehColor nameColor = new NehColor("show_name", "FFFFFFFF");
    private static Collection<ResourcePackProfile> packs = Collections.emptyList();

    public PackHud() {
        super("pack");
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(nameColor);
    }

    @Override
    public void tick() {
        packs = mc.getResourcePackManager().getEnabledProfiles();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrix) {
        if (packs.isEmpty()) return;
        drawBackground(matrix,backgroundColor);
        int x = 2;
        int y = 2;
        for (ResourcePackProfile pack : packs) {
//            try {
//                pack.bindTexture();
//            } catch (Throwable e) {
//                mc.getTextureManager().bindTexture(PLACEHOLDER);
//            }
//            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//            GlStateManager.enableBlend();
//            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//            GlStateManager.blendFunc(770, 771);
//            drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
//            GlStateManager.disableBlend();
            x += 34;
            drawText(matrix, pack.getDisplayName(), x, y + getHeight() / 2.0F - 4.0F, nameColor.asColor(), textShadow.get());
        }
    }

    @Override
    protected int getWidth() {
        if (packs.isEmpty()) return 0;
        int i = 0;
        for (ResourcePackProfile pack : packs) {
            int j = mc.textRenderer.getWidth(pack.getDisplayName());
            if (i < j) i = j;
        }
        return i + 36;
    }

    @Override
    protected int getHeight() {
        if (packs.isEmpty()) return 0;
        return packs.size() * 36;
    }

}
