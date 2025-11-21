# Estrutura
Neste programa contamos com o "ADSL.jj", que é responsável pela estrutura dos dados, pela árvore sintática do código DSL.
Além disso, temos o "ArduinoVisitor.java" que é responsável pela tradução do código para C++.

É importante ter os dois separados já que, com isso, separamos as responsabilidades e se torna possível modificar a tradução sem alterar o arquivo da DSL. Abrindo a possibilidade de, por exemplo, gerar um arquivo em Python, Java e outros.

# Como rodar?
Para modificar a sintáxe, modifique o arquivo "ADSL.jjt" ao invés do "ADSL.jj". Após isso, execute setup.bat ou 
1. Abra a pasta onde se encontra o arquivo .jj que deseja executar.
2. Digite: jjtree .\ADSL.jjt
2. Digite: javacc ADSL.jj
3. Digite: javac *.java
4. Digite: java ADSL .\02_teste.txt