package conversor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Clase encargada de conectarse a la API de ExchangeRate y obtener tasas de cambio.
 */
public class ApiService {
    /**
     * Obtiene la lista de monedas soportadas por la API.
     * @param monedaBase Moneda de referencia para la consulta
     * @return Array de códigos de monedas disponibles
     */
    public String[] obtenerMonedasDisponibles(String monedaBase) throws IOException, InterruptedException {
        String url = BASE_URL + API_KEY + "/latest/" + monedaBase;
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() != 200) {
            throw new IOException("Error al conectar con la API. Código: " + respuesta.statusCode());
        }
        JsonObject json = JsonParser.parseString(respuesta.body()).getAsJsonObject();
        if (!json.get("result").getAsString().equals("success")) {
            throw new IOException("Respuesta inválida de la API.");
        }
        JsonObject rates = json.getAsJsonObject("conversion_rates");
        return rates.keySet().toArray(new String[0]);
    }
    private static final String API_KEY = "71585e2274ffc21825359c38"; // API Key del usuario
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    /**
     * Obtiene la tasa de conversión entre dos monedas.
     * @param monedaBase Moneda de origen (ej: USD)
     * @param monedaDestino Moneda de destino (ej: ARS)
     * @return tasa de conversión, o -1 si hay error
     */
    public double obtenerTasaDeCambio(String monedaBase, String monedaDestino) throws IOException, InterruptedException {
        String url = BASE_URL + API_KEY + "/latest/" + monedaBase;
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());

        if (respuesta.statusCode() != 200) {
            throw new IOException("Error al conectar con la API. Código: " + respuesta.statusCode());
        }

        JsonObject json = JsonParser.parseString(respuesta.body()).getAsJsonObject();
        if (!json.get("result").getAsString().equals("success")) {
            throw new IOException("Respuesta inválida de la API.");
        }

        JsonObject rates = json.getAsJsonObject("conversion_rates");
        if (!rates.has(monedaDestino)) {
            throw new IOException("Moneda destino no soportada.");
        }

        return rates.get(monedaDestino).getAsDouble();
    }
}
