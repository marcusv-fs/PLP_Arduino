import java.util.ArrayList;
import java.util.List;

public class ArduinoVisitor implements ADSLVisitor {
    private int indentLevel = 0;
    
    private String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
    
    // Método para extração de operadores em um nó
    private List<Token> extractOperators(SimpleNode node, int[] operatorKinds) {
        List<Token> operators = new ArrayList<>();
        for (Token t = node.jjtGetFirstToken(); t != null; t = t.next) {
            for (int kind : operatorKinds) {
                if (t.kind == kind) {
                    operators.add(t);
                    break;
                }
            }
        }
        return operators;
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
        Node pinoNode = node.jjtGetChild(0);
        
        System.out.print(getIndent());
        
        if (pinoNode instanceof ASTPinos) {
            SimpleNode tipoPinoNode = (SimpleNode) ((SimpleNode) pinoNode).jjtGetChild(0);
            Token pinoToken = tipoPinoNode.jjtGetFirstToken();
            String pino = pinoToken.image;

            if (tipoPinoNode instanceof ASTPinosA) {
                System.out.println("analogRead(" + pino + ");");
            } else {
                System.out.println("digitalRead(" + pino + ");");
            }
        } else {
            System.out.print("digitalRead(");
            ((SimpleNode) pinoNode).jjtAccept(this, data);
            System.out.println(");");
        }
        return data;
    }

    @Override
    public Object visit(ASTDigitalWrite node, Object data) {
        Node pinoNode = node.jjtGetChild(0);
        SimpleNode valorNode = (SimpleNode) node.jjtGetChild(1);
        
        System.out.print(getIndent() + "digitalWrite(");
        
        if (pinoNode instanceof ASTPinos) {
            SimpleNode tipoPinoNode = (SimpleNode) ((SimpleNode) pinoNode).jjtGetChild(0);
            Token pinoToken = tipoPinoNode.jjtGetFirstToken();
            String pino = pinoToken.image;
            
            if (tipoPinoNode instanceof ASTPinosA) {
                int analogPin = Integer.parseInt(pino.substring(1));
                pino = String.valueOf(analogPin + 14);
            }
            
            System.out.print(pino);
        } else {
            ((SimpleNode) pinoNode).jjtAccept(this, data);
        }
        
        System.out.print(", ");
        valorNode.jjtAccept(this, data);
        System.out.println(");");
        
        return data;
    }

