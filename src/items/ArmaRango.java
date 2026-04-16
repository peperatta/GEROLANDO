package items;

public class ArmaRango extends Arma{
    public ArmaRango(String nombre, int ataque){
        super(nombre, ataque);

    }

    @Override
    public void usar() {
        System.out.println("Ataque a distancia con " + nombre);
    }
}
