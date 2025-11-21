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
        // O nó Frequencia é o primeiro filho
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
        // Primeiro filho: ValorTempo
        SimpleNode valorNode = (SimpleNode) node.jjtGetChild(0);
        Token valorToken = valorNode.jjtGetFirstToken();
        String valor = valorToken.image;
        
        // Segundo filho: UnidadeTempo
        SimpleNode unidadeNode = (SimpleNode) node.jjtGetChild(1);
        Token unidadeToken = unidadeNode.jjtGetFirstToken();
        String unidade = unidadeToken.image;
        
        // Converter para milissegundos
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
            System.out.print(t.image);
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
                    System.out.println(lines[i]);
                } else {
                    System.out.println(lines[i]);
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
                    System.out.println(lines[i]);
                } else {
                    System.out.println(lines[i]);
                }
            }
        }
        return data;
    }
}