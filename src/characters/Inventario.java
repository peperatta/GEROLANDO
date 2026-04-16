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

    // Agregar item (con límite)
    public boolean agregar(Item item) {
        if (items.size() >= MAX_ITEMS) {
            System.out.println("Inventario lleno");
            return false;
        }

        items.add(item);
        return true;
    }

    // Eliminar item
    public boolean eliminar(Item item) {
        return items.remove(item);
    }

    // Verificar si contiene un item
    public boolean contiene(Item item) {
        return items.contains(item);
    }

    // Obtener lista completa (solo lectura lógica)
    public List<Item> getItems() {
        return items;
    }

    // Tamaño actual
    public int size() {
        return items.size();
    }

    // Verificar si está lleno
    public boolean estaLleno() {
        return items.size() >= MAX_ITEMS;
    }

    // Limpiar inventario
    public void limpiar() {
        items.clear();
    }

    // Buscar item por nombre
    public Item buscarPorNombre(String nombre) {
        for (Item i : items) {
            if (i.getNombre().equalsIgnoreCase(nombre)) {
                return i;
            }
        }
        return null;
    }

    // Debug visual
    public void mostrarInventario() {
        System.out.println("=== Inventario ===");

        for (int i = 0; i < items.size(); i++) {
            System.out.println("- " + items.get(i).getNombre());
        }
    }
}