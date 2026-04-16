package items;

public abstract class Arma extends Item {

    private int ataque;

    public Arma(String nombre, int ataque) {
        super(nombre);
        this.ataque = ataque;
    }

    public int getAtaque() {
        return ataque;
    }

    public abstract void usar();
}