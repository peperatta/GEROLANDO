package data.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.model.DatosEnemigo;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnemigoLoader {

    public static Map<String, DatosEnemigo> cargar(String ruta) {

        Map<String, DatosEnemigo> catalogo = new HashMap<>();

        try {
            Gson gson = new Gson();

            Type listType = new TypeToken<List<DatosEnemigo>>() {}.getType();

            List<DatosEnemigo> lista = gson.fromJson(
                    new FileReader(ruta),
                    listType
            );

            // Convertimos lista → mapa por ID
            for (DatosEnemigo enemigo : lista) {
                catalogo.put(enemigo.id, enemigo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return catalogo;
    }
}

