package items;

import characters.Gerolando;

public class Pocion extends Consumible {

    public Pocion(String nombre, int precio, String tipo, int valor, String spritePath) {
        super(nombre, precio, tipo, valor, spritePath);
    }

    @Override
    public void usar(Gerolando jugador) {
        if (jugador == null) return;

        if (tipo.equalsIgnoreCase("vida")) {
            if (jugador.getVidaActual() >= jugador.getVidaMax()) {
                System.out.println("La vida ya está al máximo.");
                return;
            }

            jugador.curarVida(valor);
            System.out.println("Gerolando usó " + getNombre() + " y recuperó " + valor + " de vida.");
            return;
        }

        if (tipo.equalsIgnoreCase("mana")) {
            if (jugador.getManaActual() >= jugador.getManaMax()) {
                System.out.println("El mana ya está al máximo.");
                return;
            }

            jugador.curarMana(valor);
            System.out.println("Gerolando usó " + getNombre() + " y recuperó " + valor + " de mana.");
            return;
        }

        System.out.println("Tipo de poción no reconocido: " + tipo);
    }
}