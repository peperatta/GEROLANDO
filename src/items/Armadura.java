package items;

public class Armadura extends Item{
    private int defensa;
    private int peso;
    public String spritePath;

    public Armadura(String nombre, int defensa, int peso, String spritePath) {
        super(nombre);
        this.defensa = defensa;
        this.peso = peso;
        this.spritePath = spritePath;
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
