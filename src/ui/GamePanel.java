package ui;

import characters.Enemigo;
import game.ExplorationResult;
import game.GameEngine;
import game.combat.CombatResult;
import game.combat.CombatSystem;
import game.loot.LootResult;
import items.*;
import ui.battle.BattleMessageQueue;
import ui.battle.BattleOverlayRenderer;
import world.map.MapLoader;
import world.map.TileMap;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {

    // =========================
    // CONSTANTES DE PANTALLA
    // =========================
    private static final int TILE_SIZE = 20;
    private static final int SCREEN_COLS = 16;
    private static final int SCREEN_ROWS = 12;
    private int vidaVisualJugador;
    private int manaVisualJugador;
    private BufferedImage dropSprite;
    private boolean mostrandoDrop = false;
    private static final int INTERNAL_WIDTH = TILE_SIZE * SCREEN_COLS;
    private static final int INTERNAL_HEIGHT = TILE_SIZE * SCREEN_ROWS;
    private static final int SCALE = 2;
    private static final int WIDTH = INTERNAL_WIDTH * SCALE;
    private static final int HEIGHT = INTERNAL_HEIGHT * SCALE;
    private static final long MOVE_COOLDOWN_MS = 170;
    private static final String[] PAUSE_COMMANDS = {"ESTADO", "ITEMS", "HECHIZOS"};
    private static final int PAUSE_ITEMS_VISIBLE = 6;

    // =========================
    // ESTADOS VISUALES
    // =========================
    private enum GameViewState {
        EXPLORATION,
        COMBAT,
        PAUSE
    }
    private enum BattleMenuMode {
        COMMAND,
        INVENTORY,
        STATUS,
        MESSAGE
    }
    private enum PauseMenuMode {
        COMMAND,
        STATUS,
        ITEMS,
        SPELLS
    }

    // =========================
    // MOTOR Y SISTEMAS
    // =========================
    private final GameEngine engine;
    private CombatSystem combatSystem;

    private final BattleOverlayRenderer battleRenderer;
    private final BattleMessageQueue battleMessageQueue;

    // =========================
    // ESTADO DE VISTA
    // =========================
    private GameViewState currentView = GameViewState.EXPLORATION;
    private BattleMenuMode battleMenuMode = BattleMenuMode.COMMAND;
    private PauseMenuMode pauseMenuMode = PauseMenuMode.COMMAND;

    // =========================
    // MAPA Y POSICIÓN
    // =========================
    private TileMap mapa;

    private int playerCol = 2;
    private int playerRow = 2;

    private int screenCol = 0;
    private int screenRow = 0;

    private long lastMoveTime = 0;

    // =========================
    // MENÚ DE COMBATE
    // =========================
    private int selectedCommand = 0;
    private int selectedItem = 0;
    private int inventoryScrollOffset = 0;

    // =========================
    // MENU DE PAUSA
    // =========================
    private int selectedPauseCommand = 0;
    private int selectedPauseItem = 0;
    private int pauseInventoryScrollOffset = 0;
    private String pauseMessage = "";

    // =========================
    // MENSAJES DE COMBATE
    // =========================
    private String battleMessage = "Elige un comando.";
    private String fullBattleMessage = "";
    private String visibleBattleMessage = "";
    private int messageCharIndex = 0;

    private boolean messageFinished = true;
    private boolean arrowVisible = false;

    private boolean combateTerminadoPendiente = false;
    private boolean jugadorGanoPendiente = false;

    // =========================
    // TIMERS
    // =========================
    private Timer messageTimer;
    private Timer arrowBlinkTimer;
    private Timer enemyShakeTimer;
    private Timer screenShakeTimer;

    // =========================
    // EFECTOS VISUALES
    // =========================
    private int enemyShakeOffsetX = 0;
    private int enemyShakeTicks = 0;

    private int screenShakeX = 0;
    private int screenShakeY = 0;
    private int screenShakeTicks = 0;

    // =========================
    // BUFFER
    // =========================
    private BufferedImage screenBuffer;

    // =========================
    // SPRITES / TILES
    // =========================
    private BufferedImage sand;
    private BufferedImage sand2;
    private BufferedImage sand3;
    private BufferedImage wall;
    private BufferedImage water;
    private BufferedImage grass;
    private BufferedImage stone;

    private BufferedImage player;
    private BufferedImage enemySprite;
    private BufferedImage battleBackground;

    // =========================
    // CONSTRUCTOR
    // =========================
    public GamePanel(GameEngine engine) {
        this.engine = engine;
        this.battleRenderer = new BattleOverlayRenderer();
        this.battleMessageQueue = new BattleMessageQueue();
        this.vidaVisualJugador = engine.getJugador().getVidaActual();
        this.manaVisualJugador = engine.getJugador().getManaActual();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        screenBuffer = new BufferedImage(
                INTERNAL_WIDTH,
                INTERNAL_HEIGHT,
                BufferedImage.TYPE_INT_ARGB
        );

        cargarMapa();
        cargarImagenes();

        configurarControles();
        configurarTimersMensaje();
        configurarEnemyShake();
        configurarScreenShake();
    }

    // =========================
    // CARGA DE MAPA E IMÁGENES
    // =========================
    private void cargarMapa() {
        mapa = MapLoader.cargar("/assets/maps/overworld.txt");
    }
    private void cargarImagenes() {
        sand = cargarImagenResource("/assets/tiles/sand.png");
        sand2 = cargarImagenResource("/assets/tiles/sand_stone.png");
        sand3 = cargarImagenResource("/assets/tiles/sand_shell.png");

        wall = cargarImagenResource("/assets/tiles/wall.png");
        water = cargarImagenResource("/assets/tiles/water.png");

        grass = cargarImagenResource("/assets/tiles/grass.png");
        stone = cargarImagenResource("/assets/tiles/stone.png");

        player = cargarImagenResource("/assets/sprites/player/gerolando.png");
    }
    private BufferedImage cargarImagenResource(String ruta) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(ruta));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("No se pudo cargar imagen: " + ruta);
            return null;
        }
    }
    private BufferedImage cargarImagenDesdePath(String ruta) {
        try {
            return ImageIO.read(new File(ruta));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("No se pudo cargar imagen desde path: " + ruta);
            return null;
        }
    }
    private BufferedImage cargarFondoCombate() {
        String biomeId = engine.getBiomeActual().getId();

        switch (biomeId) {
            case "playa":
                return cargarImagenResource("/assets/tiles/backgrounds/playa.png");

            case "bosque":
                return cargarImagenResource("/assets/tiles/backgrounds/bosque.png");

            case "cueva":
                return cargarImagenResource("/assets/tiles/backgrounds/cueva.png");

            default:
                return null;
        }
    }

    // =========================
    // CONTROLES
    // =========================
    private void configurarControles() {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("UP"), "up");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "down");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "left");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "space");
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "enter");
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");

        actionMap.put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarArriba();
            }
        });

        actionMap.put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarAbajo();
            }
        });

        actionMap.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentView == GameViewState.EXPLORATION) {
                    moverJugador(-1, 0);
                }
            }
        });

        actionMap.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentView == GameViewState.EXPLORATION) {
                    moverJugador(1, 0);
                }
            }
        });

        actionMap.put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarAccion();
            }
        });

        actionMap.put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarEnter();
            }
        });

        actionMap.put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarAccion();
            }
        });
    }
    private void manejarEnter() {
        if (currentView == GameViewState.EXPLORATION) {
            abrirPausa();
            return;
        }

        confirmarAccion();
    }
    private void manejarArriba() {
        if (currentView == GameViewState.PAUSE) {
            manejarArribaPausa();
            return;
        }

        if (currentView == GameViewState.EXPLORATION) {
            moverJugador(0, -1);
            return;
        }

        if (battleMenuMode == BattleMenuMode.COMMAND) {
            selectedCommand--;

            if (selectedCommand < 0) {
                selectedCommand = 2;
            }
        } else if (battleMenuMode == BattleMenuMode.INVENTORY) {
            moverSeleccionInventario(-1);
        }

        repaint();
    }
    private void manejarAbajo() {
        if (currentView == GameViewState.PAUSE) {
            manejarAbajoPausa();
            return;
        }

        if (currentView == GameViewState.EXPLORATION) {
            moverJugador(0, 1);
            return;
        }

        if (battleMenuMode == BattleMenuMode.COMMAND) {
            selectedCommand++;

            if (selectedCommand > 2) {
                selectedCommand = 0;
            }
        } else if (battleMenuMode == BattleMenuMode.INVENTORY) {
            moverSeleccionInventario(1);
        }

        repaint();
    }
    private void confirmarAccion() {
        if (currentView == GameViewState.PAUSE) {
            confirmarAccionPausa();
            return;
        }

        if (currentView != GameViewState.COMBAT) return;

        if (battleMenuMode == BattleMenuMode.MESSAGE) {
            avanzarMensajeCombate();
            return;
        }

        if (battleMenuMode == BattleMenuMode.COMMAND) {
            confirmarComando();
            return;
        }

        if (battleMenuMode == BattleMenuMode.INVENTORY) {
            usarItemSeleccionado();
        }
    }
    private void cancelarAccion() {
        if (currentView == GameViewState.PAUSE) {
            cancelarPausa();
            return;
        }

        if (currentView != GameViewState.COMBAT) return;

        if (battleMenuMode == BattleMenuMode.MESSAGE) {
            return;
        }

        battleMenuMode = BattleMenuMode.COMMAND;
        battleMessage = "Elige un comando.";

        repaint();
    }

    // =========================
    // PAUSA
    // =========================
    private void abrirPausa() {
        currentView = GameViewState.PAUSE;
        pauseMenuMode = PauseMenuMode.COMMAND;
        selectedPauseCommand = 0;
        selectedPauseItem = 0;
        pauseInventoryScrollOffset = 0;
        pauseMessage = "";

        repaint();
    }
    private void cerrarPausa() {
        currentView = GameViewState.EXPLORATION;
        pauseMenuMode = PauseMenuMode.COMMAND;
        pauseMessage = "";

        repaint();
    }
    private void manejarArribaPausa() {
        if (pauseMenuMode == PauseMenuMode.COMMAND) {
            selectedPauseCommand--;

            if (selectedPauseCommand < 0) {
                selectedPauseCommand = 2;
            }
        } else if (pauseMenuMode == PauseMenuMode.ITEMS) {
            moverSeleccionItemsPausa(-1);
        }

        repaint();
    }
    private void manejarAbajoPausa() {
        if (pauseMenuMode == PauseMenuMode.COMMAND) {
            selectedPauseCommand++;

            if (selectedPauseCommand > 2) {
                selectedPauseCommand = 0;
            }
        } else if (pauseMenuMode == PauseMenuMode.ITEMS) {
            moverSeleccionItemsPausa(1);
        }

        repaint();
    }
    private void confirmarAccionPausa() {
        if (pauseMenuMode == PauseMenuMode.COMMAND) {
            confirmarComandoPausa();
            return;
        }

        if (pauseMenuMode == PauseMenuMode.ITEMS) {
            usarItemPausa();
        }
    }
    private void confirmarComandoPausa() {
        switch (selectedPauseCommand) {
            case 0:
                pauseMenuMode = PauseMenuMode.STATUS;
                pauseMessage = "";
                break;

            case 1:
                pauseMenuMode = PauseMenuMode.ITEMS;
                selectedPauseItem = 0;
                pauseInventoryScrollOffset = 0;
                pauseMessage = "";
                break;

            case 2:
                pauseMenuMode = PauseMenuMode.SPELLS;
                pauseMessage = "Aun no hay hechizos.";
                break;
        }

        repaint();
    }
    private void cancelarPausa() {
        if (pauseMenuMode == PauseMenuMode.COMMAND) {
            cerrarPausa();
            return;
        }

        pauseMenuMode = PauseMenuMode.COMMAND;
        pauseMessage = "";

        repaint();
    }
    private void moverSeleccionItemsPausa(int direccion) {
        int totalItems = getItemsOrdenados().size();

        if (totalItems == 0) {
            selectedPauseItem = 0;
            pauseInventoryScrollOffset = 0;
            return;
        }

        selectedPauseItem += direccion;

        if (selectedPauseItem < 0) {
            selectedPauseItem = totalItems - 1;
        } else if (selectedPauseItem >= totalItems) {
            selectedPauseItem = 0;
        }

        int maxVisible = PAUSE_ITEMS_VISIBLE;

        if (selectedPauseItem < pauseInventoryScrollOffset) {
            pauseInventoryScrollOffset = selectedPauseItem;
        } else if (selectedPauseItem >= pauseInventoryScrollOffset + maxVisible) {
            pauseInventoryScrollOffset = selectedPauseItem - maxVisible + 1;
        }
    }
    private void usarItemPausa() {
        int indiceReal = convertirIndiceVisualAReal(selectedPauseItem);

        if (indiceReal == -1) {
            pauseMessage = "No hay item seleccionado.";
            repaint();
            return;
        }

        Item item = engine.getJugador().inventario.getItems().get(indiceReal);
        engine.getJugador().usarItem(item);

        if (item instanceof Consumible) {
            vidaVisualJugador = engine.getJugador().getVidaActual();
            manaVisualJugador = engine.getJugador().getManaActual();
            pauseMessage = "Usaste " + item.getNombre() + ".";
        } else if (item instanceof Arma || item instanceof Armadura) {
            pauseMessage = "Equipaste " + item.getNombre() + ".";
        } else {
            pauseMessage = "Usaste " + item.getNombre() + ".";
        }

        int totalItems = getItemsOrdenados().size();
        if (selectedPauseItem >= totalItems) {
            selectedPauseItem = Math.max(0, totalItems - 1);
        }
        if (pauseInventoryScrollOffset > selectedPauseItem) {
            pauseInventoryScrollOffset = selectedPauseItem;
        }

        repaint();
    }

    // =========================
    // EXPLORACIÓN
    // =========================
    private void moverJugador(int colDelta, int rowDelta) {
        if (currentView != GameViewState.EXPLORATION) return;

        long now = System.currentTimeMillis();

        if (now - lastMoveTime < MOVE_COOLDOWN_MS) {
            return;
        }

        lastMoveTime = now;

        int nuevaCol = playerCol + colDelta;
        int nuevaRow = playerRow + rowDelta;

        if (!mapa.esCaminable(nuevaRow, nuevaCol)) {
            return;
        }

        playerCol = nuevaCol;
        playerRow = nuevaRow;

        int tileActual = mapa.getTile(playerRow, playerCol);
        engine.actualizarBiomePorTile(tileActual);

        actualizarPantallaActual();

        ExplorationResult result = engine.avanzar();

        if (result.hayEncuentro()) {
            iniciarCombate(result);
        }

        repaint();
    }
    private void actualizarPantallaActual() {
        screenCol = playerCol / SCREEN_COLS;
        screenRow = playerRow / SCREEN_ROWS;
    }

    // =========================
    // COMBATE
    // =========================
    private void iniciarCombate(ExplorationResult result) {
        currentView = GameViewState.COMBAT;
        battleMenuMode = BattleMenuMode.COMMAND;

        selectedCommand = 0;
        selectedItem = 0;
        inventoryScrollOffset = 0;

        Enemigo enemigo = result.getEnemigo();

        combatSystem = new CombatSystem(engine.getJugador(), enemigo);
        vidaVisualJugador = engine.getJugador().getVidaActual();
        manaVisualJugador = engine.getJugador().getManaActual();
        enemySprite = cargarImagenDesdePath(enemigo.spritePath);
        battleBackground = cargarFondoCombate();

        mostrarMensajesDeCombate(combatSystem.iniciarCombate());
    }
    private void confirmarComando() {
        switch (selectedCommand) {
            case 0:
                atacar();
                break;

            case 1:
                abrirInventarioCombate();
                break;

            case 2:
                battleMenuMode = BattleMenuMode.STATUS;
                battleMessage = "";
                repaint();
                break;
        }
    }
    private void abrirInventarioCombate() {
        battleMenuMode = BattleMenuMode.INVENTORY;
        selectedItem = 0;
        inventoryScrollOffset = 0;
        battleMessage = "Selecciona un item.";

        repaint();
    }
    private void atacar() {
        if (combatSystem == null) return;

        CombatResult result = combatSystem.atacar();

        iniciarEnemyShake();

        if (result.isCombateTerminado()) {
            marcarCombateTerminado(result.isJugadorGano());
        }

        mostrarMensajesDeCombate(result.getMensaje());
    }
    private void usarItemSeleccionado() {
        if (combatSystem == null) return;

        int indiceReal = convertirIndiceVisualAReal(selectedItem);

        if (indiceReal == -1) {
            mostrarMensajesDeCombate("Error al seleccionar item.");
            return;
        }

        CombatResult result = combatSystem.usarItem(indiceReal);
        vidaVisualJugador = engine.getJugador().getVidaActual();
        manaVisualJugador = engine.getJugador().getManaActual();

        if (result.isCombateTerminado()) {
            marcarCombateTerminado(result.isJugadorGano());
        }

        mostrarMensajesDeCombate(result.getMensaje());
    }
    private void marcarCombateTerminado(boolean jugadorGano) {
        combateTerminadoPendiente = true;
        jugadorGanoPendiente = jugadorGano;
    }
    private void finalizarCombatePendiente() {
        if (jugadorGanoPendiente) {
            LootResult lootResult = engine.getLootSystem()
                    .procesarRecompensas(engine.getJugador(), combatSystem.getEnemigo());

            if (lootResult.tieneDrop()) {
                mostrarDropEnCombate(lootResult);
                return;
            }

            if (!lootResult.getMensaje().isEmpty()) {
                mostrarMensajesDeCombate(lootResult.getMensaje() + "\nSpace para continuar.");
                combateTerminadoPendiente = true;
                jugadorGanoPendiente = false;
                return;
            }
        }

        cerrarCombateYVolverAlMapa();
    }
    private void mostrarDropEnCombate(LootResult lootResult) {
        Item item = lootResult.getItemDropeado();

        enemySprite = null;
        mostrandoDrop = true;
        String spritePath = obtenerSpritePathItem(item);

        if (spritePath != null) {
            dropSprite = cargarImagenDesdePath(spritePath);
        } else {
            dropSprite = null;
        }

        String mensaje = lootResult.getMensaje();

        if (mensaje == null || mensaje.isEmpty()) {
            mensaje = "Encontraste " + item.getNombre() + ".";
        }

        mostrarMensajesDeCombate(mensaje + "\nSpace para continuar.");

        combateTerminadoPendiente = true;
        jugadorGanoPendiente = false;
    }
    private void cerrarCombateYVolverAlMapa() {
        engine.finalizarCombate(engine.getJugador().estaVivo());

        currentView = GameViewState.EXPLORATION;
        battleMenuMode = BattleMenuMode.COMMAND;

        combatSystem = null;
        enemySprite = null;
        battleBackground = null;
        dropSprite = null;
        mostrandoDrop = false;

        combateTerminadoPendiente = false;
        jugadorGanoPendiente = false;

        battleMessageQueue.limpiar();
        battleMessage = "Elige un comando.";

        repaint();
    }

    // =========================
    // INVENTARIO VISUAL
    // =========================
    private void moverSeleccionInventario(int direccion) {
        int totalItems = getItemsOrdenados().size();

        if (totalItems == 0) {
            selectedItem = 0;
            inventoryScrollOffset = 0;
            repaint();
            return;
        }

        selectedItem += direccion;

        if (selectedItem < 0) {
            selectedItem = totalItems - 1;
        } else if (selectedItem >= totalItems) {
            selectedItem = 0;
        }

        int maxVisible = 3;

        if (selectedItem < inventoryScrollOffset) {
            inventoryScrollOffset = selectedItem;
        } else if (selectedItem >= inventoryScrollOffset + maxVisible) {
            inventoryScrollOffset = selectedItem - maxVisible + 1;
        }

        repaint();
    }
    private List<Item> getItemsOrdenados() {
        List<Item> originales = engine.getJugador().inventario.getItems();

        List<Item> equipados = new ArrayList<>();
        List<Item> noEquipados = new ArrayList<>();

        for (Item item : originales) {
            if (engine.getJugador().estaEquipado(item)) {
                equipados.add(item);
            } else {
                noEquipados.add(item);
            }
        }

        List<Item> resultado = new ArrayList<>();
        resultado.addAll(equipados);
        resultado.addAll(noEquipados);

        return resultado;
    }
    private int convertirIndiceVisualAReal(int indiceVisual) {
        List<Item> ordenados = getItemsOrdenados();
        List<Item> originales = engine.getJugador().inventario.getItems();

        if (indiceVisual < 0 || indiceVisual >= ordenados.size()) {
            return -1;
        }

        Item seleccionado = ordenados.get(indiceVisual);

        return originales.indexOf(seleccionado);
    }

    // =========================
    // MENSAJES DE COMBATE
    // =========================
    private void configurarTimersMensaje() {
        messageTimer = new Timer(35, e -> avanzarLetraMensaje());

        arrowBlinkTimer = new Timer(450, e -> {
            if (messageFinished && battleMenuMode == BattleMenuMode.MESSAGE) {
                arrowVisible = !arrowVisible;
                repaint();
            }
        });

        arrowBlinkTimer.start();
    }
    private void mostrarMensajesDeCombate(String texto) {
        battleMessageQueue.limpiar();
        battleMessageQueue.agregarLineas(texto);

        if (!battleMessageQueue.estaVacia()) {
            battleMenuMode = BattleMenuMode.MESSAGE;
            iniciarTextoTypewriter(battleMessageQueue.siguiente());
        } else {
            battleMessage = "Elige un comando.";
            battleMenuMode = BattleMenuMode.COMMAND;
        }

        repaint();
    }
    private void avanzarMensajeCombate() {
        if (!messageFinished) {
            completarMensajeActual();
            return;
        }

        if (!battleMessageQueue.estaVacia()) {
            iniciarTextoTypewriter(battleMessageQueue.siguiente());
            return;
        }

        if (!combatSystem.esTurnoJugador()) {

            CombatResult resultadoEnemigo = combatSystem.turnoEnemigoSeparado();

            if (resultadoEnemigo.isCombateTerminado()) {
                marcarCombateTerminado(resultadoEnemigo.isJugadorGano());
            }

            mostrarMensajesDeCombate(resultadoEnemigo.getMensaje());
            return;
        }

        if (combateTerminadoPendiente) {
            if (mostrandoDrop || !jugadorGanoPendiente) {
                cerrarCombateYVolverAlMapa();
            } else {
                finalizarCombatePendiente();
            }
            return;
        }

        battleMenuMode = BattleMenuMode.COMMAND;
        battleMessage = "";
        visibleBattleMessage = "";
        fullBattleMessage = "";
        messageFinished = true;
        arrowVisible = false;

        repaint();
    }
    private void iniciarTextoTypewriter(String mensaje) {
        fullBattleMessage = mensaje == null ? "" : mensaje;
        visibleBattleMessage = "";
        messageCharIndex = 0;
        messageFinished = false;
        arrowVisible = false;

        battleMessage = visibleBattleMessage;

        if (fullBattleMessage.startsWith("Gerolando recibió")) {
            vidaVisualJugador = engine.getJugador().getVidaActual();
            manaVisualJugador = engine.getJugador().getManaActual();
            iniciarScreenShake();
        }

        if (messageTimer.isRunning()) {
            messageTimer.stop();
        }

        messageTimer.start();

        repaint();
    }
    private void avanzarLetraMensaje() {
        if (messageCharIndex < fullBattleMessage.length()) {
            visibleBattleMessage += fullBattleMessage.charAt(messageCharIndex);
            messageCharIndex++;

            battleMessage = visibleBattleMessage;

            repaint();
            return;
        }

        messageTimer.stop();

        messageFinished = true;
        arrowVisible = true;

        repaint();
    }
    private void completarMensajeActual() {
        if (messageFinished) return;

        messageTimer.stop();

        visibleBattleMessage = fullBattleMessage;
        battleMessage = visibleBattleMessage;
        messageCharIndex = fullBattleMessage.length();
        messageFinished = true;
        arrowVisible = true;

        repaint();
    }

    // =========================
    // EFECTOS VISUALES
    // =========================
    private void configurarEnemyShake() {
        enemyShakeTimer = new Timer(40, e -> {
            if (enemyShakeTicks <= 0) {
                enemyShakeOffsetX = 0;
                enemyShakeTimer.stop();
                repaint();
                return;
            }

            enemyShakeOffsetX = (enemyShakeTicks % 2 == 0) ? 3 : -3;
            enemyShakeTicks--;

            repaint();
        });
    }
    private void iniciarEnemyShake() {
        enemyShakeTicks = 8;
        enemyShakeOffsetX = 3;

        if (enemyShakeTimer.isRunning()) {
            enemyShakeTimer.stop();
        }

        enemyShakeTimer.start();
    }
    private void configurarScreenShake() {
        screenShakeTimer = new Timer(30, e -> {
            if (screenShakeTicks <= 0) {
                screenShakeX = 0;
                screenShakeY = 0;
                screenShakeTimer.stop();
                repaint();
                return;
            }

            screenShakeX = (int) (Math.random() * 6) - 3;
            screenShakeY = (int) (Math.random() * 6) - 3;

            screenShakeTicks--;

            repaint();
        });
    }
    private void iniciarScreenShake() {
        screenShakeTicks = 10;

        if (screenShakeTimer.isRunning()) {
            screenShakeTimer.stop();
        }

        screenShakeTimer.start();
    }

    // =========================
    // RENDER PRINCIPAL
    // =========================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D bufferGraphics = screenBuffer.createGraphics();

        bufferGraphics.translate(screenShakeX, screenShakeY);

        bufferGraphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.fillRect(0, 0, INTERNAL_WIDTH, INTERNAL_HEIGHT);

        dibujarMapa(bufferGraphics);
        dibujarJugador(bufferGraphics);

        if (currentView == GameViewState.COMBAT && combatSystem != null) {
            dibujarCombate(bufferGraphics);
        }

        if (currentView == GameViewState.PAUSE) {
            dibujarMenuPausa(bufferGraphics);
        } else {
            dibujarDebug(bufferGraphics);
        }

        bufferGraphics.dispose();

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        g2.drawImage(screenBuffer, 0, 0, WIDTH, HEIGHT, null);
    }
    private void dibujarCombate(Graphics2D g2) {
        battleRenderer.draw(
                g2,
                INTERNAL_WIDTH,
                INTERNAL_HEIGHT,
                combatSystem,
                enemySprite,
                battleBackground,
                selectedCommand,
                selectedItem,
                inventoryScrollOffset,
                battleMenuMode.name(),
                battleMessage,
                arrowVisible,
                enemyShakeOffsetX,
                vidaVisualJugador,
                manaVisualJugador,
                dropSprite,
                mostrandoDrop
        );
    }
    private void dibujarMenuPausa(Graphics2D g2) {
        int commandX = 8;
        int commandY = 8;
        int commandW = 112;
        int commandH = 82;

        dibujarPanelPausa(g2, commandX, commandY, commandW, commandH);

        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.setColor(Color.WHITE);
        g2.drawString("-COMMAND-", commandX + 26, commandY + 11);

        dibujarComandosPausa(g2, commandX + 10, commandY + 30);

        if (pauseMenuMode != PauseMenuMode.COMMAND) {
            int contentX = 18;
            int contentY = 96;
            int contentW = 284;
            int contentH = 128;

            dibujarPanelPausa(g2, contentX, contentY, contentW, contentH);
            dibujarContenidoPausa(g2, contentX + 14, contentY + 22, contentW - 28, contentH - 32);
        }
    }
    private void dibujarComandosPausa(Graphics2D g2, int x, int y) {
        for (int i = 0; i < PAUSE_COMMANDS.length; i++) {
            int textY = y + (i * 18);

            if (pauseMenuMode == PauseMenuMode.COMMAND && selectedPauseCommand == i) {
                g2.drawString(">", x, textY);
            }

            g2.drawString(PAUSE_COMMANDS[i], x + 14, textY);
        }
    }
    private void dibujarContenidoPausa(Graphics2D g2, int x, int y, int w, int h) {
        switch (pauseMenuMode) {
            case STATUS:
                dibujarEstadoPausa(g2, x, y);
                break;

            case ITEMS:
                dibujarItemsPausa(g2, x, y);
                break;

            case SPELLS:
                dibujarHechizosPausa(g2, x, y);
                break;

            case COMMAND:
            default:
                g2.drawString("Elige una", x, y);
                g2.drawString("opcion.", x, y + 16);
                break;
        }
    }
    private void dibujarEstadoPausa(Graphics2D g2, int x, int y) {
        var jugador = engine.getJugador();

        g2.drawString("-STATUS-", x + 92, y - 10);
        g2.drawString("LV " + jugador.getNivel(), x, y + 8);
        g2.drawString("XP " + jugador.getXP(), x + 126, y + 8);
        g2.drawString("HP " + jugador.getVidaActual() + "/" + jugador.getVidaMax(), x, y + 27);
        g2.drawString("MP " + jugador.getManaActual() + "/" + jugador.getManaMax(), x + 126, y + 27);
        g2.drawString("ATK " + jugador.getAtaque(), x, y + 46);
        g2.drawString("DEF " + jugador.getDefensa(), x + 126, y + 46);
        g2.drawString("VEL " + jugador.getVelocidad(), x, y + 65);
        g2.drawString("GOLD " + jugador.getOro(), x + 126, y + 65);

    }
    private void dibujarItemsPausa(Graphics2D g2, int x, int y) {
        List<Item> itemsOrdenados = getItemsOrdenados();

        g2.drawString("-ITEM-", x + 104, y - 10);

        if (itemsOrdenados.isEmpty()) {
            g2.drawString("Inventario vacio.", x, y + 18);
            return;
        }

        int maxVisible = PAUSE_ITEMS_VISIBLE;

        for (int i = 0; i < maxVisible; i++) {
            int itemIndex = pauseInventoryScrollOffset + i;

            if (itemIndex >= itemsOrdenados.size()) break;

            Item item = itemsOrdenados.get(itemIndex);
            int textY = y + 8 + (i * 15);

            if (itemIndex == selectedPauseItem) {
                g2.drawString(">", x, textY);
            }

            g2.drawString(construirLineaItemPausa(item), x + 12, textY);
        }

        if (!pauseMessage.isEmpty()) {
            g2.drawString(limitarTextoPausa(pauseMessage, 34), x, y + 98);
        }
    }
    private void dibujarHechizosPausa(Graphics2D g2, int x, int y) {
        g2.drawString("-SPELL-", x + 100, y - 10);
        g2.drawString("Aun no hay hechizos.", x, y + 22);
    }
    private String construirLineaItemPausa(Item item) {
        String linea = item.getNombre();

        if (engine.getJugador().estaEquipado(item)) {
            linea += " [EQ]";
        }

        if (item instanceof Arma) {
            linea += " +" + ((Arma) item).getAtaque();
        } else if (item instanceof Armadura) {
            linea += " +" + ((Armadura) item).getDefensa();
        } else if (item instanceof Pocion) {
            linea += " +" + ((Pocion) item).getValor();
        }

        return limitarTextoPausa(linea, 36);
    }
    private String limitarTextoPausa(String texto, int maxLength) {
        if (texto == null) return "";

        if (texto.length() <= maxLength) {
            return texto;
        }

        return texto.substring(0, maxLength - 3) + "...";
    }
    private void dibujarPanelPausa(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(Color.BLACK);
        g2.fillRect(x, y, w, h);

        g2.setColor(Color.WHITE);
        g2.drawRect(x, y, w, h);
        g2.drawRect(x + 2, y + 2, w - 4, h - 4);
    }
    private String obtenerSpritePathItem(Item item) {
        if (item instanceof Arma) {
            return ((Arma) item).spritePath;
        }

        if (item instanceof Armadura) {
            return ((Armadura) item).spritePath;
        }

        if (item instanceof Pocion) {
            return ((Consumible) item).getSpritePath();
        }

        return null;
    }
    private void dibujarMapa(Graphics2D g2) {
        int offsetCol = screenCol * SCREEN_COLS;
        int offsetRow = screenRow * SCREEN_ROWS;

        for (int row = 0; row < SCREEN_ROWS; row++) {
            for (int col = 0; col < SCREEN_COLS; col++) {
                int mapRow = offsetRow + row;
                int mapCol = offsetCol + col;

                if (!mapa.estaDentro(mapRow, mapCol)) {
                    continue;
                }

                int tileId = mapa.getTile(mapRow, mapCol);

                dibujarTile(g2, tileId, col, row);
            }
        }
    }
    private void dibujarTile(Graphics2D g2, int tileId, int screenCol, int screenRow) {
        BufferedImage img = obtenerImagenTile(tileId);

        int x = screenCol * TILE_SIZE;
        int y = screenRow * TILE_SIZE;

        if (img != null) {
            g2.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
            return;
        }

        g2.setColor(obtenerColorFallback(tileId));
        g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
    }
    private void dibujarJugador(Graphics2D g2) {
        int localCol = playerCol - (screenCol * SCREEN_COLS);
        int localRow = playerRow - (screenRow * SCREEN_ROWS);

        int x = localCol * TILE_SIZE;
        int y = localRow * TILE_SIZE;

        if (player != null) {
            g2.drawImage(player, x, y, TILE_SIZE, TILE_SIZE, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        }
    }
    private void dibujarDebug(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.drawString("Pantalla: " + screenCol + "," + screenRow, 8, 14);
        g2.drawString("Pos: " + playerCol + "," + playerRow, 8, 28);
    }

    // =========================
    // TILE HELPERS
    // =========================
    private BufferedImage obtenerImagenTile(int tileId) {
        switch (tileId) {
            case 1:
                return wall;

            case 2:
                return water;

            case 0:
                return sand;

            case 3:
                return sand2;

            case 4:
                return sand3;

            case 10:
                return grass;

            case 20:
                return stone;

            default:
                return grass;
        }
    }
    private Color obtenerColorFallback(int tileId) {
        switch (tileId) {
            case 1:
                return Color.DARK_GRAY;

            case 2:
                return Color.BLUE;

            case 3:
                return Color.GREEN;

            case 4:
                return Color.GRAY;

            case 0:
            default:
                return new Color(50, 150, 70);
        }
    }
}
