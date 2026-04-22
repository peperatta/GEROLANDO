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
        productos.add(new ShopProduct("pocion_vida_pequena", "consumible"));
        productos.add(new ShopProduct("pocion_mana_pequena", "consumible"));

        // Armas
        productos.add(new ShopProduct("espada_madera", "arma"));
        productos.add(new ShopProduct("espada_hierro", "arma"));

        // Armaduras
        productos.add(new ShopProduct("ropa_vieja", "armadura"));
    }

    public void abrirTienda(Gerolando jugador) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n=== TIENDA ===");
            System.out.println("Oro actual: " + jugador.getOro());
            System.out.println("1. Comprar");
            System.out.println("2. Vender");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");

            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    mostrarProductos();
                    comprarProducto(jugador);
                    break;
                case 2:
                    venderProducto(jugador);
                    break;
                case 3:
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
            ShopProduct producto = productos.get(i);
            Item item = crearItemDesdeProducto(producto);

            if (item != null) {
                System.out.println((i + 1) + ". " + item.getNombre()
                        + " [" + producto.getCategoria() + "] - "
                        + item.getPrecio() + " oro");
            } else {
                System.out.println((i + 1) + ". Error al cargar producto");
            }
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
        Item itemComprado = crearItemDesdeProducto(producto);

        if (itemComprado == null) {
            System.out.println("No se pudo crear el producto.");
            return;
        }

        int precio = itemComprado.getPrecio();

        if (!jugador.gastarOro(precio)) {
            return;
        }

        boolean agregado = jugador.inventario.agregar(itemComprado);

        if (!agregado) {
            System.out.println("No se pudo agregar al inventario.");
            jugador.ganarOro(precio);
            return;
        }

        System.out.println("Compraste: " + itemComprado.getNombre() + " por " + precio + " de oro.");
    }

    private void venderProducto(Gerolando jugador) {
        if (jugador.inventario.size() == 0) {
            System.out.println("No tienes items para vender.");
            return;
        }

        System.out.println("\n=== VENDER ITEMS ===");
        jugador.inventario.mostrarInventario();
        System.out.println("0. Cancelar");
        System.out.print("Selecciona un item por número para vender: ");

        int opcion = scanner.nextInt();

        if (opcion == 0) {
            System.out.println("Venta cancelada.");
            return;
        }

        int index = opcion - 1;

        if (index < 0 || index >= jugador.inventario.size()) {
            System.out.println("Índice no válido.");
            return;
        }

        Item item = jugador.inventario.getItems().get(index);

        if (jugador.estaEquipado(item)) {
            System.out.println("No puedes vender un objeto que está equipado.");
            return;
        }

        int precioVenta = item.getPrecio() / 2;

        if (precioVenta <= 0) {
            System.out.println("Ese item no se puede vender.");
            return;
        }

        jugador.inventario.eliminar(item);
        jugador.ganarOro(precioVenta);

        System.out.println("Vendiste " + item.getNombre() + " por " + precioVenta + " de oro.");
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