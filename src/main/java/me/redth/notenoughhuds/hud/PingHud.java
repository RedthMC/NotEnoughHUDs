package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.net.InetSocketAddress;
import java.util.Optional;

public class PingHud extends TextHud {
    public final NehBoolean useStatusPing = new NehBoolean("use_status_ping", false);
    public static ClientConnection cc;
    public long nextPingTime;

    public PingHud() {
        super("ping", "%ping% ms");
        options.add(useStatusPing);
    }

    @Override
    public void tick() {
        if (cc != null) cc.tick();
        super.tick();
    }

    @Override
    protected String getText() {
        return format.get().replaceAll("%ping%", String.valueOf(getPing()));
    }

    public int getPing() {
        if (useStatusPing.get()) {
            long l = Util.getMeasuringTimeMs();
            if (l > nextPingTime) {
                nextPingTime = l + 15000;
                new Thread(PingHud::ping).start();
            }
            return mc.getCurrentServerEntry() == null ? -1 : (int) mc.getCurrentServerEntry().ping;
        }
        if (mc.player == null) return 68;
        if (mc.getNetworkHandler() == null) return -1;
        PlayerListEntry info = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        if (info == null) return -1;
        return info.getLatency();

    }

    public static void ping() {
        ServerInfo server = mc.getCurrentServerEntry();
        if (server == null) return;
        try {
            ServerAddress address = ServerAddress.parse(server.address);
            Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);
            if (optional.isEmpty()) return;
            cc = ClientConnection.connect(optional.get(), false);
            cc.setPacketListener(new ClientQueryPacketListener() {
                public long timePinged;
                public boolean done;

                @Override
                public void onDisconnected(Text reason) {
                }

                @Override
                public ClientConnection getConnection() {
                    return cc;
                }

                @Override
                public void onResponse(QueryResponseS2CPacket packetIn) {
                    if (done) {
                        cc.disconnect(Text.of("unrequested"));
                        return;
                    }
                    done = true;
                    timePinged = Util.getMeasuringTimeMs();
                    cc.send(new QueryPingC2SPacket(timePinged));
                }

                @Override
                public void onPong(QueryPongS2CPacket packetIn) {
                    server.ping = Util.getMeasuringTimeMs() - timePinged;
                    cc.disconnect(Text.of("done"));
                }
            });
            cc.send(new HandshakeC2SPacket(address.getAddress(), address.getPort(), NetworkState.STATUS)); // handshake
            cc.send(new QueryRequestC2SPacket());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
