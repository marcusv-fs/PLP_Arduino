public class ArduinoVisitor implements ADSLVisitor {
    
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
        node.childrenAccept(this, data);
        System.out.println("}");
        System.out.println();
        return data;
    }

    @Override
    public Object visit(ASTRepita node, Object data) {
        System.out.println("void loop() {");
        node.childrenAccept(this, data);
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
        
        System.out.println("  pinMode(" + pino + ", " + arduinoMode + ");");
        return data;
    }

    @Override
    public Object visit(ASTFreqMonitor node, Object data) {
        SimpleNode freqNode = (SimpleNode) node.jjtGetChild(0);
        Token freqToken = freqNode.jjtGetFirstToken();
        String baudRate = freqToken.image;
        
        System.out.println("  Serial.begin(" + baudRate + ");");
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
        System.out.println("  delay(" + ms + ");");
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
        SimpleNode pinosNode = (SimpleNode) node.jjtGetChild(0);
        Token pinoToken = pinosNode.jjtGetFirstToken();
        String pino = pinoToken.image;

        // Decide automaticamente se é leitura digital ou analógica
        if (pinosNode instanceof ASTPinosA) {
            // Para pino analógico, usa analogRead
            int analogPin = Integer.parseInt(pino.substring(1));
            System.out.println("  analogRead(" + analogPin + ");");
        } else {
            // Para pino digital, usa digitalRead
            System.out.println("  digitalRead(" + pino + ");");
        }
        return data;
    }

    @Override
    public Object visit(ASTDigitalWrite node, Object data) {
        SimpleNode pinosNode = (SimpleNode) node.jjtGetChild(0);
        Token pinoToken = pinosNode.jjtGetFirstToken();
        String pino = pinoToken.image;
        
        SimpleNode valorNode = (SimpleNode) node.jjtGetChild(1);
        Token valorToken = valorNode.jjtGetFirstToken();
        String valor = valorToken.image;
        
        if (pinosNode instanceof ASTPinosA) {
            int analogPin = Integer.parseInt(pino.substring(1));
            pino = String.valueOf(analogPin + 14);
        }
        
        // Converter 0/1 para LOW/HIGH
        String arduinoValue = "LOW";
        if (valor.equals("1")) {
            arduinoValue = "HIGH";
        }
        
        System.out.println("  digitalWrite(" + pino + ", " + arduinoValue + ");");
        return data;
    }

    @Override
    public Object visit(ASTAnalogWrite node, Object data) {
        SimpleNode pinosNode = (SimpleNode) node.jjtGetChild(0);
        Token pinoToken = pinosNode.jjtGetFirstToken();
        String pino = pinoToken.image;
        
        SimpleNode valorNode = (SimpleNode) node.jjtGetChild(1);
        Token valorToken = valorNode.jjtGetFirstToken();
        String valor = valorToken.image;
        
        System.out.println("  analogWrite(" + pino + ", " + valor + ");");
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
            System.out.print("  " + t.image);
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
                    System.out.print("  " + lines[i]);
                } else {
                    System.out.print("\n  " + lines[i]);
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
                    System.out.print("  " + lines[i]);
                } else {
                    System.out.print("\n  " + lines[i]);
                }
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTSerialPrint node, Object data) {
        SimpleNode contentNode = (SimpleNode) node.jjtGetChild(0);
        Token contentToken = contentNode.jjtGetFirstToken();
        String content = contentToken.image;
        
        // Verifica se é string ou identificador
        if (contentToken.kind == ADSLConstants.STRING) {
            // Se for string, processa como antes
            content = processarString(content);
        }
        // Se for identificador, usa diretamente (não precisa de processamento)
        
        System.out.println("  Serial.println(" + content + ");");
        return data;
    }

    @Override
    public Object visit(ASTPrintContent node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTString node, Object data) {
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
            Token initToken = initNode.jjtGetFirstToken();
            String valor = initToken.image;
            
            // Processa strings (remove aspas)
            if (initToken.kind == ADSLConstants.STRING) {
                valor = processarString(valor);
            }
            
            inicializacao = " = " + valor;
        }
        
        // Mapeia o tipo do ADSL para o tipo do Arduino
        String arduinoType = mapType(tipo);
        
        System.out.println("  " + arduinoType + " " + identifier + inicializacao + ";");
        return data;
    }

    @Override
    public Object visit(ASTTiposVar node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTInicializador node, Object data) {
        return data;
    }

    private String mapType(String tipo) {
        switch (tipo) {
            case "byte": return "byte";
            case "int": return "int";
            case "unsigned int": return "unsigned int";
            case "long": return "long";
            case "unsigned long": return "unsigned long";
            case "float": return "float";
            case "char": return "char";
            case "String": return "String";
            case "Boolean": return "boolean";
            default: return "int"; // padrão
        }
    }

    @Override
    public Object visit(ASTSeSenao node, Object data) {
        // O primeiro filho é a condição
        SimpleNode condicaoNode = (SimpleNode) node.jjtGetChild(0);
        Token condicaoToken = condicaoNode.jjtGetFirstToken();
        String condicao = condicaoToken.image;
        
        System.out.println("  if (" + condicao + ") {");
        
        // Processa os comandos do bloco "se"
        // Começa do filho 1 até encontrar o senao ou terminar
        boolean inElse = false;
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof SimpleNode) {
                SimpleNode simpleChild = (SimpleNode) child;
                // Verifica se é um nó de comando (não é condição)
                if (!(simpleChild instanceof ASTCondicao)) {
                    simpleChild.jjtAccept(this, data);
                }
            }
        }
        
        System.out.println("  }");
        
        // Por enquanto, não processamos o senao automaticamente
        // Você precisaria modificar a estrutura do nó para identificar
        // onde começa o bloco senao
        // Esta é uma implementação simplificada
        
        return data;
    }

    @Override
    public Object visit(ASTCondicao node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTAtribuicao node, Object data) {
        Token idToken = node.jjtGetFirstToken();
        String identifier = idToken.image;
        
        SimpleNode expressaoNode = (SimpleNode) node.jjtGetChild(0);
        
        System.out.print("  " + identifier + " = ");
        expressaoNode.jjtAccept(this, data);
        System.out.println(";");
        
        return data;
    }

    @Override
    public Object visit(ASTExpressao node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTLeiaExpressao node, Object data) {
        SimpleNode pinosNode = (SimpleNode) node.jjtGetChild(0);
        Token pinoToken = pinosNode.jjtGetFirstToken();
        String pino = pinoToken.image;

        // Decide automaticamente se é leitura digital ou analógica
        if (pinosNode instanceof ASTPinosA) {
            // Para pino analógico, usa analogRead
            int analogPin = Integer.parseInt(pino.substring(1));
            System.out.print("analogRead(" + analogPin + ")");
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
}