package items;

import characters.Gerolando;

public abstract class Item {

    protected String nombre;

    public Item(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public abstract void usar();

    public void usar(Gerolando jugador) {
        usar();
    }
}