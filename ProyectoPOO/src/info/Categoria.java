package info;

public enum Categoria {
    HISTORIA("Historia"),
    CIENCIA("Ciencia"),
    ENTRETENIMIENTO("Entretenimiento"),
    DEPORTES("Deportes"),
    GEOGRAFIA("Geografia"),
    RANDOM("Random");

    private final String category;

    private Categoria(String category) {
        this.category = category;
    }

    public String toString() {
        return this.category;
    }
}
