package game;

import characters.Enemigo;
import characters.Gerolando;
import data.factory.ArmaFactory;
import data.factory.ArmaduraFactory;
import data.factory.PotionFactory;
import items.Item;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Combate {
    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();

    private static PotionFactory potionFactory;
    private static ArmaFactory armaFactory;
    private static ArmaduraFactory armaduraFactory;

    public static void configurarDrops(PotionFactory potionFactoryParam,
                                       ArmaFactory armaFactoryParam,
                                       ArmaduraFactory armaduraFactoryParam) {
        potionFactory = potionFactoryParam;
        armaFactory = armaFactoryParam;
        armaduraFactory = armaduraFactoryParam;
    }

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
            int oroGanado = enemigo.getVida() / 3;

            jugador.ganarXP(xpGanada);
            jugador.ganarOro(oroGanado);

            procesarDrop(enemigo, jugador);
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
            return true;
        } else {
            System.out.println("Índice de item no válido.");
            return false;
        }
    }

    private static void turnoEnemigo(Enemigo enemigo, Gerolando jugador) {
        System.out.println("Turno de " + enemigo.nombre);
        enemigo.atacar(jugador);
    }

    private static void procesarDrop(Enemigo enemigo, Gerolando jugador) {
        List<String> drops = enemigo.getDrops();

        if (drops == null || drops.isEmpty()) {
            return;
        }

        int roll = random.nextInt(100) + 1;

        if (roll > enemigo.getDropChance()) {
            return;
        }

        String dropId = drops.get(random.nextInt(drops.size()));
        Item drop = crearItemDesdeId(dropId);

        if (drop == null) {
            System.out.println("No se pudo generar el drop: " + dropId);
            return;
        }

        System.out.println("¡El enemigo soltó: " + drop.getNombre() + "!");

        if (!jugador.inventario.estaLleno()) {
            jugador.inventario.agregar(drop);
            System.out.println(drop.getNombre() + " fue agregado al inventario.");
            return;
        }

        System.out.println("Pero tu inventario está lleno.");
        manejarDropConInventarioLleno(jugador, drop);
    }

    private static void manejarDropConInventarioLleno(Gerolando jugador, Item nuevoItem) {
        System.out.println("\n=== INVENTARIO LLENO ===");
        jugador.inventario.mostrarInventario();
        System.out.println("0. No recoger el objeto");
        System.out.print("Selecciona el objeto que quieres reemplazar: ");

        int opcion = scanner.nextInt();

        if (opcion == 0) {
            System.out.println("Dejaste " + nuevoItem.getNombre() + " en el suelo.");
            return;
        }

        int index = opcion - 1;

        if (index < 0 || index >= jugador.inventario.size()) {
            System.out.println("Índice no válido. No recogiste el objeto.");
            return;
        }

        Item itemAnterior = jugador.inventario.getItem(index);

        if (jugador.estaEquipado(itemAnterior)) {
            System.out.println("No puedes reemplazar un objeto que está equipado.");
            System.out.println("No recogiste " + nuevoItem.getNombre() + ".");
            return;
        }

        boolean reemplazado = jugador.inventario.reemplazarItem(index, nuevoItem);

        if (!reemplazado) {
            System.out.println("No se pudo reemplazar el objeto.");
            return;
        }

        System.out.println("Reemplazaste " + itemAnterior.getNombre() + " por " + nuevoItem.getNombre() + ".");
    }

    private static Item crearItemDesdeId(String id) {
        if (potionFactory != null) {
            try {
                Item item = potionFactory.crear(id);
                if (item != null) return item;
            } catch (Exception ignored) {
            }
        }

        if (armaFactory != null) {
            try {
                Item item = armaFactory.crear(id);
                if (item != null) return item;
            } catch (Exception ignored) {
            }
        }

        if (armaduraFactory != null) {
            try {
                Item item = armaduraFactory.crear(id);
                if (item != null) return item;
            } catch (Exception ignored) {
            }
        }

        return null;
    }
}