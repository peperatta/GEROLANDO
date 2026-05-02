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

public class GamePanel extends JPanel {

    private static final int TILE_SIZE = 20;
    private static final int SCREEN_COLS = 16;
    private static final int SCREEN_ROWS = 12;
    private static final int WIDTH = TILE_SIZE * SCREEN_COLS;
    private static final int HEIGHT = TILE_SIZE * SCREEN_ROWS;
    private BattleMessageQueue battleMessageQueue = new BattleMessageQueue();

    private boolean combateTerminadoPendiente = false;
    private boolean jugadorGanoPendiente = false;

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

    private String battleMessage = "Elige un comando.";

    private BufferedImage grass;
    private BufferedImage wall;
    private BufferedImage water;
    private BufferedImage player;
    private BufferedImage enemySprite;
    private BufferedImage battleBackground;

    public GamePanel(GameEngine engine) {
        this.engine = engine;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        cargarMapa();
        cargarImagenes();
        configurarControles();
    }

    private void cargarMapa() {
        mapa = MapLoader.cargar("/assets/maps/overworld.txt");
    }

    private void cargarImagenes() {
        grass = cargarImagenResource("/assets/tiles/grass.png");
        wall = cargarImagenResource("/assets/tiles/wall.png");
        water = cargarImagenResource("/assets/tiles/water.png");
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
                if (currentView == GameViewState.EXPLORATION) moverJugador(-1, 0);
            }
        });

        actionMap.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentView == GameViewState.EXPLORATION) moverJugador(1, 0);
            }
        });

        actionMap.put("enter", new AbstractAction() {
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
            selectedItem--;
            if (selectedItem < 0) selectedItem = Math.max(0, engine.getJugador().inventario.size() - 1);
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
            selectedItem++;
            if (selectedItem >= engine.getJugador().inventario.size()) selectedItem = 0;
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

        StringBuilder mensajes = new StringBuilder();
        mensajes.append("Gerolando ataca.");

        if (!result.getMensaje().isEmpty()) {
            mensajes.append("\n").append(result.getMensaje());
        }

        if (result.isCombateTerminado()) {
            marcarCombateTerminado(result.isJugadorGano());
        }

        mostrarMensajesDeCombate(mensajes.toString());
    }

    private void usarItemSeleccionado() {
        if (combatSystem == null) return;

        if (engine.getJugador().inventario.size() == 0) {
            mostrarMensajesDeCombate("El inventario está vacío.");
            return;
        }

        CombatResult result = combatSystem.usarItem(selectedItem);

        StringBuilder mensajes = new StringBuilder();
        mensajes.append("Usaste un item.");

        if (!result.getMensaje().isEmpty()) {
            mensajes.append("\n").append(result.getMensaje());
        }

        if (result.isCombateTerminado()) {
            marcarCombateTerminado(result.isJugadorGano());
        }

        mostrarMensajesDeCombate(mensajes.toString());
    }

    private void moverJugador(int colDelta, int rowDelta) {
        if (currentView != GameViewState.EXPLORATION) return;

        int nuevaCol = playerCol + colDelta;
        int nuevaRow = playerRow + rowDelta;

        if (!mapa.esCaminable(nuevaRow, nuevaCol)) {
            return;
        }

        playerCol = nuevaCol;
        playerRow = nuevaRow;

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

        battleMessage = result.getMensaje() + "\n" + combatSystem.iniciarCombate();
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

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        dibujarMapa(g2);
        dibujarJugador(g2);

        if (currentView == GameViewState.COMBAT && combatSystem != null) {
            battleRenderer.draw(
                    g2,
                    WIDTH,
                    HEIGHT,
                    combatSystem,
                    enemySprite,
                    battleBackground,
                    selectedCommand,
                    selectedItem,
                    battleMenuMode.name(),
                    battleMessage
            );
        }

        dibujarDebug(g2);
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
            battleMessage = battleMessageQueue.siguiente();
            battleMenuMode = BattleMenuMode.MESSAGE;
        } else {
            battleMessage = "Elige un comando.";
            battleMenuMode = BattleMenuMode.COMMAND;
        }

        repaint();
    }

    private void avanzarMensajeCombate() {
        if (!battleMessageQueue.estaVacia()) {
            battleMessage = battleMessageQueue.siguiente();
            repaint();
            return;
        }

        if (combateTerminadoPendiente) {
            finalizarCombatePendiente();
            return;
        }

        battleMessage = "Elige un comando.";
        battleMenuMode = BattleMenuMode.COMMAND;
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
}