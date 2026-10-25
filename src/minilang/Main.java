package minilang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args){
        String caminho = "teste.txt";

        try {
            String texto = Files.readString(Path.of(caminho));

            Lexer lexer = new Lexer(texto);
            ArrayList<Token> tokens = lexer.tokenizar();

            Parser parser = new Parser(tokens);
            parser.analisar();

            System.out.println("Programa válido.");
        } catch (IOException e) {
            System.out.println("Não foi possível ler o arquivo: " + caminho);
        } catch (ErroSintatico e) {
            System.out.println(e.getMessage());
        }
    }
}
