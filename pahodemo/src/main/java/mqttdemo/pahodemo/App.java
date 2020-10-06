package mqttdemo.pahodemo;

public class App
{
	public App() {
		
	}
	
    public static void main( String[] args )
    {
        System.out.println( "MQTT Services Demo!" );
        //new App().doDemo();
        new MqttAuthenticationService();
        new MqttCollectService();
       
    }
    public void doDemo() {
    			
    }
    
}
