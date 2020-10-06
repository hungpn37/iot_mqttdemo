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

public class MqttCollectService implements MqttCallback{
	
	private static String TAG = "[MQTT_COLLECT]: ";
	String collect_sub_topic = "ktmt_collect";
	String collect_pub_topic = "ktmt_collect_%d";
    int qos = 2; //AT_LEAST_ONCE
    String broker = "tcp://broker.hivemq.com:1883";
    String clientId = "SERVER_COLLECT_SERVICE_ID";
    
	private MqttClient mqttCollect = null;
	public MqttCollectService() {
		
        MemoryPersistence persistence = new MemoryPersistence();
        try {
        	mqttCollect = new MqttClient(broker, clientId, persistence);
        	MqttConnectOptions connOpts = new MqttConnectOptions();
        	connOpts.setCleanSession(true);
        	System.out.println(TAG + "Connecting to broker: " + broker);
        	mqttCollect.setCallback(this);
        	mqttCollect.connect(connOpts);
        	if(mqttCollect.isConnected())
        		System.out.println(TAG + "Connected. Subscribing the collect data from devices ...");
        	mqttCollect.subscribe(collect_sub_topic, qos);
        	
        }
        catch(MqttException me) {
        	System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();      
        }
	}
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println(TAG + "Connection lost");
		while(!mqttCollect.isConnected())
		{	
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				System.out.println(TAG + "Trying to connect again");
				mqttCollect.connect();
				mqttCollect.setCallback(this);
				mqttCollect.subscribe(collect_sub_topic, qos);
			}
			catch(MqttException me) {
				System.out.println("reason "+me.getReasonCode());
	            System.out.println("msg "+me.getMessage());
	            System.out.println("loc "+me.getLocalizedMessage());
	            System.out.println("cause "+me.getCause());
	            System.out.println("excep "+me);
	            me.printStackTrace();
			}
		}
		System.out.println(TAG + "Connected again");
	}
	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception{
		String msg = new String(message.getPayload());
		System.out.println(TAG + "Received message: " + msg);
    	if(!topic.equals(collect_sub_topic)){
    		System.out.println(TAG + "Message Arrived not subcribed collect topic");
    		return;
    	}
    	else
    	{
    		int devid=0, packet_no;
    		double tempVal, humidVal, tdsVal, phVal;
    		System.out.println(TAG + "Received a collect data packet");
    		//Parse json data packet
    		try {
        		JSONObject jobj = new JSONObject(msg);
    			devid = jobj.getInt("id"); 
    			System.out.println("DEVICE ID: " + devid);
    			packet_no = jobj.getInt("packet_no");
    	    	System.out.println("PACKET_NO: " + packet_no);
    	    	tempVal = jobj.getDouble("temperature");
    	    	System.out.println("TEMPERATURE: " + tempVal);
    	    	humidVal = jobj.getDouble("humidity");
    	    	System.out.println("HUMIDITY: " + humidVal);
    	    	tdsVal = jobj.getDouble("tds");
    	    	System.out.println("TDS: " + tdsVal);
    	    	phVal = jobj.getDouble("pH");
    	    	System.out.println("PH: " + phVal);
    	    	
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			System.out.println(TAG + "Collect data packet is wrong format");
    			e.printStackTrace();
    		}
    		//TODO1: Dua du lieu vao database
    		
    		//TODO2: Phan hoi da nhan duoc collect data
    		try {
    			String collect_pub_topic_devid = String.format(collect_pub_topic, devid);
    			String collect_pub_msg = "COLLECTED_ACK";
    			mqttCollect.publish(collect_pub_topic_devid, collect_pub_msg.getBytes(), 0, false);
    			System.out.println(TAG + "Published collect data ACK to the device_id " + devid);	
    		}
    		catch(MqttException me){
    			System.out.println(TAG + "Gui phan hoi collect data that bai");
				me.printStackTrace();
    		}
    	}
		
	}

}
