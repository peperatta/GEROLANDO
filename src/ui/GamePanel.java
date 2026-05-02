package ui;

import world.map.MapLoader;
import world.map.TileMap;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GamePanel extends JPanel {

    private static final int TILE_SIZE = 20;
    private static final int SCREEN_COLS = 16;
    private static final int SCREEN_ROWS = 12;

    private static final int WIDTH = TILE_SIZE * SCREEN_COLS;
    private static final int HEIGHT = TILE_SIZE * SCREEN_ROWS;

    private TileMap mapa;

    private int playerCol = 2;
    private int playerRow = 2;

    private int screenCol = 0;
    private int screenRow = 0;

    private BufferedImage grass;
    private BufferedImage wall;
    private BufferedImage water;
    private BufferedImage player;

    public GamePanel() {
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
        grass = cargarImagen("/assets/tiles/grass.png");
        wall = cargarImagen("/assets/tiles/wall.png");
        water = cargarImagen("/assets/tiles/water.png");
        player = cargarImagen("/assets/sprites/player/gerolando.png");
    }

    private BufferedImage cargarImagen(String ruta) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(ruta));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("No se pudo cargar imagen: " + ruta);
            return null;
        }
    }

    private void configurarControles() {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("UP"), "moverArriba");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "moverAbajo");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moverIzquierda");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moverDerecha");

        actionMap.put("moverArriba", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moverJugador(0, -1);
            }
        });

        actionMap.put("moverAbajo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moverJugador(0, 1);
            }
        });

        actionMap.put("moverIzquierda", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moverJugador(-1, 0);
            }
        });

        actionMap.put("moverDerecha", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moverJugador(1, 0);
            }
        });
    }

    private void moverJugador(int colDelta, int rowDelta) {
        int nuevaCol = playerCol + colDelta;
        int nuevaRow = playerRow + rowDelta;

        if (!mapa.esCaminable(nuevaRow, nuevaCol)) {
            return;
        }

        playerCol = nuevaCol;
        playerRow = nuevaRow;

        actualizarPantallaActual();
        repaint();
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
}