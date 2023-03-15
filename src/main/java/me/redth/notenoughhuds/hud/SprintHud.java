package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.config.option.NehString;
import net.minecraft.client.settings.KeyBinding;

public class SprintHud extends Hud {
    public final NehBoolean toggleSprint = new NehBoolean("toggle_sprint", true);
    public final NehColor textColor = new NehColor("text_color", "FFFFFFFF");
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehString normal = new NehString("sprint_normal", "Sprinting [Vanilla]");
    public final NehString toggled = new NehString("sprint_toggled", "Sprinting [Toggled]");
    private final KeyBinding sprintKey;
    private boolean sprintToggled;

    public SprintHud() {
        super("sprint");
        options.add(toggleSprint);
        options.add(textColor);
        options.add(textShadow);
        options.add(normal);
        options.add(toggled);
        sprintKey = mc.gameSettings.keyBindSprint;
    }

    public void onInput(int keycode) {
        if (!isEnabled()) return;
        if (!toggleSprint.get()) return;
        if (keycode != mc.gameSettings.keyBindSprint.getKeyCode()) return;

        sprintToggled = !sprintToggled;
    }

    public void tick() {
        if (sprintToggled) {
            KeyBinding.setKeyBindState(sprintKey.getKeyCode(), toggleSprint.get());
            if (!toggleSprint.get()) sprintToggled = false;
        }
        super.tick();
    }

    @Override
    protected int getWidth() {
        return mc.fontRendererObj.getStringWidth(getText()) + 2;
    }

    @Override
    protected int getHeight() {
        return 9;
    }

    @Override
    public void render() {
        drawString(getText(), 1, 0, textColor.asInt(), textShadow.get());
    }

    private String getText() {
        if (isEditing() || sprintToggled) return toggled.get();
        return mc.thePlayer != null && mc.thePlayer.isSprinting() ? normal.get() : "";
    }

}
