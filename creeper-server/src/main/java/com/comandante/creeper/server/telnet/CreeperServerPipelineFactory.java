package com.comandante.creeper.server.telnet;


import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class CreeperServerPipelineFactory implements ChannelPipelineFactory {

    private final ChannelHandler handler;

    public CreeperServerPipelineFactory(ChannelHandler handler) {
        this.handler = handler;
    }

    public ChannelPipeline getPipeline() {
        ChannelPipeline pipeline = new DefaultChannelPipeline();
        pipeline.addLast("framer", new CreeperDelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new CreeperStringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("handler", handler);
        return pipeline;
    }
}
