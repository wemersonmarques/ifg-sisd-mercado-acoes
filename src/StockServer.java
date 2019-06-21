import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;

public class StockServer {

	private static TopicConnection topicConn;
	private static TopicPublisher topicPub;
	private static TopicSession topicSess;
	private static List<String> empresas;
	
	
	public static void main(String[] args) {
		empresas = new ArrayList<>();
		empresas.add("Vale");
		empresas.add("Taurus");
		empresas.add("Danone");
		empresas.add("Samsung");
		empresas.add("Ricardo Eletro");
		empresas.add("Magazine Luiza");
		empresas.add("Edifier");
		empresas.add("LG");
		empresas.add("NET");
		empresas.add("Vivo");
		
		iniciarConexao();
		enviarMensagens();
		
	}

	public static void iniciarConexao() {
		try {
			Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			env.put(Context.PROVIDER_URL, "tcp://localhost:61616");
			env.put("topic.market", "StockMarket");
			// cria o contexto inicial
			InitialContext ctx = new InitialContext(env);

			// lookup da f�brica de conex�o (t�pico)
			TopicConnectionFactory topicFactory = (TopicConnectionFactory) ctx.lookup("TopicConnectionFactory");
			// cria uma conex�o (t�pico)
			topicConn = topicFactory.createTopicConnection();
			// cria uma sess�o (t�pico)
			topicSess = topicConn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

			// lookup do objeto referente ao t�pico
			Topic topic = (Topic) ctx.lookup("market");
			// cria um publisher (t�pico)
			topicPub = topicSess.createPublisher(topic);
		} catch (Exception e) {
			System.out.println("Ocorreu a falha" + e.getMessage());
		}
	}

	public static void enviarMensagens() {
		try {
			while (true) {
				// cria a mensagem
				MapMessage message = topicSess.createMapMessage();

				message.setString("Empresa", empresas.get((int) (Math.random() * 10)));
				message.setFloat("Valor", (float) Math.random() * 101);

				// send the message
				topicPub.publish(message);
				System.out.println("Ações enviadas com sucesso para o mercado");

				long espera = (long) (Math.random() * 10000);
				System.out
						.println("Vou esperar " + espera / 1000 + " segundos para enviar novas ações para o mercado.");
				Thread.sleep(espera);
			}
		} catch (Exception e) {
			System.out.println("Ocorreu a falha" + e.getMessage());
		} finally {
			try {
				// fechando conexao
				topicConn.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

}
