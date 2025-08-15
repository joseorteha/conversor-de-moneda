package conversor;

import java.util.Scanner;
import conversor.util.Consola;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import com.google.gson.Gson;

/**
 * Clase principal que gestiona el menú y el flujo del programa.
 */
public class Main {
    // Cargar historial desde archivo
    private static void cargarHistorial(String archivo, List<String> historial) {
        File f = new File(archivo);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                historial.add(linea);
            }
        } catch (IOException e) {
            Consola.imprimirError("No se pudo cargar el historial: " + e.getMessage());
        }
    }

    // Guardar historial en archivo
    private static void guardarHistorial(String archivo, List<String> historial) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            for (String linea : historial) {
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            Consola.imprimirError("No se pudo guardar el historial: " + e.getMessage());
        }
    }

    // Cargar monedas favoritas desde archivo JSON
    private static List<String> cargarFavoritas() {
        File f = new File("favoritas.json");
        if (!f.exists()) return new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            Gson gson = new Gson();
            String[] arr = gson.fromJson(br, String[].class);
            return new ArrayList<>(Arrays.asList(arr));
        } catch (IOException e) {
            Consola.imprimirError("No se pudo cargar favoritas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Guardar monedas favoritas en archivo JSON
    private static void guardarFavoritas(List<String> favoritas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("favoritas.json"))) {
            Gson gson = new Gson();
            bw.write(gson.toJson(favoritas));
        } catch (IOException e) {
            Consola.imprimirError("No se pudo guardar favoritas: " + e.getMessage());
        }
    }

    // Configurar monedas favoritas
    private static void configurarFavoritas(Scanner scanner, String[] monedas, List<String> favoritas) {
        Consola.limpiarPantalla();
        Consola.imprimirTitulo("Configura tus monedas favoritas (máx. 5)");
        favoritas.clear();
        for (int i = 0; i < 5; i++) {
            Consola.imprimirInfo("Selecciona moneda favorita " + (i + 1) + " (o 0 para terminar):");
            for (int j = 0; j < monedas.length; j++) {
                System.out.printf("%d. %s\n", j + 1, monedas[j]);
            }
            String entrada = scanner.nextLine();
            if (entrada.equals("0")) break;
            try {
                int idx = Integer.parseInt(entrada);
                if (idx < 1 || idx > monedas.length) {
                    Consola.imprimirError("Opción inválida.");
                    i--;
                    continue;
                }
                String moneda = monedas[idx - 1];
                if (favoritas.contains(moneda)) {
                    Consola.imprimirError("Ya seleccionaste esa moneda.");
                    i--;
                    continue;
                }
                favoritas.add(moneda);
            } catch (NumberFormatException e) {
                Consola.imprimirError("Debes ingresar un número válido.");
                i--;
            }
        }
        Consola.imprimirInfo("Monedas favoritas guardadas: " + favoritas);
        System.out.println("(Presiona Enter para volver al menú principal)");
        scanner.nextLine();
    }

    // Menú de conversión (normal y múltiple)
    private static void menuConversion(Scanner scanner, Conversor conversor, String[] monedas, List<String> historial, List<String> favoritas, boolean multiple) {
        Consola.limpiarPantalla();
        Consola.imprimirTitulo(multiple ? "Conversión múltiple" : "Conversión de moneda");
        List<String> origenes = favoritas.isEmpty() ? Arrays.asList(monedas) : favoritas;
        Consola.imprimirInfo("Selecciona la moneda de origen:");
        for (int i = 0; i < origenes.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, origenes.get(i));
        }
        System.out.println("(Escribe el número de la moneda de origen o 0 para volver)");
        String entrada = scanner.nextLine();
        if (entrada.equals("0")) return;
        int opcionBase;
        try {
            opcionBase = Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            Consola.imprimirError("Error: Debe ingresar un número válido.");
            return;
        }
        if (opcionBase < 1 || opcionBase > origenes.size()) {
            Consola.imprimirError("Opción inválida.");
            return;
        }
        String monedaBase = origenes.get(opcionBase - 1);

        List<String> destinos = new ArrayList<>();
        if (multiple) {
            Consola.imprimirInfo("Selecciona monedas de destino (separadas por coma, máx. 5, no repitas la de origen):");
            for (int i = 0; i < monedas.length; i++) {
                System.out.printf("%d. %s\n", i + 1, monedas[i]);
            }
            System.out.println("Ejemplo: 2,5,7");
            entrada = scanner.nextLine();
            String[] partes = entrada.split(",");
            for (String parte : partes) {
                try {
                    int idx = Integer.parseInt(parte.trim());
                    if (idx < 1 || idx > monedas.length) continue;
                    String moneda = monedas[idx - 1];
                    if (!moneda.equals(monedaBase) && !destinos.contains(moneda) && destinos.size() < 5) {
                        destinos.add(moneda);
                    }
                } catch (NumberFormatException ignored) {}
            }
            if (destinos.isEmpty()) {
                Consola.imprimirError("No seleccionaste monedas válidas.");
                return;
            }
        } else {
            Consola.imprimirInfo("Selecciona la moneda de destino:");
            for (int i = 0; i < monedas.length; i++) {
                System.out.printf("%d. %s\n", i + 1, monedas[i]);
            }
            System.out.println("(Escribe el número de la moneda de destino o 0 para volver)");
            entrada = scanner.nextLine();
            if (entrada.equals("0")) return;
            int opcionDestino;
            try {
                opcionDestino = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                Consola.imprimirError("Error: Debe ingresar un número válido.");
                return;
            }
            if (opcionDestino < 1 || opcionDestino > monedas.length) {
                Consola.imprimirError("Opción inválida.");
                return;
            }
            String monedaDestino = monedas[opcionDestino - 1];
            if (monedaBase.equals(monedaDestino)) {
                Consola.imprimirError("No se puede convertir a la misma moneda.");
                return;
            }
            destinos.add(monedaDestino);
        }

        Consola.imprimirInfo("Ingresa la cantidad a convertir (puedes usar punto o coma decimal):");
        entrada = scanner.nextLine();
        double cantidad;
        try {
            cantidad = Double.parseDouble(entrada.replace(",", "."));
            if (cantidad <= 0) {
                Consola.imprimirError("Error: La cantidad debe ser mayor a cero.");
                return;
            }
        } catch (NumberFormatException e) {
            Consola.imprimirError("Error: Debe ingresar un número válido.");
            return;
        }

        for (String monedaDestino : destinos) {
            try {
                double resultado = conversor.convertir(cantidad, monedaBase, monedaDestino);
                String registro = String.format("%.2f %s equivalen a %.2f %s", cantidad, monedaBase, resultado, monedaDestino);
                Consola.imprimirResultado(registro);
                historial.add(registro);
            } catch (Exception e) {
                Consola.imprimirError("Error al convertir a " + monedaDestino + ": " + e.getMessage());
            }
        }
        System.out.println("(Presiona Enter para volver al menú principal)");
        scanner.nextLine();
    }
    public static void main(String[] args) {
        // Forzar codificación UTF-8 para mostrar caracteres especiales correctamente
        System.setProperty("file.encoding", "UTF-8");
        ApiService apiService = new ApiService();
        Conversor conversor = new Conversor(apiService);
        Scanner scanner = new Scanner(System.in);

        // Historial de conversiones
        List<String> historial = new ArrayList<>();
        String archivoHistorial = "historial.txt";
        cargarHistorial(archivoHistorial, historial);

        // Monedas favoritas
        List<String> favoritas = cargarFavoritas();

        // Obtener monedas disponibles
        String[] monedas;
        try {
            monedas = apiService.obtenerMonedasDisponibles("USD");
        } catch (Exception e) {
            Consola.imprimirError("Error al obtener monedas disponibles: " + e.getMessage());
            scanner.close();
            return;
        }

        while (true) {
            Consola.limpiarPantalla();
            Consola.imprimirTitulo("=== MENÚ PRINCIPAL ===");
            Consola.imprimirInfo("1. Convertir moneda");
            Consola.imprimirInfo("2. Conversión múltiple");
            Consola.imprimirInfo("3. Ver historial de conversiones");
            Consola.imprimirInfo("4. Configurar monedas favoritas");
            Consola.imprimirInfo("5. Salir");
            System.out.println("(Elige una opción escribiendo el número y presiona Enter)");
            String opcionMenu = scanner.nextLine();

            switch (opcionMenu) {
                case "1":
                    menuConversion(scanner, conversor, monedas, historial, favoritas, false);
                    guardarHistorial(archivoHistorial, historial);
                    break;
                case "2":
                    menuConversion(scanner, conversor, monedas, historial, favoritas, true);
                    guardarHistorial(archivoHistorial, historial);
                    break;
                case "3":
                    Consola.limpiarPantalla();
                    Consola.imprimirTitulo("--- Historial de conversiones ---");
                    if (historial.isEmpty()) {
                        Consola.imprimirInfo("No hay conversiones realizadas aún.");
                    } else {
                        historial.forEach(Consola::imprimirResultado);
                    }
                    System.out.println("(Presiona Enter para regresar al menú principal)");
                    scanner.nextLine();
                    break;
                case "4":
                    configurarFavoritas(scanner, monedas, favoritas);
                    guardarFavoritas(favoritas);
                    break;
                case "5":
                    Consola.imprimirTitulo("¡Gracias por usar el conversor!");
                    scanner.close();
                    return;
                default:
                    Consola.imprimirError("Opción inválida. Por favor, selecciona 1, 2, 3, 4 o 5.");
                    System.out.println("(Presiona Enter para volver al menú principal)");
                    scanner.nextLine();
            }
        }
    }
}
