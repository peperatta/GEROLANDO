package game.loot;

import characters.Enemigo;
import characters.Gerolando;
import data.factory.ArmaFactory;
import data.factory.ArmaduraFactory;
import data.factory.PotionFactory;
import items.Item;

import java.util.List;
import java.util.Random;

public class LootSystem {
    private PotionFactory potionFactory;
    private ArmaFactory armaFactory;
    private ArmaduraFactory armaduraFactory;
    private Random random;

    public LootSystem(PotionFactory potionFactory,
                      ArmaFactory armaFactory,
                      ArmaduraFactory armaduraFactory) {
        this.potionFactory = potionFactory;
        this.armaFactory = armaFactory;
        this.armaduraFactory = armaduraFactory;
        this.random = new Random();
    }

    public LootResult procesarRecompensas(Gerolando jugador, Enemigo enemigo) {
        int xpGanada = enemigo.getVida() / 5;
        int oroGanado = enemigo.getVida() / 3;

        jugador.ganarXP(xpGanada);
        jugador.ganarOro(oroGanado);

        StringBuilder mensaje = new StringBuilder();
        Item drop = calcularDrop(enemigo);

        if (drop == null) {
            return new LootResult(mensaje.toString(), null);
        }

        mensaje.append("\n¡El enemigo soltó: ").append(drop.getNombre()).append("!");

        if (!jugador.inventario.estaLleno()) {
            jugador.inventario.agregar(drop);
            mensaje.append("\n").append(drop.getNombre()).append(" fue agregado al inventario.");
            return new LootResult(mensaje.toString(), null);
        }

        mensaje.append("\nEl inventario está lleno.");
        return new LootResult(mensaje.toString(), drop);
    }

    private Item calcularDrop(Enemigo enemigo) {
        List<String> drops = enemigo.getDrops();

        if (drops == null || drops.isEmpty()) {
            return null;
        }

        int roll = random.nextInt(100) + 1;

        if (roll > enemigo.getDropChance()) {
            return null;
        }

        String dropId = drops.get(random.nextInt(drops.size()));

        return crearItemDesdeId(dropId);
    }

    private Item crearItemDesdeId(String id) {
        try {
            return potionFactory.crear(id);
        } catch (Exception ignored) {
        }

        try {
            return armaFactory.crear(id);
        } catch (Exception ignored) {
        }

        try {
            return armaduraFactory.crear(id);
        } catch (Exception ignored) {
        }

        return null;
    }
}