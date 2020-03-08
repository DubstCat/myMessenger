package com.example.mymessenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.function.Consumer;


public class MainActivity extends AppCompatActivity {



    Server server;
    MessageController controller;

    String myName;

    public MainActivity() throws Exception {
    }
    //TextView t = findViewById(R.id.textView);


    @Override
    protected void onStart(){
        super.onStart();

        server = new Server(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(final Pair<String, String> pair) {
                // Сюда приходят сообщения
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        controller.addMessage(
                                new MessageController.Message(
                                        pair.first,
                                        pair.second,
                                        false
                                )

                        );
                    }
                });
            }
        }, new Consumer<Pair<String, String>>() {
            @Override
            public void accept(final Pair<String, String> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = String.format("From %s: %s",pair.second,pair.first );
                        Toast.makeText(MainActivity.this,text,Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
        server.connect();
    }

    @Override
    protected void onStop(){
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {  //Вызывается при старте окошка (Activity)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  //Интерфейс этого окошка лежит в файле activity_main



        final EditText chatMessage = findViewById(R.id.chatMessage);
        Button sendButton = findViewById(R.id.sendButton);


        RecyclerView charWindow = findViewById(R.id.chatWindow);

        controller = new MessageController();

        controller
                .setIncomingLayout(R.layout.message)
                .setOutgoingLayout(R.layout.outgoing_message)
                .setMessageTextId(R.id.messageText)
                .setUserNameId(R.id.username)
                .setMessageTimeId(R.id.messageTime)
                .appendTo(charWindow,this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            //Код, который вызывается при клике на кнопку
                String userMessage =chatMessage.getText().toString();
                if (!userMessage.equals("")){
                controller.addMessage(
                        new MessageController.Message(
                                userMessage,
                                "Вы",
                                true
                        )
                );
                server.sendMessage(userMessage);

                chatMessage.setText("");}

            }
        });
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Enter your name: ");
        final EditText nameInput = new EditText(this);
        builder.setView(nameInput);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myName = nameInput.getText().toString();
                server.sendName(myName);

            }
        });
        builder.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_toast :
                Toast.makeText(this,"Вы нажали на менюшку",Toast.LENGTH_SHORT).show();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

//TEST
    /*try

    {
        String text = "Всем привет, это skillbox!";
        String shifr = Crypto.encrypt(text);
        Log.i("CRYPTO", "SHIFR" + shifr);
        String deshifr = Crypto.decrypt(shifr);
        Log.i("CRYPTO", "DESHIFR" + deshifr);
    } catch (Exception E){
        E.printStackTrace();
    }*/



}
