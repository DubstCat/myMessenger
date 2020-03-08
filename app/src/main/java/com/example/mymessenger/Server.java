package com.example.mymessenger;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Server {
    WebSocketClient client;

    //Наш MainActivity - это потребитель сообщения
    private Consumer<Pair<String,String>> messageConsumer;
    private Consumer<Pair<String,String>> privateMessageConsumer;

    private Map<Long,String> nameMap = new ConcurrentHashMap<>();



    public Server(Consumer<Pair<String, String>> messageConsumer,Consumer<Pair<String,String>> privateMessageConsumer) {
        this.messageConsumer = messageConsumer;
        this.privateMessageConsumer = privateMessageConsumer;
    }

    //Будем вызывать когда нужно подключиться
    public void connect(){
        URI addr;
        try {
        addr = new URI("ws://35.210.129.230:8881/");
        }catch (URISyntaxException e){
            e.printStackTrace();
            return;
        }
client = new WebSocketClient(addr) {
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        // При подключении
        Log.i("WSSERVER","Connected to server");
    }

    @Override
    public void onMessage(String json) {
        int type = Protocol.getType(json);
        if (type == Protocol.MESSAGE){
            //показать сообщение
            onIncomingTextMessage(json);
        }
        if (type == Protocol.USER_STATUS){
            //Обновить статус пользователя
            onStatusUpdate(json);
        }
        Log.i("WSSERVER","Received message"+ json );
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i("WSSERVER","Connection closed");
    }

    @Override
    public void onError(Exception ex) {
        Log.i("WSSERVER","Error occured: "+ex.getMessage());
    }
};
        client.connect();
    }
    public int count=0;
    public String str1;
    ;
    private void onStatusUpdate(String json){
        Protocol.UserStatus status = Protocol.unpackStatus(json);
        Protocol.User u = status.getUser();
        if (status.isConnected()){ //Новый пользователь подключился
           // Toast.makeText(mn.context,"ККК",Toast.LENGTH_SHORT);
            nameMap.put(u.getId(),u.getName());
            count++;

        }
        else {
          nameMap.remove(u.getId());
            count--;


        }
    }

    private void onIncomingTextMessage(String json){
        Protocol.Message message = Protocol.unpackMessage(json);
        String name = nameMap.get(message.getSender());
        if (name == null){
            name ="Unnamed";
        }
        String text = null;
        try {
            text = Crypto.decrypt(message.getEncodedText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(message.getReceiver()==Protocol.Message.GROUP_CHAT){
        messageConsumer.accept(new Pair<>(text,name));
        }else{
            privateMessageConsumer.accept(new Pair<>(text,name));
        }

    }

    public void sendMessage(String messageText){
        long receiver = Protocol.Message.GROUP_CHAT;
        if (messageText.contains("@")){
            String name = messageText.split("@")[0].trim();

            for(Long id:nameMap.keySet()){
                if(nameMap.get(id).equals(name)) {
                    receiver = id;
                    break;
                }
            }
        }
        try {
            messageText = Crypto.encrypt(messageText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Protocol.Message m = new Protocol.Message(messageText);
        m.setReceiver(receiver);
        String json = Protocol.packMessage(m);
        if (client != null && client.isOpen()) {
            client.send(json);
        }
    }

    public void sendName(String name){
        String json = Protocol.packName(
                new Protocol.UserName(name)
        );
        if (client != null && client.isOpen()) {
            client.send(json);

        }
    }


}
