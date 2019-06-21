
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class BuyAgent {
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private static Session session;
	private static String COMPRAR = "ACOES_COMPRAR";
	private static MessageConsumer consumidor;

	public static void main(String[] args) {
		iniciarConexao();
		consumir();
	}

	public static void iniciarConexao() {
		try {
			// Getting JMS connection from the server and starting it
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			Connection connection = connectionFactory.createConnection();
			connection.start();

			// Creating a non transactional session to send/receive JMS message.
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Destination represents here our queue 'JCG_QUEUE' on the JMS server.
			// The queue will be created automatically on the server.
			Destination destComprar = session.createQueue(COMPRAR);
			consumidor = session.createConsumer(destComprar);

		} catch (Exception e) {
			System.out.println("Ocorreu a falha " + e.getMessage());
		}

	}

	public static void consumir() {

		try {
			while (true) {
				System.out.println("Mensagem recebida pelo BuyAgent: " + consumidor.receive());
			}
		} catch (JMSException e) {
			System.out.println("Ocorreu um erro para receber as mensagems " + e.getMessage());
		}
	}
}
