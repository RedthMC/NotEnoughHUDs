package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.config.option.NehInteger;
import me.redth.notenoughhuds.config.option.NehString;


public class TextHud extends BaseHud {
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehBoolean showBg = new NehBoolean("show_background", true);
    public final NehInteger bgWidth = new NehInteger("background_width", 56, 10, 110);
    public final NehInteger bgHeight = new NehInteger("background_height", 18, 10, 110);
    public final NehColor textColor = new NehColor("text_color", "FFFFFFFF");
    public final NehString format;
    public String text;

    public TextHud(String id, String defaultFormat) {
        super(id);
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(showBg);
        options.add(bgWidth);
        options.add(bgHeight);
        options.add(textColor);
        options.add(format = new NehString("format", defaultFormat));
    }

    public TextHud() {
        this("text", "Plain Text");
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
        return showBg.get() ? bgWidth.get() : mc.fontRendererObj.getStringWidth(text) + 2;
    }

    @Override
    protected int getHeight() {
        return showBg.get() ? bgHeight.get() : mc.fontRendererObj.FONT_HEIGHT;
    }

    @Override
    public void render() {
        if (showBg.get()) {
            drawBackground(backgroundColor);
            drawString(text, getWidth() / 2.0f, getHeight() / 2.0f - 4, textColor.asInt(), textShadow.get(), Alignment.CENTER);
        } else {
            drawString(text, 1, 0, textColor.asInt(), textShadow.get());
        }
    }
}
