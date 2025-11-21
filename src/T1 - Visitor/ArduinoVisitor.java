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
        System.out.println("  // Código que se repete continuamente");
        System.out.println("}");
        return data;
    }

    @Override
    public Object visit(ASTSingleLineComment node, Object data) {
        // Usando jjtGetFirstToken() que está disponível com TRACK_TOKENS
        Token t = node.jjtGetFirstToken();
        System.out.println(t.image); // Use print em vez de println se o token já incluir quebra de linha
        return data;
    }

    @Override
    public Object visit(ASTMultiLineComment node, Object data) {
        Token t = node.jjtGetFirstToken();
        System.out.println(t.image);
        return data;
    }

    @Override
    public Object visit(ASTFormalComment node, Object data) {
        Token t = node.jjtGetFirstToken();
        System.out.println(t.image);
        return data;
    }
}