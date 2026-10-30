package minilang;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private int pos;

    public Parser(ArrayList<Token> tokens){
        this.tokens = tokens;
        this.pos = 0;
    }

    private Token atual(){
        return tokens.get(pos);
    }

    private String encontrado(){
        if (atual().getTipo() == TipoToken.FIM) {
            return "fim do arquivo";
        }
        return "'" + atual().getTexto() + "'";
    }

    private void consumir(TipoToken esperado, String descricao) throws ErroSintatico{
        if (atual().getTipo() != esperado) {
            throw new ErroSintatico("esperava " + descricao + " mas encontrou " + encontrado(), atual().getLinha(), atual().getColuna());
        }
        pos++;
    }

    private void tipo() throws ErroSintatico{
        if (atual().getTipo() == TipoToken.INTEIRO || atual().getTipo() == TipoToken.DCIMAL){
            pos++;
        } else {
            throw new ErroSintatico("esperava 'inteiro' ou 'dcimal' mas encontrou " + encontrado(), atual().getLinha(), atual().getColuna());
        }
    }

    private void valor() throws ErroSintatico {
        TipoToken tipo = atual().getTipo();

        if (tipo == TipoToken.ID || tipo == TipoToken.NUM || tipo == TipoToken.NUM_DECIMAL) {
            pos++;
        } else {
            throw new ErroSintatico("esperava identificador ou número mas encontrou " + encontrado(),atual().getLinha(),atual().getColuna());
        }
    }

    private void oprel() throws ErroSintatico{
        TipoToken tipo = atual().getTipo();

        if (tipo == TipoToken.IGUAL
                || tipo == TipoToken.DIFERENTE
                || tipo == TipoToken.MENOR
                || tipo == TipoToken.MAIOR
                || tipo == TipoToken.MENOR_IGUAL
                || tipo == TipoToken.MAIOR_IGUAL){
            pos++;
        } else {
            throw new ErroSintatico("esperava operador relacional mas encontrou " + encontrado(), atual().getLinha(), atual().getColuna());
        }
    }

    private void condicao() throws ErroSintatico{
        valor();
        oprel();
        valor();
    }

    private void declaracao() throws ErroSintatico{
        tipo();
        consumir(TipoToken.ID, "identificador");

        if (atual().getTipo() == TipoToken.ATRIBUICAO){
            consumir(TipoToken.ATRIBUICAO, "'='");
            valor();
        }

        consumir(TipoToken.PONTO_VIRGULA, "';'");
    }

    private void bloco() throws ErroSintatico{
        consumir(TipoToken.ABRE_CHAVE, "'{'");

        while (atual().getTipo() == TipoToken.SE || atual().getTipo() == TipoToken.ENQUANTO){
            if (atual().getTipo() == TipoToken.SE){
                ifElse();
            } else {
                whileLoop();
            }
        }

        consumir(TipoToken.FECHA_CHAVE, "'}'");
    }

    private void whileLoop() throws ErroSintatico{
        consumir(TipoToken.ENQUANTO, "'enquanto'");
        consumir(TipoToken.ABRE_PAREN, "'('");
        condicao();
        consumir(TipoToken.FECHA_PAREN, "')'");
        bloco();
    }

    private void ifElse() throws ErroSintatico{
        consumir(TipoToken.SE, "'se'");
        consumir(TipoToken.ABRE_PAREN, "'('");
        condicao();
        consumir(TipoToken.FECHA_PAREN, "')'");
        bloco();
        consumir(TipoToken.SENN, "'senn'");
        bloco();
    }

    private void programa() throws ErroSintatico{
        consumir(TipoToken.OI, "'oi'");

        while (atual().getTipo() == TipoToken.INTEIRO || atual().getTipo() == TipoToken.DCIMAL){
            declaracao();
        }

        ifElse();

        while (atual().getTipo() == TipoToken.SE){
            ifElse();
        }

        consumir(TipoToken.TCHAU, "'tchau'");
        //if alguma coisa -> exception 'fora do escopo do programa. lx cy'
        consumir(TipoToken.FIM, "fim do arquivo");
    }

    public void analisar() throws ErroSintatico{
        programa();
    }
}
