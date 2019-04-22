package IO.NettyDemo.client.handler;

import java.util.Date;

import IO.NettyDemo.message.BaseMsg;
import IO.NettyDemo.message.Constants;
import IO.NettyDemo.message.LoginMsg;
import IO.NettyDemo.message.LoginReplyMsg;
import IO.NettyDemo.message.MsgType;
import IO.NettyDemo.message.PingMsg;
import IO.NettyDemo.message.ReplyClientBody;
import IO.NettyDemo.message.ReplyMsg;
import IO.NettyDemo.message.ReplyServerBody;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class NettyClientHandler extends SimpleChannelInboundHandler<BaseMsg>{

    //利用写空闲发送心跳检测消息
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    PingMsg pingMsg=new PingMsg();
                    ctx.writeAndFlush(pingMsg);
                    System.out.println("send ping to server userEventTriggered --------"+new Date());
                    break;
                default:
                    break;
            }
        }
    }



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {
        MsgType msgType=baseMsg.getType();
        switch (msgType){
            case LOGIN:{
                //向服务器发起登录
                LoginMsg loginMsg=new LoginMsg();
                loginMsg.setPassword("yao");
                loginMsg.setUserName("robin");
                channelHandlerContext.writeAndFlush(loginMsg);
            }break;
            case PING:{
                System.out.println("receive ping from server----------"+new Date());
            }break;
            case ASK:{
                ReplyClientBody replyClientBody=new ReplyClientBody("client info **** !!!");
                ReplyMsg replyMsg=new ReplyMsg();
                replyMsg.setBody(replyClientBody);
                channelHandlerContext.writeAndFlush(replyMsg);
            }break;
            case REPLY:{
                ReplyMsg replyMsg=(ReplyMsg)baseMsg;
                ReplyServerBody replyServerBody=(ReplyServerBody)replyMsg.getBody();
                System.out.println("receive client msg: "+replyServerBody.getServerInfo());
            }break;
            case LOGIN_REPLY:{
                //ReplyClientBody replyClientBody=new ReplyClientBody("client info **** !!!");

                LoginReplyMsg loginReplyMsg = (LoginReplyMsg)baseMsg;
                Constants.setClientId(loginReplyMsg.getLoginToken());
            }break;

            default:break;
        }
        ReferenceCountUtil.release(msgType);
    }


}
