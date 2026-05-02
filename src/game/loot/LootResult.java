package game.loot;

import items.Item;

public class LootResult {
    private String mensaje;
    private Item itemDropeado;

    public LootResult(String mensaje, Item itemDropeado) {
        this.mensaje = mensaje;
        this.itemDropeado = itemDropeado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Item getItemDropeado() {
        return itemDropeado;
    }

    public boolean tieneDrop() {
        return itemDropeado != null;
    }
}