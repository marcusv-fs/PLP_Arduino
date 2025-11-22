public class ArduinoVisitor implements ADSLVisitor {
    private int indentLevel = 0;
    
    private String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  "); // 2 espaços por nível
        }
        return sb.toString();
    }
    
    @Override
    public Object visit(SimpleNode node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTStart node, Object data) {
        System.out.println("// Código gerado automaticamente");
        System.out.println();
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTConfig node, Object data) {
        System.out.println("void setup() {");
        indentLevel++;
        node.childrenAccept(this, data);
        indentLevel--;
        System.out.println("}");
        System.out.println();
        return data;
    }

    @Override
    public Object visit(ASTExecute node, Object data) {
        System.out.println("void loop() {");
        indentLevel++;
        node.childrenAccept(this, data);
        indentLevel--;
        System.out.println("}");
        return data;
    }

    @Override
    public Object visit(ASTPinMode node, Object data) {
        Token tipoToken = node.jjtGetFirstToken();
        String tipo = tipoToken.image;
        
        SimpleNode pinosNode = (SimpleNode) node.jjtGetChild(0);
        Token pinoToken = pinosNode.jjtGetFirstToken();
        String pino = pinoToken.image;
        
        String arduinoMode = "INPUT";
        if (tipo.equals("Saida")) {
            arduinoMode = "OUTPUT";
        }
        
        if (pinosNode instanceof ASTPinosA) {
            int analogPin = Integer.parseInt(pino.substring(1));
            pino = String.valueOf(analogPin + 14);
        }
        
        System.out.println(getIndent() + "pinMode(" + pino + ", " + arduinoMode + ");");
        return data;
    }

    @Override
    public Object visit(ASTFreqMonitor node, Object data) {
        SimpleNode freqNode = (SimpleNode) node.jjtGetChild(0);
        Token freqToken = freqNode.jjtGetFirstToken();
        String baudRate = freqToken.image;
        
        System.out.println(getIndent() + "Serial.begin(" + baudRate + ");");
        return data;
    }

    @Override
    public Object visit(ASTFrequencia node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTDelay node, Object data) {
        SimpleNode valorNode = (SimpleNode) node.jjtGetChild(0);
        Token valorToken = valorNode.jjtGetFirstToken();
        String valor = valorToken.image;
        
        SimpleNode unidadeNode = (SimpleNode) node.jjtGetChild(1);
        Token unidadeToken = unidadeNode.jjtGetFirstToken();
        String unidade = unidadeToken.image;
        
        long ms = converterParaMs(Long.parseLong(valor), unidade);
        System.out.println(getIndent() + "delay(" + ms + ");");
        return data;
    }

    private long converterParaMs(long valor, String unidade) {
        switch (unidade) {
            case "ns": return valor / 1000000;
            case "ms": return valor;
            case "s": return valor * 1000;
            case "min": return valor * 60 * 1000;
            case "h": return valor * 60 * 60 * 1000;
            default: return valor;
        }
    }

    @Override
    public Object visit(ASTRead node, Object data) {
        // O primeiro filho pode ser Expressao ou Pinos
        Node pinoNode = node.jjtGetChild(0);
        
        System.out.print(getIndent());
        
        // Verifica o tipo do nó do pino
        if (pinoNode instanceof ASTPinos) {
            // Notação antiga com Pinos() - precisa verificar o filho para saber o tipo
            SimpleNode tipoPinoNode = (SimpleNode) ((SimpleNode) pinoNode).jjtGetChild(0);
            Token pinoToken = tipoPinoNode.jjtGetFirstToken();
            String pino = pinoToken.image;

            if (tipoPinoNode instanceof ASTPinosA) {
                // Para pino analógico, usa analogRead
                System.out.println("analogRead(" + pino + ");");
            } else {
                // Para pino digital, usa digitalRead
                System.out.println("digitalRead(" + pino + ");");
            }
        } else {
            // Notação nova com Expressao() - assume digitalRead
            System.out.print("digitalRead(");
            ((SimpleNode) pinoNode).jjtAccept(this, data);
            System.out.println(");");
        }
        return data;
    }

    @Override
    public Object visit(ASTDigitalWrite node, Object data) {
        // O primeiro filho pode ser Expressao ou Pinos
        Node pinoNode = node.jjtGetChild(0);
        // O segundo filho é sempre a expressão do valor
        SimpleNode valorNode = (SimpleNode) node.jjtGetChild(1);
        
        System.out.print(getIndent() + "digitalWrite(");
        
        // Verifica o tipo do nó do pino
        if (pinoNode instanceof ASTPinos) {
            // Notação antiga com Pinos() - precisa verificar o filho para saber o tipo
            SimpleNode tipoPinoNode = (SimpleNode) ((SimpleNode) pinoNode).jjtGetChild(0);
            Token pinoToken = tipoPinoNode.jjtGetFirstToken();
            String pino = pinoToken.image;
            
            if (tipoPinoNode instanceof ASTPinosA) {
                int analogPin = Integer.parseInt(pino.substring(1));
                pino = String.valueOf(analogPin + 14);
            }
            
            System.out.print(pino);
        } else {
            // Notação nova com Expressao()
            ((SimpleNode) pinoNode).jjtAccept(this, data);
        }
        
        System.out.print(", ");
        valorNode.jjtAccept(this, data);
        System.out.println(");");
        
        return data;
    }


    @Override
    public Object visit(ASTAnalogWrite node, Object data) {
        // O primeiro filho pode ser Expressao ou PinosPWM
        Node pinoNode = node.jjtGetChild(0);
        // O segundo filho é sempre a expressão do valor
        SimpleNode valorNode = (SimpleNode) node.jjtGetChild(1);
        
        System.out.print(getIndent() + "analogWrite(");
        
        // Verifica o tipo do nó do pino
        if (pinoNode instanceof ASTPinosPWM) {
            // Notação antiga com PinosPWM()
            Token pinoToken = ((SimpleNode) pinoNode).jjtGetFirstToken();
            System.out.print(pinoToken.image);
        } else {
            // Notação nova com Expressao()
            ((SimpleNode) pinoNode).jjtAccept(this, data);
        }
        
        System.out.print(", ");
        valorNode.jjtAccept(this, data);
        System.out.println(");");
        
        return data;
    }

    @Override
    public Object visit(ASTValorDigital node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTValorAnalogico node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTValorTempo node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTUnidadeTempo node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTPinos node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTPinosD node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTPinosA node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTPinosPWM node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTComando node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSingleLineComment node, Object data) {
        Token t = node.jjtGetFirstToken();
        if (t != null) {
            String[] lines = t.image.split("\n", -1);
            for (String line : lines) {
                System.out.println(getIndent() + line);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTMultiLineComment node, Object data) {
        Token t = node.jjtGetFirstToken();
        if (t != null) {
            String[] lines = t.image.split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                if (i == 0) {
                    System.out.print(getIndent() + lines[i]);
                } else {
                    System.out.print("\n" + getIndent() + lines[i]);
                }
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTFormalComment node, Object data) {
        Token t = node.jjtGetFirstToken();
        if (t != null) {
            String[] lines = t.image.split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                if (i == 0) {
                    System.out.print(getIndent() + lines[i]);
                } else {
                    System.out.print("\n" + getIndent() + lines[i]);
                }
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTSerialPrint node, Object data) {
        SimpleNode expressaoNode = (SimpleNode) node.jjtGetChild(0);
        
        System.out.print(getIndent() + "Serial.println(");
        expressaoNode.jjtAccept(this, data);
        System.out.println(");");
        
        return data;
    }

    private String processarString(String str) {
        // Remove as aspas externas
        str = str.substring(1, str.length() - 1);
        
        // Processa caracteres de escape básicos
        str = str.replace("\\n", "\" + \"\\n\" + \"");
        str = str.replace("\\t", "\" + \"\\t\" + \"");
        str = str.replace("\\\"", "\" + \"\\\"\" + \"");
        
        // Se houve substituições, precisamos reconstruir a string
        if (str.contains("\" + \"")) {
            // Remove o último " + " se existir
            if (str.endsWith("\" + \"")) {
                str = str.substring(0, str.length() - 4);
            }
            return "String(" + str + ")";
        }
        
        return "\"" + str + "\"";
    }

    @Override
    public Object visit(ASTDecVar node, Object data) {
        SimpleNode tipoNode = (SimpleNode) node.jjtGetChild(0);
        Token tipoToken = tipoNode.jjtGetFirstToken();
        String tipo = tipoToken.image;
        
        Token idToken = node.jjtGetFirstToken().next; // O token após o tipo é o identificador
        String identifier = idToken.image;
        
        // Verifica se há inicializador
        String inicializacao = "";
        if (node.jjtGetNumChildren() > 1) {
            SimpleNode initNode = (SimpleNode) node.jjtGetChild(1);
            // Para expressões complexas, precisamos processar o nó de expressão
            if (initNode instanceof ASTInicializador) {
                Token initToken = initNode.jjtGetFirstToken();
                String valor = initToken.image;
                
                // Processa strings (remove aspas)
                if (initToken.kind == ADSLConstants.STRING) {
                    valor = processarString(valor);
                }
                inicializacao = " = " + valor;
            } else {
                // É uma expressão complexa
                System.out.print(getIndent() + mapType(tipo) + " " + identifier + " = ");
                initNode.jjtAccept(this, data);
                System.out.println(";");
                return data;
            }
        }
        
        // Mapeia o tipo do ADSL para o tipo do Arduino
        String arduinoType = mapType(tipo);
        
        System.out.println(getIndent() + arduinoType + " " + identifier + inicializacao + ";");
        return data;
    }

    @Override
    public Object visit(ASTTiposVar node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTInicializador node, Object data) {
        // Processa o conteúdo do inicializador
        return node.childrenAccept(this, data);
    }

    private String mapType(String tipo) {
        switch (tipo) {
            case "byte": return "byte";
            case "int": return "int";
            case "unsigned int": return "unsigned int";
            case "long": return "long";
            case "unsigned long": return "unsigned long";
            case "real": return "float";
            case "char": return "char";
            case "String": return "String";
            case "Boolean": return "boolean";
            case "void": return "void";
            default: return "int"; // padrão
        }
    }

    @Override
    public Object visit(ASTSeSenao node, Object data) {
        String currentIndent = getIndent();
        
        // Processa a condição
        System.out.print(currentIndent + "if (");
        SimpleNode condicaoNode = (SimpleNode) node.jjtGetChild(0);
        condicaoNode.jjtAccept(this, data);
        System.out.println(") {");
        
        // Aumenta indentação para o bloco se
        indentLevel++;
        SimpleNode blocoSeNode = (SimpleNode) node.jjtGetChild(1);
        blocoSeNode.jjtAccept(this, data);
        indentLevel--;
        
        System.out.println(currentIndent + "}");
        
        // Se houver terceiro filho, é o bloco senao
        if (node.jjtGetNumChildren() > 2) {
            SimpleNode blocoSenaoNode = (SimpleNode) node.jjtGetChild(2);
            System.out.println(currentIndent + "else {");
            // Aumenta indentação para o bloco senao
            indentLevel++;
            blocoSenaoNode.jjtAccept(this, data);
            indentLevel--;
            System.out.println(currentIndent + "}");
        }
        
        return data;
    }

    @Override
    public Object visit(ASTBlocoSe node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTBlocoSenao node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTCondicao node, Object data) {
        // A condição agora é uma expressão booleana
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTAtribuicao node, Object data) {
        Token idToken = node.jjtGetFirstToken();
        String identifier = idToken.image;
        
        SimpleNode expressaoNode = (SimpleNode) node.jjtGetChild(0);
        
        System.out.print(getIndent() + identifier + " = ");
        expressaoNode.jjtAccept(this, data);
        System.out.println(";");
        
        return data;
    }

    @Override
    public Object visit(ASTExpressao node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTExpressaoBooleana node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTExpressaoOu node, Object data) {
        // Processa expressão OR (||)
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i > 0) {
                System.out.print(" || ");
            }
            Node child = node.jjtGetChild(i);
            if (child instanceof SimpleNode) {
                ((SimpleNode) child).jjtAccept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTExpressaoE node, Object data) {
        // Processa expressão AND (&&)
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i > 0) {
                System.out.print(" && ");
            }
            Node child = node.jjtGetChild(i);
            if (child instanceof SimpleNode) {
                ((SimpleNode) child).jjtAccept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTExpressaoRelacional node, Object data) {
        // Processa comparações: <, >, <=, >=, ==, !=
        if (node.jjtGetNumChildren() == 1) {
            // Apenas uma expressão, sem operador
            Node child = node.jjtGetChild(0);
            if (child instanceof SimpleNode) {
                ((SimpleNode) child).jjtAccept(this, data);
            }
        } else if (node.jjtGetNumChildren() == 2) {
            // Expressão operador expressão
            Node left = node.jjtGetChild(0);
            Node right = node.jjtGetChild(1);
            
            if (left instanceof SimpleNode) {
                ((SimpleNode) left).jjtAccept(this, data);
            }
            
            // Obtém o operador
            Token operatorToken = null;
            for (Token t = node.jjtGetFirstToken(); t != null; t = t.next) {
                if (t.kind == ADSLConstants.LT || t.kind == ADSLConstants.GT ||
                    t.kind == ADSLConstants.LE || t.kind == ADSLConstants.GE ||
                    t.kind == ADSLConstants.EQ || t.kind == ADSLConstants.NE) {
                    operatorToken = t;
                    break;
                }
            }
            
            if (operatorToken != null) {
                System.out.print(" " + operatorToken.image + " ");
            }
            
            if (right instanceof SimpleNode) {
                ((SimpleNode) right).jjtAccept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTExpressaoAditiva node, Object data) {
        // Processa adição e subtração: +, -
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i > 0) {
                // Obtém o operador entre as expressões
                boolean isPlus = true;
                // Lógica simplificada para determinar o operador
                // Em uma implementação real, você precisaria de uma lógica mais sofisticada
                System.out.print(" + ");
            }
            Node child = node.jjtGetChild(i);
            if (child instanceof SimpleNode) {
                ((SimpleNode) child).jjtAccept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTExpressaoMultiplicativa node, Object data) {
        // Processa multiplicação, divisão e módulo: *, /, %
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i > 0) {
                // Lógica simplificada para operadores
                System.out.print(" * ");
            }
            Node child = node.jjtGetChild(i);
            if (child instanceof SimpleNode) {
                ((SimpleNode) child).jjtAccept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTExpressaoUnaria node, Object data) {
        // Processa operadores unários: +, -, !, ~
        Token firstToken = node.jjtGetFirstToken();
        if (firstToken != null && 
            (firstToken.kind == ADSLConstants.PLUS || 
             firstToken.kind == ADSLConstants.MINUS ||
             firstToken.kind == ADSLConstants.BANG ||
             firstToken.kind == ADSLConstants.TILDE)) {
            System.out.print(firstToken.image);
        }
        
        // Processa a expressão
        if (node.jjtGetNumChildren() > 0) {
            Node child = node.jjtGetChild(0);
            if (child instanceof SimpleNode) {
                ((SimpleNode) child).jjtAccept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTExpressaoPrimaria node, Object data) {
        // Processa elementos primários: números, strings, identificadores, leituras, parênteses
        if (node.jjtGetNumChildren() == 0) {
            // Pode ser um literal ou identificador direto
            Token firstToken = node.jjtGetFirstToken();
            if (firstToken != null) {
                if (firstToken.kind == ADSLConstants.STRING) {
                    System.out.print(processarString(firstToken.image));
                } else if (firstToken.kind == ADSLConstants.LPAREN) {
                    System.out.print("(");
                    // O conteúdo entre parênteses será processado pelos filhos
                } else if (firstToken.kind == ADSLConstants.RPAREN) {
                    System.out.print(")");
                } else {
                    System.out.print(firstToken.image);
                }
            }
        } else {
            // Processa os filhos
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                Node child = node.jjtGetChild(i);
                if (child instanceof SimpleNode) {
                    if (i > 0) {
                        // Adiciona espaço entre elementos se necessário
                        System.out.print(" ");
                    }
                    ((SimpleNode) child).jjtAccept(this, data);
                }
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTLeiaExpressao node, Object data) {
        SimpleNode pinosNode = (SimpleNode) node.jjtGetChild(0);
        Token pinoToken = pinosNode.jjtGetFirstToken();
        String pino = pinoToken.image;

        // Verifica diretamente pelo nome do pino se começa com "A"
        if (pino.startsWith("A")) {
            // Para pino analógico, usa analogRead
            System.out.print("analogRead(" + pino + ")");
        } else {
            // Para pino digital, usa digitalRead
            System.out.print("digitalRead(" + pino + ")");
        }
        return data;
    }

    @Override
    public Object visit(ASTValorNumerico node, Object data) {
        Token t = node.jjtGetFirstToken();
        if (t != null) {
            System.out.print(t.image);
        }
        return data;
    }

    @Override
    public Object visit(ASTRepita node, Object data) {
        String currentIndent = getIndent();
        
        // Processa a expressão (que pode ser número ou variável)
        System.out.print(currentIndent + "for (int i = 0; i < ");
        
        // O primeiro filho agora é a expressão
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode expressaoNode = (SimpleNode) node.jjtGetChild(0);
            expressaoNode.jjtAccept(this, data);
        }
        
        System.out.println("; i++) {");
        
        // Processa o bloco de comandos
        indentLevel++;
        if (node.jjtGetNumChildren() > 1) {
            SimpleNode blocoNode = (SimpleNode) node.jjtGetChild(1);
            blocoNode.jjtAccept(this, data);
        }
        indentLevel--;
        
        System.out.println(currentIndent + "}");
        
        return data;
    }
    
    @Override
    public Object visit(ASTBlocoRepita node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTFuncao node, Object data) {
        Token nomeToken = node.jjtGetFirstToken().next; // Token após "funcao"
        String nomeFuncao = nomeToken.image;
        
        // Processa tipo de retorno (se existir)
        String tipoRetorno = "void";
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode tipoRetornoNode = (SimpleNode) node.jjtGetChild(0);
            if (tipoRetornoNode instanceof ASTTipoRetorno) {
                Token tipoToken = tipoRetornoNode.jjtGetFirstToken();
                tipoRetorno = mapType(tipoToken.image);
            }
        }
        
        System.out.print(tipoRetorno + " " + nomeFuncao + "(");
        
        // Processa parâmetros
        int startIndex = (tipoRetorno.equals("void") && node.jjtGetNumChildren() > 0) ? 0 : 1;
        if (node.jjtGetNumChildren() > startIndex) {
            SimpleNode parametrosNode = (SimpleNode) node.jjtGetChild(startIndex);
            parametrosNode.jjtAccept(this, data);
        }
        
        System.out.println(") {");
        
        // Processa corpo da função
        indentLevel++;
        for (int i = startIndex + 1; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof SimpleNode) {
                ((SimpleNode) child).jjtAccept(this, data);
            }
        }
        indentLevel--;
        
        System.out.println("}");
        System.out.println();
        return data;
    }

    @Override
    public Object visit(ASTParametros node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            Node child = node.jjtGetChild(i);
            if (child instanceof SimpleNode) {
                ((SimpleNode) child).jjtAccept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTParametro node, Object data) {
        SimpleNode tipoNode = (SimpleNode) node.jjtGetChild(0);
        Token tipoToken = tipoNode.jjtGetFirstToken();
        String tipo = mapType(tipoToken.image);
        
        Token idToken = node.jjtGetFirstToken().next; // Token após o tipo
        String identificador = idToken.image;
        
        System.out.print(tipo + " " + identificador);
        return data;
    }

    @Override
    public Object visit(ASTTipoRetorno node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTRetorne node, Object data) {
        System.out.print(getIndent() + "return");
        
        if (node.jjtGetNumChildren() > 0) {
            System.out.print(" ");
            SimpleNode expressaoNode = (SimpleNode) node.jjtGetChild(0);
            expressaoNode.jjtAccept(this, data);
        }
        
        System.out.println(";");
        return data;
    }

    @Override
    public Object visit(ASTChamadaFuncao node, Object data) {
        Token nomeToken = node.jjtGetFirstToken();
        String nomeFuncao = nomeToken.image;
        
        System.out.print(getIndent() + nomeFuncao + "(");
        
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode argumentosNode = (SimpleNode) node.jjtGetChild(0);
            argumentosNode.jjtAccept(this, data);
        }
        
        System.out.println(");");
        return data;
    }

    @Override
    public Object visit(ASTChamadaFuncaoExpressao node, Object data) {
        Token nomeToken = node.jjtGetFirstToken();
        String nomeFuncao = nomeToken.image;
        
        System.out.print(nomeFuncao + "(");
        
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode argumentosNode = (SimpleNode) node.jjtGetChild(0);
            argumentosNode.jjtAccept(this, data);
        }
        
        System.out.print(")");
        return data;
    }

    @Override
    public Object visit(ASTArgumentos node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            Node child = node.jjtGetChild(i);
            if (child instanceof SimpleNode) {
                ((SimpleNode) child).jjtAccept(this, data);
            }
        }
        return data;
    }
}