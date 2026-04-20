package items;

public abstract class Arma extends Item {

    private int ataque;
    private int peso;

    public Arma(String nombre, int ataque, int peso) {
        super(nombre);
        this.ataque = ataque;
        this.peso = peso;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getPeso() {
        return peso;
    }

    public abstract void usar();
}