package io.github.redth.notenoughhuds.gui.widget;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class FlatButton extends ButtonWidget {
    public FlatButton(int x, int y, int width, int height, String transKey, PressAction onPress) {
        super(x, y, width, height, Text.translatable("screen.notenoughhuds." + transKey), onPress);
    }
}
