package items;

public abstract class Arma extends Item {

    private int ataque;
    private int peso;
    public String spritePath;

    public Arma(String nombre, int ataque, int peso, String spritePath) {
        super(nombre);
        this.ataque = ataque;
        this.peso = peso;
        this.spritePath = spritePath;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getPeso() {
        return peso;
    }

    public abstract void usar();
}