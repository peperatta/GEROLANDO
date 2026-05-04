package world.map;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    public static TileMap cargar(String ruta) {
        try {
            InputStream input = MapLoader.class.getResourceAsStream(ruta);

            if (input == null) {
                throw new RuntimeException("No se encontró el mapa: " + ruta);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            List<int[]> filas = new ArrayList<>();

            String linea;

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.trim().split("\\s+");
                int[] fila = new int[partes.length];

                for (int i = 0; i < partes.length; i++) {
                    fila[i] = Integer.parseInt(partes[i]);
                }

                filas.add(fila);
            }

            int[][] tiles = filas.toArray(new int[0][]);
            return new TileMap(tiles);

        } catch (Exception e) {
            throw new RuntimeException("Error cargando mapa: " + ruta, e);
        }
    }
}