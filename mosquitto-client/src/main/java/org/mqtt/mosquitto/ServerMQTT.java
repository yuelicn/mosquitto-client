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
public class ServerMQTT {

	// ssl://MQTT安装的服务器地址:MQTT定义的端口号
	public static final String HOST = "ssl://172.16.192.102:1883";
	// tcp://MQTT安装的服务器地址:MQTT定义的端口号
	//public static final String HOST = "tcp://172.16.192.102:1883";
	// 定义一个主题
	public static final String TOPIC = "root/topic/123";
	// 定义MQTT的ID，可以在MQTT服务配置中指定
	private static final String clientid = "server11";

	private MqttClient client;
	private MqttTopic topic11;
	private String userName = "mosquitto";
	private String passWord = "mosquitto";
	public static String caFilePath = "D:/keystore/Mosquitto-ca/ssl/ca.crt";
	public static String clientCrtFilePath = "D:/keystore/Mosquitto-ca/ssl/client.crt";
	public static String clientKeyFilePath = "D:/keystore/Mosquitto-ca/ssl/client.key";

	private MqttMessage message;

	/**
	 * 构造函数
	 * 
	 * @throws Exception
	 */
	public ServerMQTT() throws Exception {
		// MemoryPersistence设置clientid的保存形式，默认为以内存保存
		client = new MqttClient(HOST, clientid, new MemoryPersistence());
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
		ServerMQTT server = new ServerMQTT();

		server.message = new MqttMessage();
		server.message.setQos(1);
		server.message.setRetained(true);
		server.message.setPayload("hello,topic14".getBytes());
		server.publish(server.topic11, server.message);
		System.out.println(server.message.isRetained() + "------ratained状态");
	}
}
