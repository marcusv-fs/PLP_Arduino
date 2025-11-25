# T3 - Gabriel: DSL Arduino em Português

Uma Domain-Specific Language (DSL) projetada para simplificar a programação de microcontroladores Arduino, utilizando comandos em português para tornar a programação mais acessível e intuitiva.

## 📋 Sobre o Projeto

Este projeto implementa um compilador/transpilador que converte código escrito em uma DSL em português para código C++ compatível com Arduino. A linguagem foi desenvolvida com foco em simplicidade, permitindo que pessoas sem experiência prévia em programação possam interagir com hardware Arduino de forma natural.

## ✨ Características Principais

- **Sintaxe em Português**: Comandos intuitivos como `acender led`, `apagar pino`, `esperar segundos`
- **Suporte a Entradas e Saídas**: Digital e analógica (PWM)
- **Estruturas Condicionais**: Comandos `se...então` com suporte a blocos aninhados
- **Duas Sintaxes para Blocos**: Suporte tanto para chaves `{}` quanto para dois pontos `:` estilo Python
- **Geração Automática de Código**: Gera automaticamente as funções `setup()` e `loop()` do Arduino
- **Indentação Automática**: Preserva formatação e indentação correta no código gerado
- **Comentários**: Suporte a comentários de linha com `//`

## 🏗️ Arquitetura

O projeto utiliza:
- **JavaCC**: Gerador de parser para processar a gramática
- **Tradução Direta**: A geração de código ocorre durante o parsing, sem AST intermediária
- **Sistema de Indentação**: Rastreamento automático de níveis de indentação para blocos aninhados

## 📁 Estrutura de Arquivos

```
T3/
├── src/
│   └── main/
│       └── java/
│           ├── ArduinoDSLParser.jj    # Gramática JavaCC
│           ├── exemplo.dsl            # Exemplo com sintaxe de chaves
│           ├── exemplo_colon.dsl     # Exemplo com sintaxe de dois pontos
│           └── utils/
│               └── ArduinoSketchEmitter.java  # Gerador de código .ino
└── utils/
    └── ArduinoSketchEmitter.java      # Cópia alternativa
```

## 🔧 Pré-requisitos

1. **Java Development Kit (JDK)**: Versão 8 ou superior
2. **JavaCC**: Versão 7.0.13 (incluída no projeto)
3. **Sistema Operacional**: Windows, Linux ou macOS

### Configuração do JavaCC

1. Adicione o caminho da pasta `scripts` de `javacc-javacc-7.0.13` ao PATH do sistema
2. Teste a instalação executando `javacc` no terminal
3. Se tudo estiver correto, a versão do JavaCC será exibida

## 🚀 Como Usar

### 1. Gerar o Parser

Primeiro, gere o parser Java a partir da gramática:

```bash
cd src/main/java
javacc ArduinoDSLParser.jj
```

### 2. Compilar o Projeto

Compile os arquivos Java gerados e a classe utilitária:

```bash
javac -d bin utils/*.java *.java
```

### 3. Executar o Compilador

Execute o parser com um arquivo `.dsl`:

```bash
java -cp bin ArduinoDSLParser exemplo.dsl generated_sketch.ino
```

**Parâmetros:**
- Primeiro argumento (opcional): arquivo de entrada `.dsl`. Se omitido, lê de `stdin`
- Segundo argumento (opcional): arquivo de saída `.ino`. Se omitido, gera `generated_sketch.ino`

## 📖 Sintaxe da Linguagem

### Comandos Básicos

#### Acender LED/Pino
```
acender led <número>
acender pino <número>
```
**Exemplo:** `acender led 13`

#### Apagar LED/Pino
```
apagar led <número>
apagar pino <número>
```
**Exemplo:** `apagar pino 13`

#### Esperar (Delay)
```
esperar <número> segundos
esperar <número> milissegundos
esperar <número> s
esperar <número> ms
```
**Exemplos:**
- `esperar 2 segundos`
- `esperar 500 milissegundos`

#### Escrever com Valor
```
escrever <periférico> <número> com valor <número>
escrever <periférico> <número> com valor <entrada>
```

**Periféricos de Saída:**
- `pino`, `led`, `buzzer`

**Periféricos de Entrada:**
- `botao`, `chave`, `A` (pino analógico), `potenciometro`, `sensor_de_temperatura`, `LDR`

**Exemplos:**
- `escrever pino 6 com valor 128` (PWM)
- `escrever led 13 com valor botao 4` (lê botão e escreve no LED)
- `escrever buzzer 9 com valor potenciometro 0` (lê potenciômetro e controla buzzer)

### Estruturas Condicionais

#### Sintaxe com Chaves
```
se botao <número> pressionado entao {
  <comandos>
}
```

#### Sintaxe com Dois Pontos (Estilo Python)
```
se botao <número> pressionado entao:
  <comandos>
fim
```
ou
```
se botao <número> pressionado entao:
  <comandos>
fimse
```

**Exemplo:**
```
se botao 4 pressionado entao {
  acender led 13
  esperar 1 segundos
  apagar led 13
}
```

### Comentários

```
// Este é um comentário de linha
acender led 13  // Comentário no final da linha
```

### Ponto e Vírgula

O ponto e vírgula (`;`) é **opcional** em todos os comandos:

```
acender led 13    // Válido
acender led 13;   // Também válido
```

## 📝 Exemplos

### Exemplo 1: Piscar LED

