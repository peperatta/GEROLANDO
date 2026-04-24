package characters;

import java.util.ArrayList;
import java.util.List;
import items.Item;
import items.TipoItem;

public class Inventario {

    private List<Item> items;
    private static final int MAX_ITEMS = 10;

    public Inventario() {
        this.items = new ArrayList<>();
    }

    public boolean agregar(Item item) {
        if (items.size() >= MAX_ITEMS) {
            System.out.println("Inventario lleno");
            return false;
        }

        items.add(item);
        return true;
    }

    public boolean eliminar(Item item) {
        return items.remove(item);
    }

    public boolean contiene(Item item) {
        return items.contains(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public int size() {
        return items.size();
    }

    public boolean estaLleno() {
        return items.size() >= MAX_ITEMS;
    }

    public void limpiar() {
        items.clear();
    }

    public Item buscarPorNombre(String nombre) {
        for (Item i : items) {
            if (i.getNombre().equalsIgnoreCase(nombre)) {
                return i;
            }
        }
        return null;
    }

    public Item getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    public boolean reemplazarItem(int index, Item nuevoItem) {
        if (index < 0 || index >= items.size() || nuevoItem == null) {
            return false;
        }

        items.set(index, nuevoItem);
        return true;
    }

    public void mostrarInventario(Gerolando jugador) {
        if (items.isEmpty()) {
            System.out.println("Vacío");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            String linea = (i + 1) + ". " + item.getNombre();

            // 👉 AQUÍ es donde va el cambio de tipo
            linea += " (" + formatearTipo(item.getTipo()) + ")";

            // Equipado
            if (jugador.estaEquipado(item)) {
                linea += " [EQUIPADO]";
            }

            System.out.println(linea);
        }
    }
    private String formatearTipo(TipoItem tipo) {
        switch (tipo) {
            case ARMA: return "Arma";
            case ARMADURA: return "Armadura";
            case CONSUMIBLE: return "Consumible";
            default: return "Desconocido";
        }
    }
}