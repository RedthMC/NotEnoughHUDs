package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehString;
import net.minecraft.util.ChatAllowedCharacters;

public class StringWidget extends TextWidget {

    public StringWidget(int x, int y, NehString option) {
        super(x, y, option, 32, ChatAllowedCharacters::isAllowedCharacter, ChatAllowedCharacters::filterAllowedCharacters);
    }

}
