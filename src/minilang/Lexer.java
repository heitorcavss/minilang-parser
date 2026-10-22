package minilang;

import java.util.ArrayList;

public class Lexer {
    private String texto;
    private int pos;
    private int linha;
    private int coluna;

    public Lexer(String texto){
        this.texto = texto;
        this.pos = 0;
        this.linha = 1;
        this.coluna = 1;
    }

    private Token criar(TipoToken tipo, String simbolo){
        Token token = new Token(tipo, simbolo, linha, coluna);
        pos += simbolo.length();
        coluna += simbolo.length();
        return token;
    }

    private char proximoChar(){
        if (pos + 1 < texto.length()) {
            return texto.charAt(pos + 1);
        }
        return '\0';
    }

    private TipoToken tipoDaPalavra(String palavra){
        if (palavra.equals("oi")) {
            return TipoToken.OI;
        }
        if (palavra.equals("tchau")) {
            return TipoToken.TCHAU;
        }
        if (palavra.equals("inteiro")) {
            return TipoToken.INTEIRO;
        }
        if (palavra.equals("dcimal")) {
            return TipoToken.DCIMAL;
        }
        if (palavra.equals("se")) {
            return TipoToken.SE;
        }
        if (palavra.equals("senn")) {
            return TipoToken.SENN;
        }
        return TipoToken.ID;
    }

    private Token lerPalavra(){
        int inicio = pos;
        int colunaInicio = coluna;

        while (pos < texto.length() && Character.isLetterOrDigit(texto.charAt(pos))) {
            pos++;
            coluna++;
        }

        String palavra = texto.substring(inicio, pos);
        return new Token(tipoDaPalavra(palavra), palavra, linha, colunaInicio);
    }

    // Por enquanto so inteiros.
    private Token lerNumero(){
        int inicio = pos;
        int colunaInicio = coluna;

        while (pos < texto.length() && Character.isDigit(texto.charAt(pos))) {
            pos++;
            coluna++;
        }

        String numero = texto.substring(inicio, pos);
        return new Token(TipoToken.NUM, numero, linha, colunaInicio);
    }

    private Token lerSimbolo() throws ErroSintatico{
        char c = texto.charAt(pos);
        char prox = proximoChar();

        switch (c) {
            case '(':
                return criar(TipoToken.ABRE_PAREN, "(");
            case ')':
                return criar(TipoToken.FECHA_PAREN, ")");
            case '{':
                return criar(TipoToken.ABRE_CHAVE, "{");
            case '}':
                return criar(TipoToken.FECHA_CHAVE, "}");
            case ';':
                return criar(TipoToken.PONTO_VIRGULA, ";");
            case '+':
                return criar(TipoToken.MAIS, "+");
            case '-':
                return criar(TipoToken.MENOS, "-");
            case '*':
                return criar(TipoToken.VEZES, "*");
            case '/':
                return criar(TipoToken.DIVIDIR, "/");
            case '=':
                if (prox == '=') {
                    return criar(TipoToken.IGUAL, "==");
                }
                return criar(TipoToken.ATRIBUICAO, "=");
            case '!':
                if (prox == '=') {
                    return criar(TipoToken.DIFERENTE, "!=");
                }
                break;
            case '<':
                if (prox == '=') {
                    return criar(TipoToken.MENOR_IGUAL, "<=");
                }
                return criar(TipoToken.MENOR, "<");
            case '>':
                if (prox == '=') {
                    return criar(TipoToken.MAIOR_IGUAL, ">=");
                }
                return criar(TipoToken.MAIOR, ">");
            default:
                break;
        }

        throw new ErroSintatico("caractere inválido '" + c + "'", linha, coluna);
    }

    public ArrayList<Token> tokenizar() throws ErroSintatico{
        ArrayList<Token> tokens = new ArrayList<>();

        while (pos < texto.length()) {
            char c = texto.charAt(pos);

            if (c == '\n') {
                pos++;
                linha++;
                coluna = 1;
            } else if (c == ' ' || c == '\t' || c == '\r') {
                pos++;
                coluna++;
            } else if (Character.isLetter(c)) {
                tokens.add(lerPalavra());
            } else if (Character.isDigit(c)) {
                tokens.add(lerNumero());
            } else {
                tokens.add(lerSimbolo());
            }
        }

        tokens.add(new Token(TipoToken.FIM, "", linha, coluna));
        return tokens;
    }
}
