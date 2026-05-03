package ui;

import characters.Enemigo;
import game.ExplorationResult;
import game.GameEngine;
import game.combat.CombatResult;
import game.combat.CombatSystem;
import game.loot.LootResult;
import items.Item;
import ui.battle.BattleOverlayRenderer;
import world.map.MapLoader;
import world.map.TileMap;
import ui.battle.BattleMessageQueue;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;

public class GamePanel extends JPanel {

    private static final int TILE_SIZE = 20;
    private static final int SCREEN_COLS = 16;
    private static final int SCREEN_ROWS = 12;
    private static final int INTERNAL_WIDTH = TILE_SIZE * SCREEN_COLS;
    private static final int INTERNAL_HEIGHT = TILE_SIZE * SCREEN_ROWS;
    private int enemyShakeOffsetX = 0;
    private int enemyShakeTicks = 0;
    private javax.swing.Timer enemyShakeTimer;
    private static final int SCALE = 2;

    private static final int WIDTH = INTERNAL_WIDTH * SCALE;
    private static final int HEIGHT = INTERNAL_HEIGHT * SCALE;
    private BufferedImage screenBuffer;
    private BattleMessageQueue battleMessageQueue = new BattleMessageQueue();

    private boolean combateTerminadoPendiente = false;
    private boolean jugadorGanoPendiente = false;
    private long lastMoveTime = 0;
    private static final long MOVE_COOLDOWN_MS = 170;

    private enum GameViewState {
        EXPLORATION,
        COMBAT
    }

    private enum BattleMenuMode {
        COMMAND,
        INVENTORY,
        STATUS,
        MESSAGE
    }

    private GameEngine engine;
    private CombatSystem combatSystem;
    private GameViewState currentView = GameViewState.EXPLORATION;
    private BattleMenuMode battleMenuMode = BattleMenuMode.COMMAND;

    private BattleOverlayRenderer battleRenderer = new BattleOverlayRenderer();

    private TileMap mapa;

    private int playerCol = 2;
    private int playerRow = 2;
    private int screenCol = 0;
    private int screenRow = 0;

    private int selectedCommand = 0;
    private int selectedItem = 0;
    private int inventoryScrollOffset = 0;

    private String battleMessage = "Elige un comando.";

    //PLAYA
    private BufferedImage sand;
    private BufferedImage sand2;
    private BufferedImage wall;
    private BufferedImage water;

    //CUEVA
    private BufferedImage stone;

    //BOSQUE
    private BufferedImage grass;


    private BufferedImage player;
    private BufferedImage enemySprite;
    private BufferedImage battleBackground;
    private String fullBattleMessage = "";
    private String visibleBattleMessage = "";
    private int messageCharIndex = 0;
    private Timer messageTimer;

    private boolean messageFinished = true;
    private boolean arrowVisible = false;
    private Timer arrowBlinkTimer;

