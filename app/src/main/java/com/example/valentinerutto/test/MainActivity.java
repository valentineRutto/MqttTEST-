package com.example.valentinerutto.test;

import android.database.Cursor;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    static String MQTTHOST ="tcp://broker.hivemq.com:1883";
    static String USERNAME =""; //your websocket client username
    static String PASSWORD = ""; //your websocket client password
    String  topicName,message;

    String clientID;
    MqttAndroidClient client;
    Button connect,subscribe,unsubscribe,publish;
    Vibrator vibrator;
    EditText recvmsg,editTextTopic,pubmsg;
    Ringtone ringtone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recvmsg=findViewById(R.id.recevMsg);
        pubmsg=findViewById(R.id.pubMsg);

        editTextTopic = (EditText) findViewById(R.id.topic);
        topicName = editTextTopic.getText().toString();

        connect();

        connect=findViewById(R.id.btnConn);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });

        subscribe=findViewById(R.id.btnSub);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              subscribe(topicName,recvmsg);            }
        });

        unsubscribe=findViewById(R.id.btnUnSub);
        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              unsubscribe(topicName);            }
        });

        publish=findViewById(R.id.btnPub);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              publish(topicName,message);            }
        });

    }
 //publish a message
public  void publish(String topicName,String  message)  {
 topicName = editTextTopic.getText().toString();
 message = pubmsg.getText().toString();

//    String message = "hello world from valentine at sendy ";
    try {
        client.publish(topicName, message.getBytes(),2,false);
    } catch ( MqttException e) {
        e.printStackTrace();
    }


}


//subscribe code
//public void Subscribe(View v){
public void subscribe(  String topicName, final EditText recv){
        topicName = editTextTopic.getText().toString();

    try {
        client.subscribe(topicName,1);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                Toast.makeText(MainActivity.this,"Connection Lost",Toast.LENGTH_LONG).show();

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                recv.setText(new String(mqttMessage.toString()));
                vibrator.vibrate(600);
                ringtone.play();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    } catch (MqttException e) {
        e.printStackTrace();
    }

}



//unsubscribe
public void unsubscribe(String topicName){
    topicName = editTextTopic.getText().toString();
     try {
        IMqttToken unsubToken = client.unsubscribe(topicName);
        unsubToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Toast.makeText(MainActivity.this,"Unsubscribed successfully from " ,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken,
                                  Throwable exception) {
                Toast.makeText(MainActivity.this,"Unsubscribed NOT successful",Toast.LENGTH_LONG).show();

            }
        });
    } catch (MqttException e) {
        e.printStackTrace();
    }
}
//connect to service
    public void connect() {

        clientID = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(),MQTTHOST,clientID);

        //connect with mqtt 3.1
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try{
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"connection success",Toast.LENGTH_LONG).show();
                    //setSubcription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"connection Failed",Toast.LENGTH_LONG).show();

                }
            });
        }catch (MqttException e){
            e.printStackTrace();

        }
    }

    public void disconnect(View view) {
        topicName = editTextTopic.getText().toString();

        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                    Toast.makeText(MainActivity.this,"Disconnection success"  + topicName,Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



}
