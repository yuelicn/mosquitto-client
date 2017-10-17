package org.mqtt.mosquitto;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * 
 * Title:Server Description: 服务器向多个客户端推送主题，即不同客户端可向服务器订阅相同主题
 * 
 * @author yueli 2017年9月1日下午17:41:10
 */
public class ServerMqtt {

	/**
	 *  ssl://MQTT安装的服务器地址:MQTT定义的端口号
	 */
	public static final String HOST = "ssl://172.16.192.102:1883";
	// tcp://MQTT安装的服务器地址:MQTT定义的端口号
	//public static final String HOST = "tcp://172.16.192.102:1883";
	/**
	 *  定义一个主题
	 */
	public static final String TOPIC = "sensor/room1/123";
	/**
	 *  定义MQTT的ID，可以在MQTT服务配置中指定
	 */
	private static final String CLIENT_ID = "server11";

	private MqttClient client;
	private MqttTopic topic11;
	private String userName = "master";
	private String passWord = "master";
	public static String caFilePath = "/Users/yueli/document/vianet/IM/Mosquitto/ssl/ca.crt";
	public static String clientCrtFilePath = "/Users/yueli/document/vianet/IM/Mosquitto/ssl/client.crt";
	public static String clientKeyFilePath = "/Users/yueli/document/vianet/IM/Mosquitto/ssl/client.key";

	private MqttMessage message;

	/**
	 * 构造函数
	 * 
	 * @throws Exception
	 */
	public ServerMqtt() throws Exception {
		// MemoryPersistence设置clientid的保存形式，默认为以内存保存
		client = new MqttClient(HOST, CLIENT_ID, new MemoryPersistence());
		connect();
	}

	/**
	 * 用来连接服务器
	 * 
	 * @throws Exception
	 */
	private void connect() throws Exception {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setUserName(userName);
		options.setPassword(passWord.toCharArray());
//		String connPool [] = {"ssl://172.16.192.102:1883","ssl://172.16.192.103:1883"};
//		options.setServerURIs(connPool);
		// 设置超时时间
		options.setConnectionTimeout(10);
		// 设置会话心跳时间
		options.setKeepAliveInterval(20);
		SSLSocketFactory factory = SslUtil.getSocketFactory(caFilePath, clientCrtFilePath, clientKeyFilePath, "111111");
		options.setSocketFactory(factory);
		try {
			client.setCallback(new PushCallback());
			client.connect(options);

			topic11 = client.getTopic(TOPIC);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param topic
	 * @param message
	 * @throws MqttPersistenceException
	 * @throws MqttException
	 */
	public void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException, MqttException {
		MqttDeliveryToken token = topic.publish(message);
		token.waitForCompletion();
		System.out.println("message is published completely! " + token.isComplete());
	}

	/**
	 * 启动入口
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ServerMqtt server = new ServerMqtt();
		int length = 5;
		for (int i = 0; i < length; i++) {
			server.message = new MqttMessage();
			server.message.setQos(1);
			server.message.setRetained(true);
			server.message.setPayload((" mac - hello number - >" + i ).getBytes());
			server.publish(server.topic11, server.message);
			Thread.sleep(10000);
			System.out.println(server.message.isRetained() + "------ratained状态");
		}
	}
}