    public GamePanel(GameEngine engine) {
        this.engine = engine;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        screenBuffer = new BufferedImage(INTERNAL_WIDTH, INTERNAL_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        cargarMapa();
        cargarImagenes();
        configurarControles();
        configurarTimersMensaje();
        configurarEnemyShake();
    }
    private void configurarEnemyShake() {
        enemyShakeTimer = new javax.swing.Timer(40, e -> {
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
    private void cargarMapa() {
        mapa = MapLoader.cargar("/assets/maps/overworld.txt");
    }

    private void cargarImagenes() {
        sand = cargarImagenResource("/assets/tiles/sand.png");
        sand2 = cargarImagenResource("/assets/tiles/sand_stone.png");
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

    private void configurarControles() {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("UP"), "up");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "down");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "left");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "space");
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
                if (currentView == GameViewState.EXPLORATION) moverJugador(-1, 0);
            }
        });

        actionMap.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentView == GameViewState.EXPLORATION) moverJugador(1, 0);
            }
        });

        actionMap.put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarAccion();
            }
        });

        actionMap.put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarAccion();
            }
        });
    }

    private void manejarArriba() {
        if (currentView == GameViewState.EXPLORATION) {
            moverJugador(0, -1);
            return;
        }

        if (battleMenuMode == BattleMenuMode.COMMAND) {
            selectedCommand--;
            if (selectedCommand < 0) selectedCommand = 2;
        } else if (battleMenuMode == BattleMenuMode.INVENTORY) {
            moverSeleccionInventario(-1);
        }

        repaint();
    }

    private void manejarAbajo() {
        if (currentView == GameViewState.EXPLORATION) {
            moverJugador(0, 1);
            return;
        }

        if (battleMenuMode == BattleMenuMode.COMMAND) {
            selectedCommand++;
            if (selectedCommand > 2) selectedCommand = 0;
        } else if (battleMenuMode == BattleMenuMode.INVENTORY) {
            moverSeleccionInventario(1);
        }

        repaint();
    }
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

    private void confirmarAccion() {
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
        if (currentView == GameViewState.COMBAT) {
            if (battleMenuMode == BattleMenuMode.MESSAGE) {
                return;
            }

            battleMenuMode = BattleMenuMode.COMMAND;
            battleMessage = "Elige un comando.";
            repaint();
        }
    }

    private void confirmarComando() {
        switch (selectedCommand) {
            case 0:
                atacar();
                break;

            case 1:
                battleMenuMode = BattleMenuMode.INVENTORY;
                selectedItem = 0;
                inventoryScrollOffset = 0;
                battleMessage = "Selecciona un item.";
                repaint();
                break;

            case 2:
                battleMenuMode = BattleMenuMode.STATUS;
                battleMessage = "";
                repaint();
                break;
        }
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

        if (result.isCombateTerminado()) {
            marcarCombateTerminado(result.isJugadorGano());
        }

        mostrarMensajesDeCombate(result.getMensaje());
    }

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

    private void iniciarCombate(ExplorationResult result) {
        currentView = GameViewState.COMBAT;
        battleMenuMode = BattleMenuMode.COMMAND;
        selectedCommand = 0;
        selectedItem = 0;

        Enemigo enemigo = result.getEnemigo();
        combatSystem = new CombatSystem(engine.getJugador(), enemigo);

        enemySprite = cargarImagenDesdePath(enemigo.spritePath);
        battleBackground = cargarFondoCombate();

        mostrarMensajesDeCombate(combatSystem.iniciarCombate());
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

    private void terminarCombate(boolean jugadorGano) {
        if (jugadorGano) {
            LootResult lootResult = engine.getLootSystem()
                    .procesarRecompensas(engine.getJugador(), combatSystem.getEnemigo());

            if (!lootResult.getMensaje().isEmpty()) {
                battleMessage = lootResult.getMensaje();
            }

            if (lootResult.tieneDrop()) {
                manejarDropConInventarioLleno(lootResult.getItemDropeado());
            }
        } else {
            battleMessage = "Has sido derrotado.";
        }

        engine.finalizarCombate(engine.getJugador().estaVivo());

        currentView = GameViewState.EXPLORATION;
        battleMenuMode = BattleMenuMode.COMMAND;
        combatSystem = null;
        enemySprite = null;
        battleBackground = null;

        repaint();
    }

    private void manejarDropConInventarioLleno(Item nuevoItem) {
        JOptionPane.showMessageDialog(
                this,
                "Inventario lleno. No se recogió: " + nuevoItem.getNombre()
        );
    }

    private void actualizarPantallaActual() {
        screenCol = playerCol / SCREEN_COLS;
        screenRow = playerRow / SCREEN_ROWS;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D bufferGraphics = screenBuffer.createGraphics();

        bufferGraphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.fillRect(0, 0, INTERNAL_WIDTH, INTERNAL_HEIGHT);

        dibujarMapa(bufferGraphics);
        dibujarJugador(bufferGraphics);

        if (currentView == GameViewState.COMBAT && combatSystem != null) {
            battleRenderer.draw(
                    bufferGraphics,
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
                    enemyShakeOffsetX
            );
        }

        dibujarDebug(bufferGraphics);

        bufferGraphics.dispose();

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        g2.drawImage(screenBuffer, 0, 0, WIDTH, HEIGHT, null);
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
            battleMenuMode = BattleMenuMode.MESSAGE;
            iniciarTextoTypewriter(battleMessageQueue.siguiente());
            return;
        }

        if (combateTerminadoPendiente) {
            finalizarCombatePendiente();
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

    private void marcarCombateTerminado(boolean jugadorGano) {
        this.combateTerminadoPendiente = true;
        this.jugadorGanoPendiente = jugadorGano;
    }

    private void finalizarCombatePendiente() {
        if (jugadorGanoPendiente) {
            LootResult lootResult = engine.getLootSystem()
                    .procesarRecompensas(engine.getJugador(), combatSystem.getEnemigo());

            if (!lootResult.getMensaje().isEmpty()) {
                JOptionPane.showMessageDialog(this, lootResult.getMensaje());
            }

            if (lootResult.tieneDrop()) {
                manejarDropConInventarioLleno(lootResult.getItemDropeado());
            }
        }

        engine.finalizarCombate(engine.getJugador().estaVivo());

        currentView = GameViewState.EXPLORATION;
        battleMenuMode = BattleMenuMode.COMMAND;
        combatSystem = null;
        enemySprite = null;
        battleBackground = null;

        combateTerminadoPendiente = false;
        jugadorGanoPendiente = false;

        battleMessageQueue.limpiar();
        battleMessage = "Elige un comando.";

        repaint();
    }
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
    private void iniciarTextoTypewriter(String mensaje) {
        fullBattleMessage = mensaje == null ? "" : mensaje;
        visibleBattleMessage = "";
        messageCharIndex = 0;
        messageFinished = false;
        arrowVisible = false;

        battleMessage = visibleBattleMessage;

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
        if (!messageFinished) {
            messageTimer.stop();
            visibleBattleMessage = fullBattleMessage;
            battleMessage = visibleBattleMessage;
            messageCharIndex = fullBattleMessage.length();
            messageFinished = true;
            arrowVisible = true;
            repaint();
        }
    }
    private java.util.List<Item> getItemsOrdenados() {
        java.util.List<Item> originales = engine.getJugador().inventario.getItems();

        java.util.List<Item> equipados = new java.util.ArrayList<>();
        java.util.List<Item> noEquipados = new java.util.ArrayList<>();

        for (Item item : originales) {
            if (engine.getJugador().estaEquipado(item)) {
                equipados.add(item);
            } else {
                noEquipados.add(item);
            }
        }

        java.util.List<Item> resultado = new java.util.ArrayList<>();
        resultado.addAll(equipados);
        resultado.addAll(noEquipados);

        return resultado;
    }
    private int convertirIndiceVisualAReal(int indiceVisual) {
        java.util.List<Item> ordenados = getItemsOrdenados();
        java.util.List<Item> originales = engine.getJugador().inventario.getItems();

        if (indiceVisual < 0 || indiceVisual >= ordenados.size()) {
            return -1;
        }

        Item seleccionado = ordenados.get(indiceVisual);

        return originales.indexOf(seleccionado);
    }
}