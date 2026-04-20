package data.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.model.DatosArma;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmaLoader {

    public static Map<String, DatosArma> cargar(String ruta) {

        Map<String, DatosArma> catalogo = new HashMap<>();

        try {
            Gson gson = new Gson();

            Type listType = new TypeToken<List<DatosArma>>() {}.getType();

            List<DatosArma> lista = gson.fromJson(
                    new FileReader(ruta),
                    listType
            );

            // Convertimos lista a mapa por ID
            for (DatosArma arma : lista) {
                catalogo.put(arma.id, arma);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return catalogo;
    }
}
