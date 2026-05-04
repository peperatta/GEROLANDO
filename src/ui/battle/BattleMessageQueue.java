package ui.battle;

import java.util.LinkedList;
import java.util.Queue;

public class BattleMessageQueue {
    private Queue<String> mensajes;

    public BattleMessageQueue() {
        this.mensajes = new LinkedList<>();
    }

    public void agregar(String mensaje) {
        if (mensaje == null || mensaje.isEmpty()) return;
        mensajes.add(mensaje);
    }

    public void agregarLineas(String texto) {
        if (texto == null || texto.isEmpty()) return;

        String[] lineas = texto.split("\n");

        for (String linea : lineas) {
            agregar(linea);
        }
    }

    public String siguiente() {
        if (mensajes.isEmpty()) {
            return "";
        }

        return mensajes.poll();
    }

    public boolean estaVacia() {
        return mensajes.isEmpty();
    }

    public void limpiar() {
        mensajes.clear();
    }
}