package game;

import characters.Gerolando;
import characters.Enemigo;
import items.Arma;
import items.Armadura;
import items.Item;

import java.util.Scanner;

public class Combate {

    public static void iniciarCombate(Gerolando jugador, Enemigo enemigo) {

        System.out.println("¡Comienza el combate contra " + enemigo.nombre + "!\n");

        // Determinar quién empieza
        boolean turnoJugador = jugador.getVelocidad() >= enemigo.getVelocidad();

        if (turnoJugador) {
            System.out.println("Gerolando es más rápido y ataca primero.\n");
        } else {
            System.out.println(enemigo.nombre + " es más rápido y ataca primero.\n");
        }

        // Loop principal del combate
        while (jugador.vidaActual > 0 && enemigo.vidaActual > 0) {

            if (turnoJugador) {
                turnoJugador(jugador, enemigo);
            } else {
                turnoEnemigo(enemigo, jugador);
            }

            // Cambiar turno
            turnoJugador = !turnoJugador;

            System.out.println("----------------------------------");
        }

        // Resultado final
        if (jugador.vidaActual > 0) {
            System.out.println("¡Ganaste el combate!");
        } else {
            System.out.println("Has sido derrotado...");
        }
    }

    // Turno del jugador
    private static void turnoJugador(Gerolando jugador, Enemigo enemigo) {
        System.out.println("Turno de Gerolando");
        Scanner scanner = new Scanner(System.in);
        int opcion = scanner.nextInt();
        switch(opcion){
            case 1: jugador.atacar(enemigo);
            break;
            case 2: jugador.inventario.mostrarInventario();
            int itemIndex = scanner.nextInt() -1;
            if(itemIndex >= 0 && itemIndex < jugador.inventario.size()){
                Item item = jugador.inventario.getItems().get(itemIndex);
                if (item instanceof Arma){
                    jugador.equiparArma((Arma) item);
                }
                else if(item instanceof Armadura){
                    jugador.equiparArmadura((Armadura) item);
                }

            } else {
                System.out.println("Índice de arma no válido.");
            }
            break;
            case 3: jugador.imprimirEstado();
            break;
            default: System.out.println("Opción no válida. Se pierde el turno.");
        }
    }

    // Turno del enemigo
    private static void turnoEnemigo(Enemigo enemigo, Gerolando jugador) {
        System.out.println("Turno de " + enemigo.nombre);

        enemigo.atacar(jugador);
    }
}