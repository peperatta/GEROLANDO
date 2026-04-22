package items;

public class ArmaMagica extends Arma{
    public ArmaMagica(String nombre, int precio, int ataque, int peso, String spritePath){
        super(nombre, precio, ataque, peso, spritePath);
        this.tipo = "magica";

    }

    @Override
    public void usar() {
        System.out.println("Equipaste el arma: " + nombre);
    }
}