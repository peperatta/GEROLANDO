package items;

public class ArmaMelee extends Arma{
    public ArmaMelee(String nombre, int ataque, int peso){
        super(nombre, ataque, peso);

    }

    @Override
    public void usar() {
        System.out.println("Ataque cuerpo a cuerpo " + "de " + this.getAtaque() +" con: "+ nombre );
    }
}
