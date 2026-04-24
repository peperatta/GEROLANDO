package items;

import characters.Gerolando;

public abstract class Consumible extends Item {

    public String tipo;
    protected int valor;
    protected String spritePath;

    public Consumible(String nombre, int precio, String tipo, int valor, String spritePath) {
        super(nombre, precio);
        this.tipo = tipo;
        this.valor = valor;
        this.spritePath = spritePath;
    }

    public int getValor() {
        return valor;
    }

    public String getSpritePath() {
        return spritePath;
    }


    @Override
    public void usar() {
        System.out.println("Este consumible necesita un objetivo.");
    }

    @Override
    public abstract void usar(Gerolando jugador);
}