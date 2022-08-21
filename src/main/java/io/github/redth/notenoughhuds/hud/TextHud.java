package io.github.redth.notenoughhuds.hud;

import io.github.redth.notenoughhuds.config.option.NehBoolean;
import io.github.redth.notenoughhuds.config.option.NehColor;
import io.github.redth.notenoughhuds.config.option.NehInteger;
import io.github.redth.notenoughhuds.config.option.NehString;
import net.minecraft.client.util.math.MatrixStack;


public class TextHud extends BaseHud {
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehBoolean showBg = new NehBoolean("show_background", true);
    public final NehInteger bgWidth = new NehInteger("background_width", 56, 10, 110);
    public final NehInteger bgHeight = new NehInteger("background_height", 18, 10, 110);
    public final NehColor textColor = new NehColor("text_color", "FFFFFFFF");
    public final NehString format;
    public String text;

    public TextHud(String id, Alignment defaultHorAlign, Alignment defaultVerAlign, int defaultX, int defaultY, String defaultFormat) {
        super(id, defaultHorAlign, defaultVerAlign, defaultX, defaultY);
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(showBg);
        options.add(bgWidth);
        options.add(bgHeight);
        options.add(textColor);
        options.add(this.format = new NehString("format", defaultFormat));
    }

    public TextHud() {
        this("text", Alignment.LEFT, Alignment.TOP, 2, 2, "Plain Text");
    }

    @Override
    public void tick() {
        text = getText();
        super.tick();
    }

    protected String getText() {
        return format.get();
    }

    @Override
    protected int getWidth() {
        return showBg.get() ? bgWidth.get() : mc.textRenderer.getWidth(text) + 2;
    }

    @Override
    protected int getHeight() {
        return showBg.get() ? bgHeight.get() : 9;
    }

    @Override
    public void render(MatrixStack matrix) {
        if (showBg.get()) {
            drawBg(matrix, backgroundColor);
            drawString(matrix, text, getWidth() / 2.0f, getHeight() / 2.0f - 4, textColor.asColor(), textShadow.get(), Alignment.CENTER);
        } else {
            drawString(matrix, text, 1, 0, textColor.asColor(), textShadow.get());
        }
    }
}
