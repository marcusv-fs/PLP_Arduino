# O Projeto
Este projeto consiste na implementação de uma Linguagem Específica de Domínio (DSL) chamada ADSL (Arduino Domain-Specific Language), cujo objetivo é abstrair e simplificar a programação de microcontroladores Arduino. A arquitetura do projeto é baseada na separação clara entre a definição da linguagem e a tradução do código, utilizando as ferramentas JavaCC e JJTree e o Padrão Visitor.

# Estrutura do Projeto
A arquitetura do projeto é baseado na separação de responsabilidades.
O componente principal, o arquivo ADSL.jj (gerado a partir do ADSL.jjt pelo JJTree), é o responsável por definir a Gramática da Linguagem Específica de Domínio (DSL) e, consequentemente, pela construção da Árvore de Sintaxe Abstrata (AST), que representa a estrutura lógica do código-fonte da DSL.
Por outro lado, o arquivo ArduinoVisitor.java implementa o Padrão Visitor. Sua função é percorrer a AST e realizar a tradução do código ADSL para o código C/C++ compatível com a plataforma Arduino.Essa separação é útil, pois permite que a lógica de tradução seja modificada independentemente da definição da linguagem.

# Principais Arquivos
## ADSL.jjt
O arquivo .jjt é o artefato que define a gramática da Linguagem Específica de Domínio (DSL) e, simultaneamente, orienta o JJTree na construção da Árvore de Sintaxe Abstrata (AST). Essencialmente, o .jjt é o modelo que transforma o código-fonte textual em uma estrutura de dados hierárquica e tipada, onde cada nó representa uma construção sintática da linguagem, como comandos, expressões ou declarações.

## ArduinoVisitor.java
 O Visitor é um padrão de projeto que permite a definição de novas operações sobre os elementos da AST sem a necessidade de modificar as classes dos nós. A classe ArduinoVisitor.java é o componente responsável por percorrer a AST gerada e aplicar a lógica de tradução. Para cada nó visitado, ele gera o código C/C++ equivalente.

# Como rodar?
Para modificar a sintáxe, modifique o arquivo "ADSL.jjt" ao invés do "ADSL.jj". Após isso, execute setup.bat ou 
1. Abra a pasta onde se encontra o arquivo .jj que deseja executar.
2. Execute : jjtree .\ADSL.jjt
2. Execute : javacc ADSL.jj
3. Execute : javac *.java
4. Execute : java ADSL .\00_teste.txt