package io.github.redth.notenoughhuds.gui;

import io.github.redth.notenoughhuds.NotEnoughHUDs;
import io.github.redth.notenoughhuds.gui.widget.FlatButton;
import io.github.redth.notenoughhuds.hud.BaseHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class EditorScreen extends Screen {
    public static final int HOVERING_COLOR = 0x80FFFFFF;
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final Screen parent;
    //    private final Snapper snapper;
    private BaseHud hovering;
    private BaseHud dragging;
    private int xOffset;
    private int yOffset;

    public EditorScreen(Screen parent) {
        super(Text.of("NotEnoughHUDs"));
        this.parent = parent;
//        snapper = new Snapper(this);
    }

    @Override
    protected void init() {
        client.keyboard.setRepeatEvents(true);
        addDrawableChild(new FlatButton(width / 2 - 100, height / 2 + 12, 200, 20, "settings", b -> client.setScreenAndRender(new SettingsScreen(this))));
        addDrawableChild(new FlatButton(width / 2 - 100, height / 2 + 34, 200, 20, "back", b -> client.setScreenAndRender(parent)));
        addDrawableChild(new FlatButton(width / 2 - 100, height / 2 + 56, 200, 20, "reload_config", b -> neh.config.load()));
    }

    @Override
    public void removed() {
        client.keyboard.setRepeatEvents(false);
        neh.config.save();
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        hovering = null;
        for (BaseHud hud : neh.hudManager.getEnabledHuds()) {
            if (hud.getX() <= mouseX && mouseX < hud.getX() + hud.getScaledWidth() && hud.getY() <= mouseY && mouseY < hud.getY() + hud.getScaledHeight()) {
                hovering = hud;
                break;
            }
        }

        if (dragging != null) {
//            updateSnaps(mouseX, mouseY);
//            dragging.setX(snapper.getSnappedX());
//            dragging.setY(snapper.getSnappedY());

            dragging.drawPad(matrix, HOVERING_COLOR);
//            snapper.drawLines(matrix);
        } else if (hovering != null) hovering.drawPad(matrix, HOVERING_COLOR);
        drawCenteredText(matrix, textRenderer, "NotEnoughHUDs", width / 2, height / 2 - 26, 16777215);
        super.render(matrix, mouseX, mouseY, delta);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (dragging != null) {
            dragging.setX((int) mouseX + xOffset);
            dragging.setY((int) mouseY + yOffset);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (hovering != null) {
            switch (button) {
                case 0 -> {
                    dragging = hovering;
//                    snapper.setCurrent(dragging = hovering);
                    xOffset = dragging.getX() - (int) mouseX;
                    yOffset = dragging.getY() - (int) mouseY;
//                    updateSnaps(mouseX, mouseY);
                }
                case 1 -> {
                    client.setScreenAndRender(new SettingsScreen(this, hovering));
                    hovering = null;
                }
                case 2 -> {
                    hovering.setEnabled(false);
                    hovering = null;
                }
            }

        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) return true;
        if (button == 0) dragging = null;
//        if (button == 0) snapper.setCurrent(dragging = null);
        return false;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (dragging == null && hovering != null) switch (keyCode) {
            case InputUtil.GLFW_KEY_UP -> hovering.setY(hovering.getY() - 1);
            case InputUtil.GLFW_KEY_DOWN -> hovering.setY(hovering.getY() + 1);
            case InputUtil.GLFW_KEY_LEFT -> hovering.setX(hovering.getX() - 1);
            case InputUtil.GLFW_KEY_RIGHT -> hovering.setX(hovering.getX() + 1);
        }
        return false;
    }

//    public void updateSnaps(double mouseX, double mouseY) {
//        snapper.updateSnaps((int) mouseX + xOffset, (int) mouseY + yOffset);
//    }
}
