package items;

public class ArmaRango extends Arma{
    public ArmaRango(String nombre, int precio, int ataque, int peso, String spritePath){
        super(nombre, precio, ataque, peso, spritePath);
        this.tipo = "rango";

    }

    @Override
    public void usar() {
        System.out.println("Equipaste el arma: " + nombre);
    }
}
