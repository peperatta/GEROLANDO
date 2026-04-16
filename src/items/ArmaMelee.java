package items;

public class ArmaMelee extends Arma{
    public ArmaMelee(String nombre, int ataque){
        super(nombre, ataque);

    }

    @Override
    public void usar() {
        System.out.println("Ataque cuerpo a cuerpo con " + nombre);
    }
}
