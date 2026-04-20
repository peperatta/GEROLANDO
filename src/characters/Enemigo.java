package characters;

import items.Arma;

import java.util.Objects;

public class Enemigo {
        public String nombre;
        private int vida;
        private int ataque;
        private int defensa;
        private int velocidad;
        public String debilidad;
        public String spritePath;

        public Enemigo(String nombre, int vida, int ataque, int defensa, String spritePath, int velocidad, String debilidad) {
            this.nombre = nombre;
            this.vida = vida;
            this.ataque = ataque;
            this.defensa = defensa;
            this.spritePath = spritePath;
            this.velocidad = velocidad;
            this.debilidad = debilidad;
        }
        //Getters
        public int getVida() {
            return vida;
        }
        public int getVelocidad() {
            return velocidad;
        }
        public int getAtaque() {
            return ataque;
        }
        public int getDefensa() {
            return defensa;
        }
        public void recibirAtaque(Gerolando gerolando) {
            int dano = gerolando.getAtaque() - this.defensa;
            if (dano <= 0) {
                System.out.println(nombre + " bloqueó el ataque.");
                return;
            }

            int danoTotal = dano;

            if (Objects.equals(this.debilidad, gerolando.getTipoAtaque())) {
                danoTotal += 3;
                System.out.println(nombre + " recibió " + danoTotal + " de daño por su debilidad. Vida restante: " + (vida - danoTotal));
            } else {
                System.out.println(nombre + " recibió " + danoTotal + " de daño. Vida restante: " + (vida - danoTotal));
            }

            this.vida -= danoTotal;
        }
        public void atacar(Gerolando gerolando) {
            int dano = this.ataque - gerolando.getDefensa();
            if (dano <= 0) {
                System.out.println(nombre + " atacó pero no causó daño.");
                return;
            }
            System.out.println(nombre + " atacó y causó " + dano + " de daño a Gerolando");
            gerolando.recibirAtaque(dano);
        }
}
