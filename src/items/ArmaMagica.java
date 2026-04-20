package items;

public class ArmaMagica extends Arma{
    public ArmaMagica(String nombre, int ataque){
        super(nombre, ataque);

    }

    @Override
    public void usar() {
        System.out.println("Ataque magico " + "de " + this.getAtaque() +" con: "+ nombre );
    }
}