    @Override
    public Object visit(ASTAnalogWrite node, Object data) {
        Node pinoNode = node.jjtGetChild(0);
        SimpleNode valorNode = (SimpleNode) node.jjtGetChild(1);
        
        System.out.print(getIndent() + "analogWrite(");
        
        if (pinoNode instanceof ASTPinosPWM) {
            Token pinoToken = ((SimpleNode) pinoNode).jjtGetFirstToken();
            System.out.print(pinoToken.image);
        } else {
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
                System.out.println((i == 0 ? getIndent() : getIndent()) + lines[i]);
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
                System.out.println((i == 0 ? getIndent() : getIndent()) + lines[i]);
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
        str = str.substring(1, str.length() - 1);
        
        str = str.replace("\\n", "\" + \"\\n\" + \"");
        str = str.replace("\\t", "\" + \"\\t\" + \"");
        str = str.replace("\\\"", "\" + \"\\\"\" + \"");
        
        if (str.contains("\" + \"")) {
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
        
        Token idToken = node.jjtGetFirstToken().next;
        String identifier = idToken.image;
        
        if (node.jjtGetNumChildren() > 1) {
            SimpleNode initNode = (SimpleNode) node.jjtGetChild(1);
            if (initNode instanceof ASTInicializador) {
                Token initToken = initNode.jjtGetFirstToken();
                String valor = initToken.image;
                
                if (initToken.kind == ADSLConstants.STRING) {
                    valor = processarString(valor);
                }
                System.out.println(getIndent() + mapType(tipo) + " " + identifier + " = " + valor + ";");
            } else {
                System.out.print(getIndent() + mapType(tipo) + " " + identifier + " = ");
                initNode.jjtAccept(this, data);
                System.out.println(";");
            }
        } else {
            System.out.println(getIndent() + mapType(tipo) + " " + identifier + ";");
        }
        return data;
    }

    @Override
    public Object visit(ASTTiposVar node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTInicializador node, Object data) {
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
            default: return "int";
        }
    }

    @Override
    public Object visit(ASTSeSenao node, Object data) {
        String currentIndent = getIndent();
        
        System.out.print(currentIndent + "if (");
        SimpleNode condicaoNode = (SimpleNode) node.jjtGetChild(0);
        condicaoNode.jjtAccept(this, data);
        System.out.println(") {");
        
        indentLevel++;
        SimpleNode blocoSeNode = (SimpleNode) node.jjtGetChild(1);
        blocoSeNode.jjtAccept(this, data);
        indentLevel--;
        
        System.out.println(currentIndent + "}");
        
        if (node.jjtGetNumChildren() > 2) {
            SimpleNode blocoSenaoNode = (SimpleNode) node.jjtGetChild(2);
            System.out.println(currentIndent + "else {");
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
        if (node.jjtGetNumChildren() == 1) {
            Node child = node.jjtGetChild(0);
            if (child instanceof SimpleNode) {
                ((SimpleNode) child).jjtAccept(this, data);
            }
        } else if (node.jjtGetNumChildren() == 2) {
            Node left = node.jjtGetChild(0);
            Node right = node.jjtGetChild(1);
            
            if (left instanceof SimpleNode) {
                ((SimpleNode) left).jjtAccept(this, data);
            }
            
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
        int[] additiveOperators = {ADSLConstants.PLUS, ADSLConstants.MINUS};
        List<Token> operators = extractOperators(node, additiveOperators);
        
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i > 0) {
                int operatorIndex = i - 1;
                if (operatorIndex < operators.size()) {
                    System.out.print(" " + operators.get(operatorIndex).image + " ");
                } else {
                    System.out.print(" + ");
                }
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
        int[] multiplicativeOperators = {ADSLConstants.STAR, ADSLConstants.SLASH, ADSLConstants.REM};
        List<Token> operators = extractOperators(node, multiplicativeOperators);
        
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i > 0) {
                int operatorIndex = i - 1;
                if (operatorIndex < operators.size()) {
                    System.out.print(" " + operators.get(operatorIndex).image + " ");
                } else {
                    System.out.print(" * ");
                }
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
        Token firstToken = node.jjtGetFirstToken();
        if (firstToken != null && 
            (firstToken.kind == ADSLConstants.PLUS || 
             firstToken.kind == ADSLConstants.MINUS ||
             firstToken.kind == ADSLConstants.BANG ||
             firstToken.kind == ADSLConstants.TILDE)) {
            System.out.print(firstToken.image);
        }
        
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
        if (node.jjtGetNumChildren() == 0) {
            Token firstToken = node.jjtGetFirstToken();
            if (firstToken != null) {
                if (firstToken.kind == ADSLConstants.STRING) {
                    System.out.print(processarString(firstToken.image));
                } else if (firstToken.kind == ADSLConstants.LPAREN) {
                    System.out.print("(");
                } else if (firstToken.kind == ADSLConstants.RPAREN) {
                    System.out.print(")");
                } else {
                    System.out.print(firstToken.image);
                }
            }
        } else {
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                if (i > 0) {
                    System.out.print(" ");
                }
                Node child = node.jjtGetChild(i);
                if (child instanceof SimpleNode) {
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

        if (pino.startsWith("A")) {
            System.out.print("analogRead(" + pino + ")");
        } else {
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
        System.out.print(getIndent() + "for (int forCount = 0; forCount < ");
        
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode expressaoNode = (SimpleNode) node.jjtGetChild(0);
            expressaoNode.jjtAccept(this, data);
        }
        
        System.out.println("; forCount++) {");
        
        indentLevel++;
        if (node.jjtGetNumChildren() > 1) {
            SimpleNode blocoNode = (SimpleNode) node.jjtGetChild(1);
            blocoNode.jjtAccept(this, data);
        }
        indentLevel--;
        
        System.out.println(getIndent() + "}");
        
        return data;
    }
    
    @Override
    public Object visit(ASTBlocoRepita node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTFuncao node, Object data) {
        Token nomeToken = node.jjtGetFirstToken().next;
        String nomeFuncao = nomeToken.image;
        
        String tipoRetorno = "void";
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode tipoRetornoNode = (SimpleNode) node.jjtGetChild(0);
            if (tipoRetornoNode instanceof ASTTipoRetorno) {
                Token tipoToken = tipoRetornoNode.jjtGetFirstToken();
                tipoRetorno = mapType(tipoToken.image);
            }
        }
        
        System.out.print(tipoRetorno + " " + nomeFuncao + "(");
        
        int startIndex = (tipoRetorno.equals("void") && node.jjtGetNumChildren() > 0) ? 0 : 1;
        if (node.jjtGetNumChildren() > startIndex) {
            SimpleNode parametrosNode = (SimpleNode) node.jjtGetChild(startIndex);
            parametrosNode.jjtAccept(this, data);
        }
        
        System.out.println(") {");
        
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
        
        Token idToken = node.jjtGetFirstToken().next;
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