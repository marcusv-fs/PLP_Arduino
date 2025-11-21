public class ArduinoVisitor implements ADSLVisitor {

    @Override
    public Object visit(SimpleNode node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTStart node, Object data) {
        System.out.println("Iniciando análise da árvore");
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTConfig node, Object data) {
        System.out.println("Configuração encontrada");
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTRepita node, Object data) {
        System.out.println("Comando Repita encontrado");
        return node.childrenAccept(this, data);
    }
}