package com.example.valentinerutto.test;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
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
    static String USERNAME ="sendy";
    static String PASSWORD = "93a3a43dbac9ddd362702fb525b42a2d";

String  topicName="topic/lang";


    MqttAndroidClient client;
    Button connect,subscribe,unsubscribe,publish;
    Vibrator vibrator;
    EditText recvmsg,editTextTopic;
    Ringtone ringtone;
    Databasehelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Databasehelper(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(),uri);



        recvmsg=findViewById(R.id.recevMsg);
        editTextTopic = (EditText) findViewById(R.id.topic);

        Connect();

        connect=findViewById(R.id.btnConn);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connect();
            }
        });

        subscribe=findViewById(R.id.btnSub);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              Subscribe(topicName,recvmsg);            }
        });

        unsubscribe=findViewById(R.id.btnUnSub);
        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Unsubscribe(topicName);            }
        });

        publish=findViewById(R.id.btnPub);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              publish(topicName);            }
        });

    }
 //publish a message
public  void publish(String topicName)  {
  topicName = editTextTopic.getText().toString();

    String message = "hello world from valentine at sendy ";
    try {
        client.publish(topicName, message.getBytes(),2,false);
    } catch ( MqttException e) {
        e.printStackTrace();
    }


}


//subscribe code
//public void Subscribe(View v){
public void Subscribe(String topicName, final EditText recv){
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
//                db.insertData(topicName,mqttMessage.toString());
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
public void Unsubscribe(String topicName){
    topicName = editTextTopic.getText().toString();

    try {
        IMqttToken unsubToken = client.unsubscribe(topicName);
        unsubToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Toast.makeText(MainActivity.this,"Unsubscribed successfully",Toast.LENGTH_LONG).show();
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
    public void Connect() {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(),MQTTHOST,clientId);

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

    public void Disconnect(View view) {
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
