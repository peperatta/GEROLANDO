package game;

import characters.Enemigo;
import characters.Gerolando;
import game.world.Biomes;
import items.Item;

import java.util.Scanner;

public class ConsoleGameRunner {
    private GameEngine engine;
    private Scanner scanner;

    public ConsoleGameRunner(GameEngine engine) {
        this.engine = engine;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== BIENVENIDO A GEROLANDO ===");

        while (engine.isRunning()) {
            switch (engine.getCurrentState()) {
                case MENU:
                    handleMenu();
                    break;

                case EXPLORATION:
                    handleExploration();
                    break;

                case COMBAT:
                    System.out.println("No hay combate activo.");
                    engine.finalizarCombate(engine.getJugador().estaVivo());
                    break;

                case GAME_OVER:
                    handleGameOver();
                    break;
            }
        }
    }

    private void handleMenu() {
        System.out.println("\n1. Iniciar partida");
        System.out.println("2. Salir");
        System.out.print("Elige una opción: ");

        int opcion = scanner.nextInt();

        switch (opcion) {
            case 1:
                System.out.println(engine.iniciarPartida());
                break;

            case 2:
                engine.detener();
                System.out.println("Saliendo del juego...");
                break;

            default:
                System.out.println("Opción no válida.");
        }
    }

    private void handleExploration() {
        System.out.println("\n=== EXPLORACIÓN ===");
        System.out.println("Zona actual: " + engine.getBiomeActual().getNombre());
        System.out.println(engine.getBiomeActual().getDescripcion());
        System.out.println("1. Avanzar");
        System.out.println("2. Ver estado");
        System.out.println("3. Ver inventario");
        System.out.println("4. Ir a la tienda");
        System.out.println("5. Cambiar de zona");
        System.out.println("6. Salir");
        System.out.print("Elige una opción: ");

        int opcion = scanner.nextInt();

        switch (opcion) {
            case 1:
                manejarAvance();
                break;

            case 2:
                engine.getJugador().imprimirEstado();
                break;

            case 3:
                manejarInventario();
                break;

            case 4:
                engine.getShop().abrirTienda(engine.getJugador());
                break;

            case 5:
                cambiarZona();
                break;

            case 6:
                engine.detener();
                System.out.println("Saliendo del juego...");
                break;

            default:
                System.out.println("Opción no válida.");
        }
    }

    private void manejarAvance() {
        ExplorationResult result = engine.avanzar();

        System.out.println(result.getMensaje());

        if (result.hayEncuentro()) {
            Enemigo enemigo = result.getEnemigo();

            Combate.iniciarCombate(engine.getJugador(), enemigo);

            engine.finalizarCombate(engine.getJugador().estaVivo());
        }
    }

    private void manejarInventario() {
        Gerolando jugador = engine.getJugador();

        if (jugador.inventario.size() == 0) {
            System.out.println("El inventario está vacío.");
            return;
        }

        System.out.println("\n=== INVENTARIO ===");
        jugador.inventario.mostrarInventario(jugador);
        System.out.println("0. Cancelar");
        System.out.print("Selecciona un item por número para usar/equipar: ");

        int opcion = scanner.nextInt();

        if (opcion == 0) {
            System.out.println("Regresando...");
            return;
        }

        int itemIndex = opcion - 1;

        System.out.println(engine.usarItemInventario(itemIndex));
    }

    private void cambiarZona() {
        System.out.println("\n=== CAMBIAR DE ZONA ===");
        System.out.println("1. Playa");
        System.out.println("2. Bosque");
        System.out.println("3. Cueva");
        System.out.println("0. Cancelar");
        System.out.print("Elige una zona: ");

        int opcion = scanner.nextInt();

        switch (opcion) {
            case 1:
                System.out.println(engine.cambiarZona(Biomes.PLAYA));
                break;

            case 2:
                System.out.println(engine.cambiarZona(Biomes.BOSQUE));
                break;

            case 3:
                System.out.println(engine.cambiarZona(Biomes.CUEVA));
                break;

            case 0:
                System.out.println("Cambio de zona cancelado.");
                break;

            default:
                System.out.println("Opción no válida.");
        }
    }

    private void handleGameOver() {
        System.out.println("\n=== GAME OVER ===");
        System.out.println("Gerolando ha caído en combate.");
        System.out.println("1. Salir");
        System.out.print("Elige una opción: ");

        int opcion = scanner.nextInt();

        if (opcion == 1) {
            engine.detener();
            System.out.println("Fin del juego.");
        } else {
            System.out.println("Opción no válida.");
        }
    }
}