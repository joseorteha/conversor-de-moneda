package conversor;

/**
 * Clase encargada de la lógica de conversión de monedas.
 */
public class Conversor {
    private ApiService apiService;

    public Conversor(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Convierte una cantidad de una moneda a otra usando la tasa obtenida de la API.
     * @param cantidad Cantidad a convertir
     * @param monedaBase Moneda de origen
     * @param monedaDestino Moneda de destino
     * @return cantidad convertida
     */
    public double convertir(double cantidad, String monedaBase, String monedaDestino) throws Exception {
        double tasa = apiService.obtenerTasaDeCambio(monedaBase, monedaDestino);
        return cantidad * tasa;
    }
}
