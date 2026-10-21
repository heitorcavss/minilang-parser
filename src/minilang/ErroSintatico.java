package minilang;

public class ErroSintatico extends Exception {
    private int linha;
    private int coluna;

    public ErroSintatico(String mensagem, int linha, int coluna){
        super(mensagem);
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getLinha(){
        return linha;
    }

    public int getColuna(){
        return coluna;
    }

    @Override
    public String getMessage(){
        return "Erro sintático na linha " + linha + ", coluna " + coluna + ": " + super.getMessage();
    }
}
