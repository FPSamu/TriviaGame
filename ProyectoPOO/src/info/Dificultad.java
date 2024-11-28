package info;
public enum Dificultad {
    FACIL("Facil"),
    NORMAL("Normal"),
    DIFICIL("Dificil");

    private final String dificulty;

    private Dificultad(String dificulty) {
        this.dificulty = dificulty;
    }

    public String toString() {
        return this.dificulty;
    }
}
