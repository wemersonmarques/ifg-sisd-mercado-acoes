import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {
	 //URL of the JMS server. DEFAULT_BROKER_URL will just mean that JMS server is on localhost
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    
    private MessageProducer producerComprar;
    private MessageProducer producerVender;
    
    private Session session;
    
    // default broker URL is : tcp://localhost:61616"
    private static String COMPRAR = "ACOES_COMPRAR";  
    private static String VENDER = "ACOES_VENDER"; 
     
    public Sender () {
    	try {
			iniciarConexao();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void iniciarConexao() throws JMSException {
    	// Getting JMS connection from the server and starting it
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
         
        //Creating a non transactional session to send/receive JMS message.
        session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);  
         
        //Destination represents here our queue 'JCG_QUEUE' on the JMS server. 
        //The queue will be created automatically on the server.
        Destination destComprar = session.createQueue(COMPRAR);
        Destination destVender = session.createQueue(VENDER);
         
        // MessageProducer is used for sending messages to the queue.
        producerComprar = session.createProducer(destComprar);
        producerVender = session.createProducer(destVender);
    }
    
    public void enviarMensagem(EAcoes acao, Map map) throws JMSException {
    	String empresa = map.get("Empresa").toString();
		float valor = Float.valueOf(map.get("Valor").toString());
		MapMessage mensagem = session.createMapMessage();
    	mensagem.setString("Empresa", empresa);
    	mensagem.setFloat("Valor", valor);
		
    	if (acao.equals(EAcoes.VENDER)) {
    		producerComprar.send(mensagem);
    	} else if (acao.equals(EAcoes.COMPRAR)) {
    		producerVender.send(mensagem);
    	}
    }
    
    
}
