package io.github.redth.notenoughhuds.gui.widget;

import io.github.redth.notenoughhuds.config.option.NehString;
import net.minecraft.SharedConstants;

public class StringWidget extends TextWidget {

    public StringWidget(int x, int y, NehString option) {
        super(x, y, option, 32, SharedConstants::isValidChar, SharedConstants::stripInvalidChars);
    }

}
