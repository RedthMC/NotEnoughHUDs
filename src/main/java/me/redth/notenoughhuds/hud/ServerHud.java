package me.redth.notenoughhuds.hud;

import com.google.common.base.Charsets;
import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class ServerHud extends BaseHud {
    public static final ServerData PLACEHOLDER = new ServerData("Hypixel", "mc.hypixel.net", false);
    public static final ResourceLocation ICON = new ResourceLocation("notenoughhuds/server_icon");
    public static final ResourceLocation UNKNOWN = new ResourceLocation("textures/misc/unknown_server.png");
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehColor nameColor = new NehColor("show_name", "FFFFFFFF");
    public final DynamicTexture texture = new DynamicTexture(64, 64);
    private static ServerData server;
    private boolean loadIcon;
    private boolean loaded;

    public ServerHud() {
        super("server");
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(nameColor);
    }

    @Override
    public void tick() {
        server = getServer();
        super.tick();
    }

    public static ServerData getServer() {
        return isEditing() && mc.getCurrentServerData() == null ? PLACEHOLDER : mc.getCurrentServerData();
    }

    @Override
    public void render() {
        if (server == null) {
            loadIcon = true;
            return;
        }
        if (loadIcon) {
            loadIcon(server);
            loadIcon = false;
        }
        drawBg(backgroundColor);
        int x = 2;
        int y = 2;
        mc.getTextureManager().bindTexture(loaded ? ICON : UNKNOWN);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();
        drawString(server.serverIP, x + 34, y + 17 - 4.0F, nameColor.asInt(), textShadow.get());
    }

    public void loadIcon(ServerData server) {
        loaded = false;
        if (server.getBase64EncodedIconData() == null) return;
        ByteBuf bytebuf = Unpooled.copiedBuffer(server.getBase64EncodedIconData(), Charsets.UTF_8);
        ByteBuf bytebuf1 = Base64.decode(bytebuf);
        BufferedImage image;

        try {
            image = TextureUtil.readBufferedImage(new ByteBufInputStream(bytebuf1));
        } catch (Throwable throwable) {
            return;
        } finally {
            bytebuf.release();
            bytebuf1.release();
        }

        if (image.getWidth() != 64 || image.getHeight() != 64) return;
        mc.getTextureManager().loadTexture(ICON, texture);
        image.getRGB(0, 0, 64, 64, texture.getTextureData(), 0, 64);
        texture.updateDynamicTexture();
        loaded = true;
    }

    @Override
    protected int getWidth() {
        return server == null ? 0 : mc.fontRendererObj.getStringWidth(server.serverIP) + 38;
    }

    @Override
    protected int getHeight() {
        return server == null ? 0 : 36;
    }

}
