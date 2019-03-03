package com.lnsoft.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created By Chr on 2019/3/3/0003.
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String request = (String) msg;
        System.out.println("From Client : " + request);
        String response = "From Server Hello : " + request;
        ctx.writeAndFlush(response);

    }
}