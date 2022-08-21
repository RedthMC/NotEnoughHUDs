package io.github.redth.notenoughhuds.hud;

import io.github.redth.notenoughhuds.config.option.NehBoolean;
import io.github.redth.notenoughhuds.config.option.NehColor;
import io.github.redth.notenoughhuds.config.option.NehString;
import net.minecraft.client.util.math.MatrixStack;

public class SprintHud extends BaseHud {
    public final NehColor textColor = new NehColor("text_color", "FFFFFFFF");
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehString vanilla = new NehString("vanilla", "Sprinting [Vanilla]");
    public final NehString toggled = new NehString("toggled", "Sprinting [Toggled]");
    private String text;

    public SprintHud() {
        super("sprint", Alignment.LEFT, Alignment.TOP, 2, 2);
        options.add(textColor);
        options.add(textShadow);
        options.add(vanilla);
        options.add(toggled);
    }

    @Override
    public void tick() {
        text = getText();
        super.tick();
    }

    @Override
    protected int getWidth() {
        return mc.textRenderer.getWidth(text) + 2;
    }

    @Override
    protected int getHeight() {
        return 9;
    }

    @Override
    public void render(MatrixStack matrix) {
        drawString(matrix, text, 1, 0, textColor.asColor(), textShadow.get());
    }

    private String getText() {
        if (isEditing() || (mc.options.getSprintToggled().getValue() && mc.options.sprintKey.isPressed())) return toggled.get();
        return mc.player != null && mc.player.isSprinting() ? vanilla.get() : "";
    }

}
