package items;

public class Armadura extends Item{
    private int defensa;
    private int peso;

    public Armadura(String nombre, int defensa, int peso) {
        super(nombre);
        this.defensa = defensa;
        this.peso = peso;
    }

    public int getDefensa() {
        return defensa;
    }
    public int getPeso(){
        return peso;
    }

    @Override
    public void usar() {
        System.out.println("Equipaste la armadura: " + nombre);
    }
}
