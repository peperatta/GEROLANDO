package items;

public class ArmaMagica extends Arma{
    public ArmaMagica(String nombre, int ataque, int peso, String spritePath){
        super(nombre, ataque, peso, spritePath);
        this.tipo = "magica";

    }

    @Override
    public void usar() {
        System.out.println("Whooosh! ");
    }
}