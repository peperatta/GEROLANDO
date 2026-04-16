package items;

public class Armadura extends Item{
    private int defensa;

    public Armadura(String nombre, int defensa) {
        super(nombre);
        this.defensa = defensa;
    }

    public int getDefensa() {
        return defensa;
    }

    @Override
    public void usar() {
        System.out.println("Equipaste la armadura: " + nombre);
    }
}
