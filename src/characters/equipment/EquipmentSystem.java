package characters.equipment;

import items.Arma;
import items.Armadura;
import items.Item;

public class EquipmentSystem {
    private Arma armaEquipada;
    private Armadura armaduraEquipada;

    public EquipmentSystem() {
        this.armaEquipada = null;
        this.armaduraEquipada = null;
    }

    public void equiparArma(Arma arma) {
        if (arma == null) return;

        this.armaEquipada = arma;
        System.out.println("Equipaste el arma: " + arma.getNombre());
    }

    public void equiparArmadura(Armadura armadura) {
        if (armadura == null) return;

        this.armaduraEquipada = armadura;
        System.out.println("Equipaste la armadura: " + armadura.getNombre());
    }

    public void usarItem(Item item) {
        if (item == null) return;

        if (item instanceof Arma) {
            if (armaEquipada == null || !armaEquipada.equals(item)) {
                equiparArma((Arma) item);
            } else {
                item.usar();
            }
            return;
        }

        if (item instanceof Armadura) {
            if (armaduraEquipada == null || !armaduraEquipada.equals(item)) {
                equiparArmadura((Armadura) item);
            } else {
                item.usar();
            }
        }
    }

    public Arma getArmaEquipada() {
        return armaEquipada;
    }

    public Armadura getArmaduraEquipada() {
        return armaduraEquipada;
    }

    public int getAtaqueBonus() {
        return (armaEquipada != null) ? armaEquipada.getAtaque() : 0;
    }

    public int getDefensaBonus() {
        return (armaduraEquipada != null) ? armaduraEquipada.getDefensa() : 0;
    }

    public int getPenalizacionPeso() {
        int pesoArma = (armaEquipada != null) ? armaEquipada.getPeso() : 0;
        int pesoArmadura = (armaduraEquipada != null) ? armaduraEquipada.getPeso() : 0;
        return pesoArma + pesoArmadura;
    }

    public String getTipoAtaque() {
        if (armaEquipada != null) {
            return armaEquipada.tipo;
        }
        return "melee";
    }
}