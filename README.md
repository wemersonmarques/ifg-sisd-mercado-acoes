# Mercdo de Ações

Projeto de um mercado de ações utilizando Broker ActiveMQ para a disciplina de Sistemas Distribuídos - IFG. 

**Responsabilidades:**

StockServer: responsável por publicar ações, de valores aleatórios entre R$ 0,00 e R$ 100,00, de empresas, em intervalos aleatórios de 0 a 10 segundos, no tópico StockMarket;
StockSubscriber: responsável por ler as ações publicadas no tópico StockMarket e:
            * Se o valor da ação ultrapassar R$ 50,00, solicitar ao Sender enviar a mensagem para a fila de VENDA das ações;
            * Se o valor da ação não ultrapassar R$ 50,00, solicitar ao Sender enviar a mensagem para a fila de COMPRA das ações;
BuyAgent: agente responsável por consumir as mensagens da fila de compra;
SellAgent: agente responsável por consumir as mensagens da fila de venda;
