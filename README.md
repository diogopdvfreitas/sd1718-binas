# Projeto de Sistemas Distribu�dos 2017/18 #

Grupo A37

|Nome              |Numero|Email                         |
|------------------|------|------------------------------|
|Diogo Freitas     |81586 |diogo.maria.freitas@gmail.com |
|Francisco Pereira |76196 |fchamicapereira@gmail.com     |
|Jo�o Rodrigues    |83483 |jominutaro@hotmail.com        |

-------------------------------------------------------------------------------

Para correr os testes do binas-ws-cli, ter�o de ser lan�adas esta��es com propriedades pr�-definidas.

Assim, dever�o ser utilizados os seguintes comandos (usar cada comando num terminal diferente):

Esta��o 1: mvn exec:java -Dws.i=1 -Dprop.x=3 -Dprop.y=6 -Dprop.capacity=5 -Dprop.bonus=1
Esta��o 2: mvn exec:java -Dws.i=2 -Dprop.x=3 -Dprop.y=8 -Dprop.capacity=6 -Dprop.bonus=2
Esta��o 3: mvn exec:java -Dws.i=3 -Dprop.x=3 -Dprop.y=10 -Dprop.capacity=7 -Dprop.bonus=3
