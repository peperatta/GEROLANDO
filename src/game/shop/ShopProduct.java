package game.shop;

public class ShopProduct {
    private String itemId;
    private String nombreVisible;
    private int precio;
    private String categoria;

    public ShopProduct(String itemId, String nombreVisible, int precio, String categoria) {
        this.itemId = itemId;
        this.nombreVisible = nombreVisible;
        this.precio = precio;
        this.categoria = categoria;
    }

    public String getItemId() {
        return itemId;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public int getPrecio() {
        return precio;
    }

    public String getCategoria() {
        return categoria;
    }
}