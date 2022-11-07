package me.redth.notenoughhuds;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class PacketHandler extends ChannelInboundHandlerAdapter {
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof S03PacketTimeUpdate) {
            neh.tpsHud.onTimeUpdate();
        }
        super.channelRead(ctx, msg);
    }

}
