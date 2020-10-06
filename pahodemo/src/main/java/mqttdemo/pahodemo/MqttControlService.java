package mqttdemo.pahodemo;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

public class MqttControlService implements MqttCallback{
	private static String TAG = "[MQTT_CONTROL]: ";
	String control_sub_topic = "nct_control_%d_ack";
	String collect_pub_topic = "nct_control_%d";
    int qos = 2; //AT_LEAST_ONCE
    String broker = "tcp://iot.eclipse.org:1883";
    String clientId = "SERVER_CONTROL_SERVICE_ID";
    
	private MqttClient mqttControl = null;
	public MqttControlService() {
		
       
	}
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println(TAG + "Connection lost");
		
	}
	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception{
		
	}

}
