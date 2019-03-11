package NettyDemo.server.handler;

import java.util.Date;

import NettyDemo.message.AskMsg;
import NettyDemo.message.BaseMsg;
import NettyDemo.message.LoginMsg;
import NettyDemo.message.LoginReplyMsg;
import NettyDemo.message.MsgType;
import NettyDemo.message.PingMsg;
import NettyDemo.message.ReplyClientBody;
import NettyDemo.message.ReplyMsg;
import NettyDemo.message.ReplyServerBody;
import NettyDemo.server.NettyChannelMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

public class NettyServerHandler extends SimpleChannelInboundHandler<BaseMsg> {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //channel失效，从Map中移除
        NettyChannelMap.remove((SocketChannel)ctx.channel());
    }



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {

        if(MsgType.LOGIN.equals(baseMsg.getType())){
            LoginMsg loginMsg=(LoginMsg)baseMsg;
            if("robin".equals(loginMsg.getUserName())&&"yao".equals(loginMsg.getPassword())){
                //登录成功,把channel存到服务端的map中
                String loginToken = NettyChannelMap.add((SocketChannel)channelHandlerContext.channel());
                System.out.println("client"+loginMsg.getUserName()+" 登录成功");
                LoginReplyMsg loginReplyMsg = new LoginReplyMsg();
                loginReplyMsg.setSuccese(true);
                loginReplyMsg.setLoginToken(loginToken);
                channelHandlerContext.channel().writeAndFlush(loginReplyMsg);
            }else{
                System.out.println("client"+loginMsg.getUserName()+" 登录失败");
                LoginReplyMsg loginReplyMsg = new LoginReplyMsg();
                loginReplyMsg.setSuccese(false);
                //loginReplyMsg.setLoginToken(loginToken);
                channelHandlerContext.channel().writeAndFlush(loginReplyMsg);
            }
            return;
        }else{
            String clientId = baseMsg.getClientId();
            if(clientId == null){
                //说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
                LoginMsg loginMsg=new LoginMsg();
                channelHandlerContext.channel().writeAndFlush(loginMsg);
                return ;
            }
            Channel channel = NettyChannelMap.get(clientId);
            if(channel==null){
                //说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
                LoginMsg loginMsg=new LoginMsg();
                channelHandlerContext.channel().writeAndFlush(loginMsg);
                return;
            }

            Channel currentChannel = channelHandlerContext.channel();
            if(currentChannel != channel){
                //登陆id不匹配
                LoginMsg loginMsg=new LoginMsg();
                channelHandlerContext.channel().writeAndFlush(loginMsg);
                return;
            }


        }
        switch (baseMsg.getType()){
            case PING:{
                PingMsg pingMsg=(PingMsg)baseMsg;
                PingMsg replyPing=new PingMsg();
                System.out.println("receive client ping:keepalive clientId="+baseMsg.getClientId()+" "+new Date());
                NettyChannelMap.get(pingMsg.getClientId()).writeAndFlush(replyPing);
            }break;
            case ASK:{
                //收到客户端的请求
                AskMsg askMsg=(AskMsg)baseMsg;
                if("authToken".equals(askMsg.getParams().getAuth())){
                    ReplyServerBody replyBody=new ReplyServerBody("server info $$$$ !!!");
                    ReplyMsg replyMsg=new ReplyMsg();
                    replyMsg.setBody(replyBody);
                    NettyChannelMap.get(askMsg.getClientId()).writeAndFlush(replyMsg);
                }
            }break;
            case REPLY:{
                //收到客户端回复
                ReplyMsg replyMsg=(ReplyMsg)baseMsg;
                ReplyClientBody clientBody=(ReplyClientBody)replyMsg.getBody();
                System.out.println("receive client msg: "+clientBody.getClientInfo());
            }break;
            case LOGOUT:{
                NettyChannelMap.remove((SocketChannel)channelHandlerContext.channel());

            }break;

            default:break;
        }
        ReferenceCountUtil.release(baseMsg);
    }
}
