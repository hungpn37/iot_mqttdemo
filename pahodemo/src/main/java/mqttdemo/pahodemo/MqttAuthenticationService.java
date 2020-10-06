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

public class MqttAuthenticationService implements MqttCallback {
	
	private static String TAG = "[MQTT_AUTHEN]: ";
	private MqttClient mqttAuthen;
	private String authen_sub_topic = "nct_authentication";
	private String authen_pub_topic = "nct_authentication_%d";
    private int qos = 2; //AT_LEAST_ONCE
    private String broker = "tcp://broker.hivemq.com:1883";
    private String clientId = "SERVER_AUTHEN_SERVICE_ID";
	
	public MqttAuthenticationService() {
		// TODO Auto-generated constructor stub
      
        MemoryPersistence persistence = new MemoryPersistence();
        try {
        	mqttAuthen = new MqttClient(broker, clientId, persistence);
        	MqttConnectOptions connOpts = new MqttConnectOptions();
        	connOpts.setCleanSession(true);
        	System.out.println(TAG + "Connecting to broker: " + broker);
        	mqttAuthen.setCallback(this);
        	mqttAuthen.connect(connOpts);
        	if(mqttAuthen.isConnected())
        		System.out.println(TAG + "Connected. Subscribing the authentication request from devices...");
        	mqttAuthen.subscribe(authen_sub_topic, qos);
        	
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
		while(!mqttAuthen.isConnected())
		{	
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				System.out.println(TAG + "Trying to connect again");
				mqttAuthen.connect();
				mqttAuthen.setCallback(this);
				mqttAuthen.subscribe(authen_sub_topic, 2);
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
    	if(!topic.equals(authen_sub_topic)){
    		System.out.println(TAG + "Message Arrived not subscribed authentication topic");
    		return;
    	}
    	else
    	{
    		int devid = -1;
    		String passwd = "";
    		System.out.println(TAG + "Received an authentication request");
    		//Message nhan duoc la authentication request
    		try {
        		JSONObject jobj = new JSONObject(msg);
    			devid = jobj.getInt("id"); 
    			System.out.println("DEVICE ID: " + devid);
    			passwd = jobj.getString("password");
    	    	System.out.println("PASSWORD: " + passwd);
    	    	if(passwd.equals("nct_laboratory")) {
    	    		
    	    	}
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			System.out.println(TAG + "Authentication request is not required format");
    			e.printStackTrace();
    		} 	
    		//TODO: Kiem tra thiet bi da duoc dang ky trong CSDL 
    		//Xac thuc thu cong cho mot so thiet bi
    		if(devid == 11 || devid == 2 && passwd.equals("nct_laboratory")) {
    			//AUTHENTICATION PASS => Gui lai ket qua xac thuc toi thiet bi
    			System.out.println(TAG + String.format("Thiet bi %d duoc xac thuc thanh cong", devid));
    			try {
    				String authen_pub_topic_id = String.format(authen_pub_topic, devid);
    				String authen_pub_msg = "PASS_KEY";
    				mqttAuthen.publish(authen_pub_topic_id, authen_pub_msg.getBytes(), qos, true);
    				System.out.println(TAG + "Published authentication result to the device_id " + devid);
    			}
    			catch(MqttException me) {
    				System.out.println(TAG + "Gui authentication result that bai");
    				me.printStackTrace();
    			}
    		}
    		else {
    			System.out.println(TAG + "Thiet bi chua duoc dang ky voi he thong");
    		}
    		
    	}
		
	}

}
