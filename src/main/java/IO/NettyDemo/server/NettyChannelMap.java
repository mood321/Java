package IO.NettyDemo.server;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.socket.SocketChannel;

public class NettyChannelMap {

	//private static Map<String,String> idMap = new ConcurrentHashMap<String,String>();
	
	private static Map<String,SocketChannel> channelMap=new ConcurrentHashMap<String, SocketChannel>();
    public static String add(SocketChannel socketChannel){
    	ChannelId channelId = socketChannel.id();
    	String idStr = channelId.asLongText();
    	channelMap.put(idStr,socketChannel);
    	return idStr;
    }
    public static Channel get(String clientId){
       return channelMap.get(clientId);
    }
    public static void remove(SocketChannel socketChannel){
    	ChannelId channelId = socketChannel.id();
    	String idStr = channelId.asLongText();
    	channelMap.remove(idStr);
        /*for (Map.Entry entry:channelMap.entrySet()){
            if (entry.getValue()==socketChannel){
            	channelMap.remove(entry.getKey());
            }
        }*/
    }
    
    public static int getSize(){
    	return channelMap.size();
    }
    
    public static Set<String> getKeys(){
    	return channelMap.keySet();
    }
 
}
