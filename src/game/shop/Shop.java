package game.shop;

import characters.Gerolando;
import data.factory.ArmaFactory;
import data.factory.ArmaduraFactory;
import data.factory.PotionFactory;
import items.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Shop {
    private List<ShopProduct> productos;
    private PotionFactory potionFactory;
    private ArmaFactory armaFactory;
    private ArmaduraFactory armaduraFactory;
    private Scanner scanner;

    public Shop(PotionFactory potionFactory, ArmaFactory armaFactory, ArmaduraFactory armaduraFactory) {
        this.potionFactory = potionFactory;
        this.armaFactory = armaFactory;
        this.armaduraFactory = armaduraFactory;
        this.scanner = new Scanner(System.in);
        this.productos = new ArrayList<>();

        cargarCatalogoInicial();
    }

    private void cargarCatalogoInicial() {
        // Consumibles
        productos.add(new ShopProduct("pocion_vida_pequena", "Poción de vida pequeña", 15, "consumible"));
        productos.add(new ShopProduct("pocion_mana_pequena", "Poción de mana pequeña", 20, "consumible"));

        // Armas
        productos.add(new ShopProduct("espada_madera", "Espada de madera", 25, "arma"));
        productos.add(new ShopProduct("espada_hierro", "Espada de hierro", 50, "arma"));

        // Armaduras
        productos.add(new ShopProduct("ropa_vieja", "Ropa vieja", 10, "armadura"));
    }

    public void abrirTienda(Gerolando jugador) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n=== TIENDA ===");
            System.out.println("Oro actual: " + jugador.getOro());
            System.out.println("1. Ver productos");
            System.out.println("2. Salir");
            System.out.print("Elige una opción: ");

            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    mostrarProductos();
                    comprarProducto(jugador);
                    break;
                case 2:
                    salir = true;
                    System.out.println("Sales de la tienda.");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void mostrarProductos() {
        System.out.println("\n=== PRODUCTOS DISPONIBLES ===");

        for (int i = 0; i < productos.size(); i++) {
            ShopProduct p = productos.get(i);
            System.out.println((i + 1) + ". " + p.getNombreVisible()
                    + " [" + p.getCategoria() + "] - "
                    + p.getPrecio() + " oro");
        }

        System.out.println("0. Cancelar");
    }

    private void comprarProducto(Gerolando jugador) {
        System.out.print("Selecciona un producto por número: ");
        int opcion = scanner.nextInt();

        if (opcion == 0) {
            System.out.println("Compra cancelada.");
            return;
        }

        int index = opcion - 1;

        if (index < 0 || index >= productos.size()) {
            System.out.println("Producto no válido.");
            return;
        }

        ShopProduct producto = productos.get(index);

        if (!jugador.gastarOro(producto.getPrecio())) {
            return;
        }

        Item itemComprado = crearItemDesdeProducto(producto);

        if (itemComprado == null) {
            System.out.println("No se pudo crear el producto.");
            jugador.ganarOro(producto.getPrecio());
            return;
        }

        boolean agregado = jugador.inventario.agregar(itemComprado);

        if (!agregado) {
            System.out.println("No se pudo agregar al inventario.");
            jugador.ganarOro(producto.getPrecio());
            return;
        }

        System.out.println("Compraste: " + itemComprado.getNombre());
    }

    private Item crearItemDesdeProducto(ShopProduct producto) {
        switch (producto.getCategoria()) {
            case "consumible":
                return potionFactory.crear(producto.getItemId());

            case "arma":
                return armaFactory.crear(producto.getItemId());

            case "armadura":
                return armaduraFactory.crear(producto.getItemId());

            default:
                return null;
        }
    }
}