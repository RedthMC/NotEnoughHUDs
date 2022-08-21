package io.github.redth.notenoughhuds.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class FlatButton extends ButtonWidget {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public FlatButton(int x, int y, int width, int height, String transKey, PressAction onPress) {
        super(x, y, width, height, Text.translatable("screen.notenoughhuds." + transKey), onPress);
    }

    @Override
    public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        if (visible) {
            int j = 14737632;
            if (!active) {
                j = 10526880;
            } else if (hovered) {
                j = 16777120;
            }

            fill(matrix, x, y, x + width, y + height, hovered ? 0x80555555 : 0x80000000);
            drawCenteredTextWithShadow(matrix, mc.textRenderer, getMessage().asOrderedText(), x + width / 2, y + (height - 8) / 2, j);
        }
    }
}
