public class ArduinoVisitor implements ADSLVisitor {
    
    @Override
    public Object visit(SimpleNode node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTStart node, Object data) {
        // Gera o código Arduino completo
        System.out.println("// Código gerado automaticamente");
        System.out.println();
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTConfig node, Object data) {
        // Gera a função setup()
        System.out.println("void setup() {");
        System.out.println("  // Configurações iniciais do Arduino");
        System.out.println("}");
        System.out.println();
        return data;
    }

    @Override
    public Object visit(ASTRepita node, Object data) {
        // Gera a função loop()
        System.out.println("void loop() {");
        System.out.println("  // Código que se repete continuamente");
        System.out.println("}");
        return data;
    }
}