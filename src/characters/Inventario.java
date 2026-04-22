package characters;

import java.util.ArrayList;
import java.util.List;
import items.Item;

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

    public void mostrarInventario() {
        System.out.println("=== Inventario ===");

        if (items.isEmpty()) {
            System.out.println("Vacío");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getNombre());
        }
    }
}