package data.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.model.DatosArmadura;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmaduraLoader {

    public static Map<String, DatosArmadura> cargar(String ruta) {

        Map<String, DatosArmadura> catalogo = new HashMap<>();

        try {
            Gson gson = new Gson();

            Type listType = new TypeToken<List<DatosArmadura>>() {}.getType();

            List<DatosArmadura> lista = gson.fromJson(
                    new FileReader(ruta),
                    listType
            );

            // Convertimos lista → mapa por ID
            for (DatosArmadura armadura : lista) {
                catalogo.put(armadura.id, armadura);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return catalogo;
    }
}

