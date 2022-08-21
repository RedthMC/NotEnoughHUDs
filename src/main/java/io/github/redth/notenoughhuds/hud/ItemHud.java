package io.github.redth.notenoughhuds.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public abstract class ItemHud extends BaseHud {
    private float x1;
    private float y1;

    public ItemHud(String id, Alignment defaultHorAlign, Alignment defaultVerAlign, int defaultXOffset, int defaultYOffset) {
        super(id, defaultHorAlign, defaultVerAlign, defaultXOffset, defaultYOffset);
    }

    @Override
    public void render(MatrixStack matrix, int x, int y, boolean centered) {
        if (scaledWidth == 0.0F || scaledHeight == 0.0F) return;
        if (centered) {
            x -= scaledWidth / 2.0F;
            y -= scaledHeight / 2.0F;
        }
        matrix.push();
        matrix.translate(x, y, 0.0F);
        matrix.scale(scale.get(), scale.get(), 1.0F);
        x1 = x;
        y1 = y;
        render(matrix);
        matrix.pop();
    }

    public void pushDrawItem() {
        MatrixStack matrix = RenderSystem.getModelViewStack();
        matrix.push();
        matrix.translate(x1, y1, 0.0F);
        matrix.scale(scale.get(), scale.get(), 1.0F);
        matrix.translate(0.0D, 0.0D, 32.0D);
        RenderSystem.applyModelViewMatrix();
    }

    public void drawItem(ItemStack stack, int x, int y, String amountText) {
        ItemRenderer ir = mc.getItemRenderer();
        this.setZOffset(200);
        ir.zOffset = 200.0F;
        ir.renderInGuiWithOverrides(stack, x, y);
        ir.renderGuiItemOverlay(mc.textRenderer, stack, x, y, amountText);
        this.setZOffset(0);
        ir.zOffset = 0.0F;
    }

    public void popDrawItem() {
        RenderSystem.getModelViewStack().pop();
        RenderSystem.applyModelViewMatrix();
    }

}
