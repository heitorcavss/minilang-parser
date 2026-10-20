package minilang;

public enum TipoToken {
    // palavras reservadas
    OI,
    TCHAU,
    INT,
    DOUBLE,
    IF,
    ELSE,

    // ids e numero
    ID,
    NUM,

    // delimitadores
    ABRE_PAREN,     // (
    FECHA_PAREN,    // )
    ABRE_CHAVE,     // {
    FECHA_CHAVE,    // }
    PONTO_VIRGULA,  // ;

    // operacoes
    ATRIBUICAO,     // =
    MAIS,           // +
    MENOS,          // -
    VEZES,          // *
    DIVIDIR,        // /

    // op relacionais
    IGUAL,          // ==
    DIFERENTE,      // !=
    MENOR,          // <
    MAIOR,          // >
    MENOR_IGUAL,    // <=
    MAIOR_IGUAL,    // >=

    // Fim da lista de tokens
    FIM
}
