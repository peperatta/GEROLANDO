package ui.battle;

import characters.Gerolando;
import game.combat.CombatSystem;
import items.Item;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BattleOverlayRenderer {

    private static final String[] COMMANDS = {"ATACAR", "INVENTARIO", "ESTADO"};

    public void draw(Graphics2D g2,
                     int width,
                     int height,
                     CombatSystem combatSystem,
                     BufferedImage enemySprite,
                     BufferedImage battleBackground,
                     int selectedCommand,
                     int selectedItem,
                     String menuMode,
                     String message) {

        drawBattleWindow(g2, width, height, battleBackground);
        drawStatsPanel(g2, combatSystem.getJugador());
        drawEnemy(g2, width, enemySprite, combatSystem);
        drawCommandPanel(g2, selectedCommand, menuMode);
        drawBottomPanel(g2, width, height, combatSystem, selectedItem, menuMode, message);
    }

    private void drawBattleWindow(Graphics2D g2, int width, int height, BufferedImage battleBackground) {
        int x = 55;
        int y = 35;
        int w = width - 110;
        int h = 105;

        g2.setColor(Color.BLACK);
        g2.fillRect(x - 4, y - 4, w + 8, h + 8);

        g2.setColor(Color.WHITE);
        g2.drawRect(x - 4, y - 4, w + 8, h + 8);

        if (battleBackground != null) {
            g2.drawImage(battleBackground, x, y, w, h, null);
        } else {
            g2.setColor(new Color(90, 170, 90));
            g2.fillRect(x, y, w, h);
        }
    }

    private void drawStatsPanel(Graphics2D g2, Gerolando jugador) {
        int x = 8;
        int y = 28;
        int w = 78;
        int h = 100;

        drawPanel(g2, x, y, w, h);

        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.setColor(Color.WHITE);

        g2.drawString("GEROLANDO", x + 8, y + 16);
        g2.drawString("LV " + jugador.getNivel(), x + 8, y + 34);
        g2.drawString("HP " + jugador.getVidaActual() + "/" + jugador.getVidaMax(), x + 8, y + 50);
        g2.drawString("MP " + jugador.getManaActual() + "/" + jugador.getManaMax(), x + 8, y + 66);
        g2.drawString("G  " + jugador.getOro(), x + 8, y + 82);
    }

    private void drawEnemy(Graphics2D g2, int width, BufferedImage enemySprite, CombatSystem combatSystem) {
        int enemySize = 46;
        int x = (width - enemySize) / 2;
        int y = 80;

        if (enemySprite != null) {
            g2.drawImage(enemySprite, x, y, enemySize, enemySize, null);
        } else {
            g2.setColor(Color.MAGENTA);
            g2.fillOval(x, y, enemySize, enemySize);
        }

        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.setColor(Color.WHITE);
        g2.drawString(
                combatSystem.getEnemigo().nombre + " HP: " + combatSystem.getEnemigo().vidaActual,
                x - 16,
                y + enemySize + 14
        );
    }

    private void drawCommandPanel(Graphics2D g2, int selectedCommand, String menuMode) {
        int x = 192;
        int y = 28;
        int w = 116;
        int h = 78;

        drawPanel(g2, x, y, w, h);

        g2.setFont(new Font("Monospaced", Font.BOLD, 11));

        for (int i = 0; i < COMMANDS.length; i++) {
            int textY = y + 20 + (i * 18);

            if (menuMode.equals("COMMAND") && i == selectedCommand) {
                g2.setColor(Color.WHITE);
                g2.drawString(">", x + 8, textY);
            }

            g2.setColor(Color.WHITE);
            g2.drawString(COMMANDS[i], x + 22, textY);
        }
    }

    private void drawBottomPanel(Graphics2D g2,
                                 int width,
                                 int height,
                                 CombatSystem combatSystem,
                                 int selectedItem,
                                 String menuMode,
                                 String message) {
        int x = 15;
        int y = height - 85;
        int w = width - 30;
        int h = 70;

        drawPanel(g2, x, y, w, h);

        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.setColor(Color.WHITE);

        if (menuMode.equals("INVENTORY")) {
            drawInventory(g2, combatSystem, selectedItem, x, y);
            return;
        }

        if (menuMode.equals("STATUS")) {
            Gerolando j = combatSystem.getJugador();
            g2.drawString("ATK: " + j.getAtaque() + "   DEF: " + j.getDefensa(), x + 12, y + 22);
            g2.drawString("VEL: " + j.getVelocidad() + "   XP: " + j.getXP(), x + 12, y + 40);
            g2.drawString("ESC para volver", x + 12, y + 58);
            return;
        }

        if (message == null || message.isEmpty()) {
            g2.drawString("Elige un comando.", x + 12, y + 28);
        } else {
            drawWrappedText(g2, message, x + 12, y + 22, w - 24);
        }
    }

    private void drawInventory(Graphics2D g2, CombatSystem combatSystem, int selectedItem, int x, int y) {
        if (combatSystem.getJugador().inventario.size() == 0) {
            g2.drawString("Inventario vacío.", x + 12, y + 28);
            g2.drawString("ESC para volver", x + 12, y + 48);
            return;
        }

        g2.drawString("Elige item. ESC para volver.", x + 12, y + 16);

        int maxVisible = Math.min(3, combatSystem.getJugador().inventario.size());

        for (int i = 0; i < maxVisible; i++) {
            Item item = combatSystem.getJugador().inventario.getItems().get(i);
            int textY = y + 34 + (i * 15);

            if (i == selectedItem) {
                g2.drawString(">", x + 12, textY);
            }

            g2.drawString(item.getNombre(), x + 28, textY);
        }
    }

    private void drawPanel(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(Color.BLACK);
        g2.fillRect(x, y, w, h);

        g2.setColor(Color.WHITE);
        g2.drawRect(x, y, w, h);
        g2.drawRect(x + 2, y + 2, w - 4, h - 4);
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineHeight = 15;

        for (String word : words) {
            String testLine = line + word + " ";

            if (g2.getFontMetrics().stringWidth(testLine) > maxWidth) {
                g2.drawString(line.toString(), x, y);
                line = new StringBuilder(word).append(" ");
                y += lineHeight;
            } else {
                line.append(word).append(" ");
            }
        }

        g2.drawString(line.toString(), x, y);
    }
}