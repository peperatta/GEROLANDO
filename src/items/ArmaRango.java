package items;

public class ArmaRango extends Arma{
    public ArmaRango(String nombre, int ataque, int peso, String spritePath){
        super(nombre, ataque, peso, spritePath);

    }

    @Override
    public void usar() {
        System.out.println("Ataque a distancia " + "de " + this.getAtaque() +" con: "+ nombre );
    }
}
