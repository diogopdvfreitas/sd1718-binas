# Projeto de Sistemas Distribuídos 2017/18 #

Grupo A37

|Nome              |Numero|Email                         |
|------------------|------|------------------------------|
|Diogo Freitas     |81586 |diogo.maria.freitas@gmail.com |
|Francisco Pereira |76196 |fchamicapereira@gmail.com     |
|João Rodrigues    |83483 |jominutaro@hotmail.com        |

-------------------------------------------------------------------------------

Para correr os testes do binas-ws-cli, terão de ser lançadas estações com propriedades pré-definidas.

Assim, deverão ser utilizados os seguintes comandos (usar cada comando num terminal diferente):

Estação 1: mvn exec:java -Dws.i=1 -Dprop.x=3 -Dprop.y=6 -Dprop.capacity=5 -Dprop.bonus=1
Estação 2: mvn exec:java -Dws.i=2 -Dprop.x=3 -Dprop.y=8 -Dprop.capacity=6 -Dprop.bonus=2
Estação 3: mvn exec:java -Dws.i=3 -Dprop.x=3 -Dprop.y=10 -Dprop.capacity=7 -Dprop.bonus=3
