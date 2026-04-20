package game;

import characters.Gerolando;
import characters.Enemigo;

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

        // Por ahora solo ataca (meter menu despues)
        jugador.atacar(enemigo);
    }

    // Turno del enemigo
    private static void turnoEnemigo(Enemigo enemigo, Gerolando jugador) {
        System.out.println("Turno de " + enemigo.nombre);

        enemigo.atacar(jugador);
    }
}