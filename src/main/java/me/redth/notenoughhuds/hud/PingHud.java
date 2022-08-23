package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.net.InetAddress;

public class PingHud extends TextHud {
    public final NehBoolean useStatusPing = new NehBoolean("use_status_ping", false);
    public NetworkManager nm;
    public long nextPingTime;

    public PingHud() {
        super("ping", "%ping% ms");
        options.add(useStatusPing);
    }

    @Override
    protected String getText() {
        return format.get().replaceAll("%ping%", String.valueOf(getPing()));
    }

    public int getPing() {
        if (useStatusPing.get()) {
            long l = Minecraft.getSystemTime();
            if (l > nextPingTime) {
                nextPingTime = l + 15000;
                new Thread(this::ping).start();
            }
            return mc.getCurrentServerData() == null ? -1 : (int) mc.getCurrentServerData().pingToServer;
        }
        if (mc.thePlayer == null) return 68;
        if (mc.getNetHandler() == null) return -1;
        NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
        if (info == null) return -1;
        return info.getResponseTime();

    }

    public void ping() {
        if (nm != null) {
            if (nm.isChannelOpen()) nm.processReceivedPackets();
            else nm.checkDisconnected();
        }
        ServerData server = mc.getCurrentServerData();
        if (server == null) return;
        try {
            ServerAddress address = ServerAddress.fromString(server.serverIP);
            nm = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(address.getIP()), address.getPort(), false);
            nm.setNetHandler(new INetHandlerStatusClient() {
                public long timePinged;
                public boolean done;

                @Override
                public void onDisconnect(IChatComponent reason) {
                }

                @Override
                public void handleServerInfo(S00PacketServerInfo packetIn) {
                    if (done) {
                        nm.closeChannel(new ChatComponentText("unrequested"));
                        return;
                    }
                    done = true;
                    timePinged = Minecraft.getSystemTime();
                    nm.sendPacket(new C01PacketPing(timePinged));
                }

                @Override
                public void handlePong(S01PacketPong packetIn) {
                    server.pingToServer = Minecraft.getSystemTime() - timePinged;
                    nm.closeChannel(new ChatComponentText("done"));
                }
            });
            nm.sendPacket(new C00Handshake(0, address.getIP(), address.getPort(), EnumConnectionState.STATUS)); // handshake
            nm.sendPacket(new C00PacketServerQuery());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
