package data.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.model.DatosPocion;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionLoader {

    public static Map<String, DatosPocion> cargar(String ruta) {

        Map<String, DatosPocion> catalogo = new HashMap<>();

        try {
            Gson gson = new Gson();

            Type listType = new TypeToken<List<DatosPocion>>() {}.getType();

            List<DatosPocion> lista = gson.fromJson(
                    new FileReader(ruta),
                    listType
            );

            // Convertimos lista a mapa por ID
            for (DatosPocion pocion : lista) {
                catalogo.put(pocion.id, pocion);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return catalogo;
    }
}

