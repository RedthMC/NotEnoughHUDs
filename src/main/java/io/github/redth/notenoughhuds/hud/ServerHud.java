package io.github.redth.notenoughhuds.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.redth.notenoughhuds.config.option.NehBoolean;
import io.github.redth.notenoughhuds.config.option.NehColor;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class ServerHud extends BaseHud {
    public static final ServerInfo PLACEHOLDER = new ServerInfo("Hypixel", "mc.hypixel.net", false);
    public static final Identifier ICON = new Identifier("notenoughhuds/server_icon");
    public static final Identifier UNKNOWN = new Identifier("textures/misc/unknown_server.png");
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehColor nameColor = new NehColor("show_name", "FFFFFFFF");
    public static NativeImageBackedTexture texture = null;
    private static ServerInfo server;
    private static boolean loadIcon;
    private static boolean loaded;

    public ServerHud() {
        super("server", Alignment.LEFT, Alignment.CENTER, 0, 0);
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(nameColor);
    }

    @Override
    public void tick() {
        server = getServer();
        super.tick();
    }

    public static ServerInfo getServer() {
        return isEditing() && mc.getCurrentServerEntry() == null ? PLACEHOLDER : mc.getCurrentServerEntry();
    }

    @Override
    public void render(MatrixStack matrix) {
        if (server == null) {
            loadIcon = true;
            return;
        }
        if (loadIcon) {
            loadIcon(server);
            loadIcon = false;
        }
        drawBg(matrix, backgroundColor);

        mc.getTextureManager().bindTexture(loaded ? ICON : UNKNOWN);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        drawTexture(matrix, 2, 2, 0, 0, 32, 32);
        RenderSystem.disableBlend();

        drawString(matrix, server.address, 36, 15, nameColor.asColor(), textShadow.get());
    }

    public static void loadIcon(ServerInfo server) {
        loaded = false;
        if (server.getIcon() == null) return;
        try {
            NativeImage image = NativeImage.read(server.getIcon());

            if (image.getWidth() != 64 || image.getHeight() != 64) return;
            if (texture == null) {
                texture = new NativeImageBackedTexture(image);
            } else {
                texture.setImage(image);
                texture.upload();
            }
            mc.getTextureManager().registerTexture(ICON, texture);
            loaded = true;
        } catch (IOException ignored) {
        }
    }

    @Override
    protected int getWidth() {
        return server == null ? 0 : mc.textRenderer.getWidth(server.address) + 38;
    }

    @Override
    protected int getHeight() {
        return server == null ? 0 : 36;
    }

}
