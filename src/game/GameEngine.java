package game;

import characters.Enemigo;
import characters.Gerolando;
import data.factory.ArmaFactory;
import data.factory.ArmaduraFactory;
import data.factory.EnemigoFactory;
import data.factory.PotionFactory;
import game.shop.Shop;
import game.world.Biome;
import game.world.Biomes;
import items.Item;
import game.loot.LootSystem;
import world.map.TileRegistry;

import java.util.Random;

public class GameEngine {
    private GameState currentState;
    private LootSystem lootSystem;
    private Gerolando jugador;
    private EnemigoFactory enemigoFactory;
    private Random random;
    private boolean running;
    private Biome biomeActual;
    private Shop shop;

    public GameEngine(Gerolando jugador,
                      EnemigoFactory enemigoFactory,
                      PotionFactory potionFactory,
                      ArmaFactory armaFactory,
                      ArmaduraFactory armaduraFactory) {
        this.jugador = jugador;
        this.enemigoFactory = enemigoFactory;
        this.random = new Random();
        this.running = true;
        this.currentState = GameState.MENU;
        this.biomeActual = Biomes.PLAYA;
        this.shop = new Shop(potionFactory, armaFactory, armaduraFactory);
        this.lootSystem = new LootSystem(potionFactory, armaFactory, armaduraFactory);
    }

    public String iniciarPartida() {
        currentState = GameState.EXPLORATION;
        return "Comienza la aventura de Gerolando...\nZona inicial: "
                + biomeActual.getNombre() + "\n" + biomeActual.getDescripcion();
    }

    public ExplorationResult avanzar() {
        String mensajeBase = "Gerolando avanza por " + biomeActual.getNombre() + "...";

        boolean hayEncuentro = random.nextInt(100) < biomeActual.getProbabilidadEncuentro();

        if (!hayEncuentro) {
            return ExplorationResult.sinEncuentro(
                    mensajeBase + "\nNo pasó nada. El camino sigue tranquilo."
            );
        }

        Enemigo enemigo = generarEnemigoAleatorio();

        if (enemigo == null) {
            return ExplorationResult.sinEncuentro(
                    mensajeBase + "\nOcurrió un problema al generar el enemigo."
            );
        }

        currentState = GameState.COMBAT;

        return ExplorationResult.conEncuentro(
                mensajeBase + "\n¡Un " + enemigo.nombre + " apareció!",
                enemigo
        );
    }

    public String usarItemInventario(int itemIndex) {
        if (jugador.inventario.size() == 0) {
            return "El inventario está vacío.";
        }

        if (itemIndex < 0 || itemIndex >= jugador.inventario.size()) {
            return "Índice de item no válido.";
        }

        Item item = jugador.inventario.getItems().get(itemIndex);
        jugador.usarItem(item);

        return "Seleccionaste: " + item.getNombre();
    }

    public String cambiarZona(Biome nuevoBiome) {
        if (nuevoBiome == null) {
            return "Zona no válida.";
        }

        this.biomeActual = nuevoBiome;

        return "Ahora estás en: " + biomeActual.getNombre()
                + "\n" + biomeActual.getDescripcion();
    }

    public void finalizarCombate(boolean jugadorVivo) {
        if (jugadorVivo) {
            currentState = GameState.EXPLORATION;
        } else {
            currentState = GameState.GAME_OVER;
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

    public void detener() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public Gerolando getJugador() {
        return jugador;
    }

    public Biome getBiomeActual() {
        return biomeActual;
    }

    public Shop getShop() {
        return shop;
    }

    public LootSystem getLootSystem() {
        return lootSystem;
    }
    public void actualizarBiomePorTile(int tileId) {
        Biome biomeDelTile = TileRegistry.obtenerBiome(tileId);

        if (biomeDelTile != null) {
            this.biomeActual = biomeDelTile;
        }
    }
}
