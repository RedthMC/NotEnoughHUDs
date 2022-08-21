package io.github.reclqtch.notenoughhuds.gui.widget;

import io.github.reclqtch.notenoughhuds.config.option.NehString;
import net.minecraft.util.ChatAllowedCharacters;

public class StringWidget extends TextWidget {

    public StringWidget(int x, int y, NehString option) {
        super(x, y, option, 32, ChatAllowedCharacters::isAllowedCharacter, ChatAllowedCharacters::filterAllowedCharacters);
    }

}
