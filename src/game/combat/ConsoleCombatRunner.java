package game.combat;

import characters.Enemigo;
import characters.Gerolando;

import java.util.Scanner;

public class ConsoleCombatRunner {
    private CombatSystem combatSystem;
    private Scanner scanner;

    public ConsoleCombatRunner(Gerolando jugador, Enemigo enemigo) {
        this.combatSystem = new CombatSystem(jugador, enemigo);
        this.scanner = new Scanner(System.in);
    }

    public boolean iniciar() {
        System.out.println(combatSystem.iniciarCombate());

        boolean combateActivo = true;
        boolean jugadorGano = false;

        while (combateActivo) {
            if (combatSystem.esTurnoJugador()) {
                CombatResult result = turnoJugador();

                if (!result.getMensaje().isEmpty()) {
                    System.out.println(result.getMensaje());
                }

                if (result.isCombateTerminado()) {
                    combateActivo = false;
                    jugadorGano = result.isJugadorGano();
                }
            }
        }

        return jugadorGano;
    }

    private CombatResult turnoJugador() {
        boolean accionRealizada = false;
        CombatResult resultado = null;

        while (!accionRealizada) {
            System.out.println("Turno de Gerolando");
            System.out.println("1. Atacar");
            System.out.println("2. Inventario");
            System.out.println("3. Ver estado");
            System.out.print("Elige una opción: ");

            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    resultado = combatSystem.atacar();
                    accionRealizada = true;
                    break;

                case 2:
                    resultado = manejarInventarioEnCombate();
                    accionRealizada = resultado != null;
                    break;

                case 3:
                    combatSystem.getJugador().imprimirEstado();
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        }

        return resultado;
    }

    private CombatResult manejarInventarioEnCombate() {
        Gerolando jugador = combatSystem.getJugador();

        if (jugador.inventario.size() == 0) {
            System.out.println("El inventario está vacío.");
            return null;
        }

        System.out.println("\n=== INVENTARIO ===");
        jugador.inventario.mostrarInventario(jugador);
        System.out.println("0. Cancelar");
        System.out.print("Selecciona un item por número para usar/equipar: ");

        int opcion = scanner.nextInt();

        if (opcion == 0) {
            System.out.println("Regresando al menú de combate...");
            return null;
        }

        int itemIndex = opcion - 1;
        return combatSystem.usarItem(itemIndex);
    }
}