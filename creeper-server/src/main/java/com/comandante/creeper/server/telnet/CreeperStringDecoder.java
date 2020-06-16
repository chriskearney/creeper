package com.comandante.creeper.server.telnet;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import java.nio.charset.Charset;

@ChannelHandler.Sharable
public class CreeperStringDecoder extends OneToOneDecoder {

    private final Charset charset;

    public CreeperStringDecoder() {
        this(Charset.defaultCharset());
    }

    public CreeperStringDecoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }


    public CreeperStringDecoder(String charsetName) {
        this(Charset.forName(charsetName));
    }

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }
        return ((ChannelBuffer) msg).toString(charset);
    }
}
