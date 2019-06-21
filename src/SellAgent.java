
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class SellAgent {
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private static Session session;
	private static String VENDER = "ACOES_VENDER";
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
			Destination destVender = session.createQueue(VENDER);
			consumidor = session.createConsumer(destVender);

		} catch (Exception e) {
			System.out.println("Ocorreu a falha " + e.getMessage());
		}

	}

	public static void consumir() {

		try {
			while (true) {
				Message mensagem = consumidor.receive();
				System.out.println("Mensagem recebida pelo SellAgent: " + mensagem);
			}
		} catch (JMSException e) {
			System.out.println("Ocorreu um erro para receber as mensagems " + e.getMessage());
		}
	}
}
