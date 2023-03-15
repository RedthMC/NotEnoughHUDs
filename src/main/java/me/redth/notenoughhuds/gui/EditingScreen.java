package me.redth.notenoughhuds.gui;

import me.redth.notenoughhuds.NotEnoughHUDs;
import net.minecraft.client.gui.GuiScreen;

public abstract class EditingScreen extends GuiScreen {
    protected static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    protected final GuiScreen parent;

    public EditingScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void displayParent() {
        mc.displayGuiScreen(parent);
    }
}
