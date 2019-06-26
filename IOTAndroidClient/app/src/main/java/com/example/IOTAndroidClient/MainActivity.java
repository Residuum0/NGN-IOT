package com.example.IOTAndroidClient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements MqttCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText txtRxData = (EditText)findViewById(R.id.TxtRxData);
        final EditText txtStatusInfo = (EditText)findViewById(R.id.txtStatus);


        IMqttToken token;

        MqttAndroidClient client = null;
        try {
           String clientId = MqttClient.generateClientId();
           client = new MqttAndroidClient(this.getApplicationContext(), "tcp://io.adafruit.com:1883", clientId);

           MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("YYY");
            options.setPassword("XXX".toCharArray());
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);

            token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    txtStatusInfo.setText("Connected");
                    subscribeClient(asyncActionToken);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    txtStatusInfo.setText("Not Connected");

                }
            });
        } catch(MqttException e) {
            e.printStackTrace();
        }
        System.out.println(client.isConnected());
    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("messageArrived1");
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("deliveryComplete");
    }
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("connectionLost");
    }

    public void subscribeClient(IMqttToken token){
        if(token.getClient().isConnected()){
            final EditText txtStatusInfo = (EditText)findViewById(R.id.txtStatus);
            txtStatusInfo.setText("Subscribed");
            try {
                IMqttMessageListener iMqttMessageListener = new IMqttMessageListener(){
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        try{

                            byte tmp[] = "".getBytes();

                           /* for(int i=0;i<message.getPayload().length;i++) {
                                System.out.printf("0x%02X", message.getPayload()[i] - 0x30);
                                tmp[i] = message.getPayload()[i];
                            }
                            System.out.println(new String(message.getPayload()));
                            System.out.flush();*/

                            String value = new String(message.getPayload(), "UTF-8");
                            System. out.println(value);
                            EditText txtRxData = findViewById(R.id.TxtRxData);
                            txtRxData.setText(value);

                            //txtStatusInfo.setText("Arrived");

                        }catch(Exception exp){
                            System.out.println("ExeptionIII");
                            System.out.flush();
                        }
                    }
                };
                String topic = "YYY/feeds/analog";
                int qos = 0;
                IMqttToken subToken = token.getClient().subscribe(topic, qos, iMqttMessageListener);
                txtStatusInfo.setText("Subscribed");
            } catch (MqttException e) {
                e.printStackTrace();
                System.out.println("ExeptionII");
                System.out.flush();
            }
        }
    }
}
