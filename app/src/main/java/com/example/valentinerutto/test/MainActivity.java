package com.example.valentinerutto.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    static String MQTTHOST ="tcp://broker.hivemq.com:1883";
    static String topicStr ="test/us";
    static String USERNAME ="sendy";
    static String PASSWORD = "93a3a43dbac9ddd362702fb525b42a2d";
    MqttAndroidClient client;
    Button publish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        publish=(Button)findViewById(R.id.button) ;
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publish(view);
            }
        });

            //create and establish mqttAndroidClient
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
 //publish a message
public  void publish(View v)  {

    String topic = topicStr;
    String message = "hello world from valentine ";
    try {
        client.publish(topic, message.getBytes(),0,false);
    } catch ( MqttException e) {
        e.printStackTrace();
    }

}



}
