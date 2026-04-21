package game;

import characters.Enemigo;
import characters.Gerolando;
import data.factory.EnemigoFactory;
import game.world.Biome;
import game.world.Biomes;
import items.Item;

import java.util.Random;
import java.util.Scanner;

public class GameEngine {
    private GameState currentState;
    private Gerolando jugador;
    private EnemigoFactory enemigoFactory;
    private Scanner scanner;
    private Random random;
    private boolean running;
    private Biome biomeActual;

    public GameEngine(Gerolando jugador, EnemigoFactory enemigoFactory) {
        this.jugador = jugador;
        this.enemigoFactory = enemigoFactory;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.running = true;
        this.currentState = GameState.MENU;
        this.biomeActual = Biomes.PLAYA; // zona inicial
    }

    public void start() {
        System.out.println("=== BIENVENIDO A GEROLANDO ===");

        while (running) {
            switch (currentState) {
                case MENU:
                    handleMenu();
                    break;
                case EXPLORATION:
                    handleExploration();
                    break;
                case COMBAT:
                    currentState = GameState.EXPLORATION;
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
                currentState = GameState.EXPLORATION;
                System.out.println("\nComienza la aventura de Gerolando...");
                System.out.println("Zona inicial: " + biomeActual.getNombre());
                System.out.println(biomeActual.getDescripcion());
                break;
            case 2:
                running = false;
                System.out.println("Saliendo del juego...");
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private void handleExploration() {
        System.out.println("\n=== EXPLORACIÓN ===");
        System.out.println("Zona actual: " + biomeActual.getNombre());
        System.out.println(biomeActual.getDescripcion());
        System.out.println("1. Avanzar");
        System.out.println("2. Ver estado");
        System.out.println("3. Ver inventario");
        System.out.println("4. Cambiar de zona");
        System.out.println("5. Salir");
        System.out.print("Elige una opción: ");

        int opcion = scanner.nextInt();

        switch (opcion) {
            case 1:
                avanzar();
                break;
            case 2:
                jugador.imprimirEstado();
                break;
            case 3:
                manejarInventario();
                break;
            case 4:
                cambiarZona();
                break;
            case 5:
                running = false;
                System.out.println("Saliendo del juego...");
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private void avanzar() {
        System.out.println("\nGerolando avanza por " + biomeActual.getNombre() + "...");

        boolean hayEncuentro = random.nextInt(100) < biomeActual.getProbabilidadEncuentro();

        if (hayEncuentro) {
            Enemigo enemigo = generarEnemigoAleatorio();

            if (enemigo == null) {
                System.out.println("Ocurrió un problema al generar el enemigo.");
                return;
            }

            System.out.println("¡Un " + enemigo.nombre + " apareció!");

            currentState = GameState.COMBAT;
            Combate.iniciarCombate(jugador, enemigo);

            if (jugador.estaVivo()) {
                currentState = GameState.EXPLORATION;
            } else {
                currentState = GameState.GAME_OVER;
            }
        } else {
            System.out.println("No pasó nada. El camino sigue tranquilo.");
        }
    }

    private Enemigo generarEnemigoAleatorio() {
        String[] enemigosDisponibles = biomeActual.getEnemigosDisponibles();

        if (enemigosDisponibles == null || enemigosDisponibles.length == 0) {
            return null;
        }

        String idElegido = enemigosDisponibles[random.nextInt(enemigosDisponibles.length)];
        return enemigoFactory.crear(idElegido);
    }

    private void manejarInventario() {
        if (jugador.inventario.size() == 0) {
            System.out.println("El inventario está vacío.");
            return;
        }

        System.out.println("\n=== INVENTARIO ===");
        jugador.inventario.mostrarInventario();
        System.out.println("0. Cancelar");
        System.out.print("Selecciona un item por número para usar/equipar: ");

        int opcion = scanner.nextInt();

        if (opcion == 0) {
            System.out.println("Regresando...");
            return;
        }

        int itemIndex = opcion - 1;

        if (itemIndex >= 0 && itemIndex < jugador.inventario.size()) {
            Item item = jugador.inventario.getItems().get(itemIndex);
            jugador.usarItem(item);
        } else {
            System.out.println("Índice de item no válido.");
        }
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
                biomeActual = Biomes.PLAYA;
                break;
            case 2:
                biomeActual = Biomes.BOSQUE;
                break;
            case 3:
                biomeActual = Biomes.CUEVA;
                break;
            case 0:
                System.out.println("Cambio de zona cancelado.");
                return;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        System.out.println("Ahora estás en: " + biomeActual.getNombre());
        System.out.println(biomeActual.getDescripcion());
    }

    private void handleGameOver() {
        System.out.println("\n=== GAME OVER ===");
        System.out.println("Gerolando ha caído en combate.");
        System.out.println("1. Salir");
        System.out.print("Elige una opción: ");

        int opcion = scanner.nextInt();

        if (opcion == 1) {
            running = false;
            System.out.println("Fin del juego.");
        } else {
            System.out.println("Opción no válida.");
        }
    }
}