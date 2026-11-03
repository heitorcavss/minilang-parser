package minilang;

import java.util.ArrayList;
import java.util.HashSet;

public class Parser {
    private ArrayList<Token> tokens;
    private int pos;
    private HashSet<String> declaradas;

    public Parser(ArrayList<Token> tokens){
        this.tokens = tokens;
        this.pos = 0;
        this.declaradas = new HashSet<>();
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

        if (tipo == TipoToken.ID && !declaradas.contains(atual().getTexto())) {
            throw new ErroSintatico("variável '" + atual().getTexto() + "' não foi declarada", atual().getLinha(), atual().getColuna());
        }

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
        Token nome = atual();
        consumir(TipoToken.ID, "identificador");

        if (declaradas.contains(nome.getTexto())){
            throw new ErroSintatico("variável '" + nome.getTexto() + "' já foi declarada", nome.getLinha(), nome.getColuna());
        }

        if (atual().getTipo() == TipoToken.ATRIBUICAO){
            consumir(TipoToken.ATRIBUICAO, "'='");
            valor();
        }

        consumir(TipoToken.PONTO_VIRGULA, "';'");
        declaradas.add(nome.getTexto());
    }

    private void comando() throws ErroSintatico{
        if (atual().getTipo() == TipoToken.SE){
            ifElse();
        } else {
            whileLoop();
        }
    }

    private void bloco() throws ErroSintatico{
        consumir(TipoToken.ABRE_CHAVE, "'{'");

        while (atual().getTipo() == TipoToken.SE || atual().getTipo() == TipoToken.ENQUANTO){
            comando();
        }

        if (atual().getTipo() == TipoToken.INTEIRO || atual().getTipo() == TipoToken.DCIMAL){
            throw new ErroSintatico("declaração não é permitida dentro de 'se' ou 'enquanto', apenas no bloco de declarações", atual().getLinha(), atual().getColuna());
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

        while (atual().getTipo() == TipoToken.SE || atual().getTipo() == TipoToken.ENQUANTO){
            comando();
        }

        consumir(TipoToken.TCHAU, "'tchau'");

        if (atual().getTipo() != TipoToken.FIM){
            throw new ErroSintatico("código fora do escopo do programa: " + encontrado(), atual().getLinha(), atual().getColuna());
        }
    }

    public void analisar() throws ErroSintatico{
        programa();
    }
}
