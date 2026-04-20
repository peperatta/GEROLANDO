package characters;

import items.Arma;

import java.util.Objects;

public class Enemigo {
        public String nombre;
        private int vida;
        public int vidaActual;
        private int ataque;
        private int defensa;
        private int velocidad;
        public String debilidad;
        public String spritePath;

        public Enemigo(String nombre, int vida, int ataque, int defensa, String spritePath, int velocidad, String debilidad) {
            this.nombre = nombre;
            this.vida = vida;
            this.vidaActual = vida;
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
        public void recibirAtaque(int dano, String tipoAtaque) {

            int danoReducido = dano - this.defensa;

            if (danoReducido <= 0) {
                System.out.println(nombre + " bloqueó el ataque.");
                return;
            }

            int danoTotal = danoReducido;

            if (Objects.equals(this.debilidad, tipoAtaque)) {
                danoTotal += 3;
                System.out.println(nombre + " es débil a " + tipoAtaque + "!");
            }

            this.vidaActual -= danoTotal;

            if (this.vidaActual < 0) {
                this.vidaActual = 0;
            }

            System.out.println(nombre + " recibió " + danoTotal + " de daño. Vida restante: " + this.vidaActual);
        }
        public void atacar(Gerolando gerolando) {
            int dano = this.ataque;
            if (dano <= 0) {
                System.out.println(nombre + " atacó pero no causó daño.");
                return;
            }
            System.out.println(nombre + " hizo un ataque de " + dano + " de daño");
            gerolando.recibirAtaque(dano);
        }
}
