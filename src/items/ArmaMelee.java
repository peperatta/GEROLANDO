package items;

public class ArmaMelee extends Arma{
    public ArmaMelee(String nombre, int precio, int ataque, int peso, String spritePath){
        super(nombre, precio, ataque, peso, spritePath);
        this.tipo = "melee";

    }

    @Override
    public void usar() {
        System.out.println("Equipaste el arma: " + nombre);
    }
}
