package items;

import characters.Gerolando;

public abstract class Item {

    protected String nombre;
    private int precio;

    public Item(String nombre, int precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }
    public int getPrecio() {
        return precio;
    }

    public abstract void usar();
    public abstract TipoItem getTipo();

    public void usar(Gerolando jugador) {
        usar();
    }
}