**Código DSL (`exemplo.dsl`):**
```
acender led 13
esperar 2 segundos
apagar led 13
```

**Código Arduino Gerado:**
```cpp
// Código gerado pela DSL Arduino (português)
#include <Arduino.h>

void setup() {
  pinMode(13, OUTPUT);
}

void loop() {
  digitalWrite(13, HIGH);
  delay(2000);
  digitalWrite(13, LOW);
  // repetir com pequeno atraso
  delay(100);
}
```

### Exemplo 2: Botão com Sintaxe de Chaves

**Código DSL:**
```
se botao 4 pressionado entao {
  acender led 13
  esperar 1 segundos
  apagar led 13
}
```

### Exemplo 3: Botão com Sintaxe de Dois Pontos

**Código DSL (`exemplo_colon.dsl`):**
```
acender led 13
esperar 2 segundos
apagar led 13
se botao 4 pressionado entao:
  acender led 13
  esperar 1 segundos
fim
```

### Exemplo 4: PWM com Potenciômetro

**Código DSL:**
```
escrever pino 6 com valor potenciometro 0
esperar 2 milissegundos
```

**Código Arduino Gerado:**
```cpp
void setup() {
  pinMode(6, OUTPUT);        // Saída PWM
  pinMode(A0, INPUT);        // Entrada analógica
}

void loop() {
  analogWrite(6, map(analogRead(A0), 0, 1023, 0, 255));
  delay(2);
  delay(100);
}
```

## 🎯 Tipos de Pinos Suportados

### Saídas Digitais
- **Pinos:** 0-13
- **Comandos:** `acender`, `apagar`, `escrever ... com valor ...`
- **Configuração:** `pinMode(pin, OUTPUT)`

### Saídas Analógicas (PWM)
- **Pinos:** 3, 5, 6, 9, 10, 11
- **Comandos:** `escrever pino N com valor X` (onde X é 0-255)
- **Configuração:** `pinMode(pin, OUTPUT)`
- **Geração:** `analogWrite(pin, value)`

### Entradas Digitais
- **Pinos:** 0-13
- **Periféricos:** `botao`, `chave`
- **Configuração:** `pinMode(pin, INPUT_PULLUP)`
- **Uso:** Em comandos `se botao N pressionado entao`

### Entradas Analógicas
- **Pinos:** A0-A5 (0-5)
- **Periféricos:** `A`, `potenciometro`, `sensor_de_temperatura`, `LDR`
- **Configuração:** `pinMode(A0, INPUT)`
- **Uso:** Em comandos `escrever ... com valor potenciometro N`

## 🔍 Detalhes Técnicos

### Sistema de Indentação

O parser mantém um sistema automático de indentação:
- **Nível base:** 2 espaços (comandos no `loop()`)
- **Níveis aninhados:** +2 espaços por nível (ex: dentro de `if`)
- **Preservação:** A indentação é preservada automaticamente no código gerado

### Geração de Código

O compilador gera automaticamente:
1. **`setup()`**: Configura todos os pinos detectados
   - `OUTPUT` para LEDs/pinos de saída
   - `INPUT_PULLUP` para botões
   - `INPUT` para entradas analógicas
2. **`loop()`**: Contém todos os comandos da DSL
   - Adiciona automaticamente `delay(100)` no final

### Mapeamento DSL → Arduino

| DSL | Arduino |
|-----|---------|
| `acender pino N` | `digitalWrite(N, HIGH);` |
| `apagar pino N` | `digitalWrite(N, LOW);` |
| `esperar N segundos` | `delay(N * 1000);` |
| `esperar N milissegundos` | `delay(N);` |
| `se botao N pressionado entao { ... }` | `if (digitalRead(N) == HIGH) { ... }` |
| `escrever pino N com valor X` | `analogWrite(N, X);` (se PWM) ou `digitalWrite(N, X > 127 ? HIGH : LOW);` |

## ⚠️ Limitações Conhecidas

1. **Validação de Pinos:** Não valida se o número do pino é válido para o Arduino
2. **Aninhamento Profundo:** Múltiplos níveis de `if` aninhados podem ter problemas de indentação
3. **Tratamento de Erros:** Erros de sintaxe interrompem o parsing sem mensagens detalhadas
4. **Delay Duplicado:** Sempre adiciona `delay(100)` no final, mesmo se já houver comandos `esperar`

## 🛠️ Desenvolvimento

### Modificando a Gramática

Para adicionar novos comandos ou modificar a sintaxe:

1. Edite o arquivo `ArduinoDSLParser.jj`
2. Adicione novos tokens na seção `TOKEN`
3. Adicione novas produções na seção `Grammar productions`
4. Regenere o parser: `javacc ArduinoDSLParser.jj`
5. Recompile: `javac -d bin utils/*.java *.java`

### Estrutura da Gramática

A gramática segue a estrutura:
```
Program → Command* EOF
Command → AcenderCmd | ApagarCmd | EsperarCmd | IfCmd | EscreverCmd
```

## 📚 Referências

- [JavaCC Documentation](https://javacc.github.io/javacc/)
- [Arduino Reference](https://www.arduino.cc/reference/en/)
- [Domain-Specific Languages](https://en.wikipedia.org/wiki/Domain-specific_language)

## 👥 Autores

Projeto desenvolvido como parte da disciplina de Paradigmas de Linguagens de Programação (PLP).

## 📄 Licença

Este projeto é parte de um trabalho acadêmico.


