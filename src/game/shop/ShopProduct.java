package game.shop;

public class ShopProduct {
    private String itemId;
    private String categoria;

    public ShopProduct(String itemId, String categoria) {
        this.itemId = itemId;
        this.categoria = categoria;
    }

    public String getItemId() {
        return itemId;
    }

    public String getCategoria() {
        return categoria;
    }
}