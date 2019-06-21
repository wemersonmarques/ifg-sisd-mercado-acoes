
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;

public class StockSubscriber {

	private static TopicSubscriber topicSub;
	private static TopicConnection topicConn;
	private static Sender sender;

	public static void main(String[] args) {
		iniciarConexao();
		receberMensagens();
		
	}

	public static void iniciarConexao() {
		try {
			sender = new Sender();
			
			Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			env.put("clientID", "client");
			env.put(Context.PROVIDER_URL, "tcp://localhost:61616");
			env.put("topic.market", "StockMarket");
			
			// cria o contexto inicial
			InitialContext ctx = new InitialContext(env);
			// lookup da f�brica de conex�o (t�pico)
			TopicConnectionFactory topicFactory = (TopicConnectionFactory) ctx.lookup("TopicConnectionFactory");
			// cria uma conex�o (t�pico)
			topicConn = topicFactory.createTopicConnection();

			// cria uma sess�o (t�pico)
			TopicSession topicSess = topicConn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = topicSess.createTopic("StockMarket");
			topicSess.createConsumer(destination);
			// lookup do objeto referente ao t�pico
			Topic topic = (Topic) ctx.lookup("market");

			// inicia a conex�o
			topicConn.start();
			
			// cria um subscriber (t�pico)
			topicSub = topicSess.createDurableSubscriber(topic, "DurableSubscriber");
			

		} catch (Exception e) {
			System.out.println("Ocorreu a falha " + e.getMessage());
		}

	}

	public static void receberMensagens() {
		try {
			HashMap map = new HashMap();

			while (true) {
				// recebe uma mensagem
				MapMessage message = (MapMessage) topicSub.receive();
				Enumeration en = message.getMapNames();

				while (en.hasMoreElements()) {
					String property = (String) en.nextElement();
					Object mapObject = message.getObject(property);
					map.put(property, mapObject);
				}

				System.out.println("Empresa recebida: " + map);
				
				if (Double.valueOf(map.get("Valor").toString()) > 50) {
					System.out.println("Acao recebida será enviada para venda pois seu valor ultrapassa 50,00 bolsonaros");
					sender.enviarMensagem(EAcoes.VENDER, map);
				} else {
					System.out.println("Acao recebida será enviada para compra pois seu valor não ultrapassa 50,00 bolsonaros");
					sender.enviarMensagem(EAcoes.COMPRAR, map);
				}
			}
		} catch (Exception e) {
			System.out.println("Falha para receber mensagem " + e.getMessage());
		}
	}
	
	
}
