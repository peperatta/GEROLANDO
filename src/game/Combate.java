package game;

import characters.Enemigo;
import characters.Gerolando;
import items.Item;

import java.util.Scanner;

public class Combate {
    static Scanner scanner = new Scanner(System.in);

    public static void iniciarCombate(Gerolando jugador, Enemigo enemigo) {
        System.out.println("¡Comienza el combate contra " + enemigo.nombre + "!\n");

        boolean turnoJugador = jugador.getVelocidad() >= enemigo.getVelocidad();

        if (turnoJugador) {
            System.out.println("Gerolando es más rápido y ataca primero.\n");
        } else {
            System.out.println(enemigo.nombre + " es más rápido y ataca primero.\n");
        }

        while (jugador.estaVivo() && enemigo.vidaActual > 0) {
            if (turnoJugador) {
                turnoJugador(jugador, enemigo);
            } else {
                turnoEnemigo(enemigo, jugador);
            }

            turnoJugador = !turnoJugador;
            System.out.println("----------------------------------");
        }

        if (jugador.estaVivo()) {
            System.out.println("¡Ganaste el combate!");
            int xpGanada = enemigo.getVida() / 5;
            int oroGanado = enemigo.getVida() / 10;

            jugador.ganarXP(xpGanada);
            jugador.ganarOro(oroGanado);
        } else {
            System.out.println("Has sido derrotado...");
        }
    }

    private static void turnoJugador(Gerolando jugador, Enemigo enemigo) {
        boolean accionRealizada = false;

        while (!accionRealizada) {
            System.out.println("Turno de Gerolando");
            System.out.println("1. Atacar");
            System.out.println("2. Inventario");
            System.out.println("3. Ver estado");
            System.out.print("Elige una opción: ");

            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    jugador.atacar(enemigo);
                    accionRealizada = true;
                    break;

                case 2:
                    accionRealizada = manejarInventarioEnCombate(jugador);
                    break;

                case 3:
                    jugador.imprimirEstado();
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static boolean manejarInventarioEnCombate(Gerolando jugador) {
        if (jugador.inventario.size() == 0) {
            System.out.println("El inventario está vacío.");
            return false;
        }

        System.out.println("\n=== INVENTARIO ===");
        jugador.inventario.mostrarInventario();
        System.out.println("0. Cancelar");
        System.out.print("Selecciona un item por número para usar/equipar: ");

        int opcion = scanner.nextInt();

        if (opcion == 0) {
            System.out.println("Regresando al menú de combate...");
            return false;
        }

        int itemIndex = opcion - 1;

        if (itemIndex >= 0 && itemIndex < jugador.inventario.size()) {
            Item item = jugador.inventario.getItems().get(itemIndex);
            jugador.usarItem(item);
            return true; // usar/equipar sí consume el turno
        } else {
            System.out.println("Índice de item no válido.");
            return false;
        }
    }

    private static void turnoEnemigo(Enemigo enemigo, Gerolando jugador) {
        System.out.println("Turno de " + enemigo.nombre);
        enemigo.atacar(jugador);
    }
}