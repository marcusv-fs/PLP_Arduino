# O Projeto
Este projeto consiste na implementação de uma Linguagem Específica de Domínio (DSL) chamada ADSL (Arduino Domain-Specific Language), cujo objetivo é abstrair e simplificar a programação de microcontroladores Arduino. A arquitetura do projeto é baseada na separação clara entre a definição da linguagem e a tradução do código, utilizando as ferramentas JavaCC e JJTree e o Padrão Visitor.

# Principais Arquivos
## ADSL.jjt
O arquivo .jjt é o artefato que define a gramática da Linguagem Específica de Domínio (DSL) e, simultaneamente, orienta o JJTree na construção da Árvore de Sintaxe Abstrata (AST). Essencialmente, o .jjt é o modelo que transforma o código-fonte textual em uma estrutura de dados hierárquica e tipada, onde cada nó representa uma construção sintática da linguagem, como comandos, expressões ou declarações.

## ArduinoVisitor.java
 O Visitor é um padrão de projeto que permite a definição de novas operações sobre os elementos da AST sem a necessidade de modificar as classes dos nós. A classe ArduinoVisitor.java é o componente responsável por percorrer a AST gerada e aplicar a lógica de tradução. Para cada nó visitado, ele gera o código C/C++ equivalente. Essa separação é útil, pois permite que a lógica de tradução seja modificada independentemente da definição da linguagem.

# Como executar?
Entre na pasta src e execute setup.bat ou 
1. Abra a pasta onde se encontra o arquivo .jjt que deseja executar.
2. Execute : jjtree .\ADSL.jjt
2. Execute : javacc ADSL.jj
3. Execute : javac *.java
4. Execute : java ADSL .\00_teste.txt

# Equivalência de Comandos da Linguagem ADSL (Arduino Domain-Specific Language)

Esta sessão apresenta a equivalência entre os comandos da linguagem ADSL, definida no arquivo JJT, e suas funções correspondentes na linguagem de programação Arduino (C/C++).

| Comando ADSL | Equivalente Arduino (C/C++) | Descrição |
| :--- | :--- | :--- |
| `Config { ... }` | `void setup() { ... }` | Bloco de configuração inicial, executado uma vez. |
| `Execute { ... }` | `void loop() { ... }` | Bloco de execução principal, repetido continuamente. |
| `funcao nome(...) { ... }` | `tipo_retorno nome(...) { ... }` | Declaração de uma função ou procedimento. |
| `retorne expressao;` | `return expressao;` | Retorna um valor de uma função. |
| `Entrada pino;` | `pinMode(pino, INPUT);` | Configura um pino digital ou analógico como entrada. |
| `Saida pino;` | `pinMode(pino, OUTPUT);` | Configura um pino digital como saída. |
| `Monitor frequencia;` | `Serial.begin(frequencia);` | Inicializa a comunicação serial com a frequência especificada. |
| `Espere valor unidade;` | `delay(valor_em_ms);` | Pausa a execução pelo tempo especificado. A unidade de tempo é convertida para milissegundos (ms). |
| `Leia pino;` | `digitalRead(pino);` ou `analogRead(pino);` | Lê o valor de um pino digital ou analógico. |
| `Escreva pino valor;` | `digitalWrite(pino, valor);` | Escreve um valor digital (HIGH/LOW, 1/0) em um pino digital. |
| `EscrevaPWM pino valor;` | `analogWrite(pino, valor);` | Escreve um valor analógico (PWM, 0-255) em um pino PWM. |
| `EscrevaMon(expressao);` | `Serial.print(expressao);` ou `Serial.println(expressao);` | Envia dados para o Monitor Serial. |
| `repita expressao { ... }` | `for (int i = 0; i < expressao; i++) { ... }` | Repete um bloco de código o número de vezes especificado pela expressão. |
| `se (condicao) { ... }` | `if (condicao) { ... }` | Estrutura condicional simples. |
| `se (condicao) { ... } senao { ... }` | `if (condicao) { ... } else { ... }` | Estrutura condicional completa. |
| `tipo_var identificador = valor;` | `tipo_var identificador = valor;` | Declaração e inicialização de variáveis. |

## Tipos de Variáveis

| Tipo ADSL | Equivalente Arduino (C/C++) | Descrição |
| :--- | :--- | :--- |
| `byte` | `byte` | Armazena um valor numérico de 8 bits sem sinal (0 a 255). |
| `int` | `int` | Armazena um valor numérico de 16 bits com sinal (-32.768 a 32.767). |
| `unsigned int` | `unsigned int` | Armazena um valor numérico de 16 bits sem sinal (0 a 65.535). |
| `long` | `long` | Armazena um valor numérico de 32 bits com sinal. |
| `unsigned long` | `unsigned long` | Armazena um valor numérico de 32 bits sem sinal. |
| `real` | `float` | Armazena números de ponto flutuante. |
| `char` | `char` | Armazena um caractere. |
| `String` | `String` | Armazena uma sequência de caracteres (objeto String). |
| `Boolean` | `bool` | Armazena um valor booleano (`true` ou `false`). |

## Unidades de Tempo

| Unidade ADSL | Equivalente Arduino (C/C++) | Fator de Conversão para `delay()` |
| :--- | :--- | :--- |
| `ms` | `delay()` | Milissegundos (fator 1). |
| `s` | `delay()` | Segundos (fator 1000). |
| `min` | `delay()` | Minutos (fator 60000). |
| `h` | `delay()` | Horas (fator 3600000). |
