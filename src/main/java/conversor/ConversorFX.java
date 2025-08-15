package conversor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import com.google.gson.Gson;

public class ConversorFX extends Application {
    // Historial y favoritas
    private List<String> historial = new ArrayList<>();
    private List<String> favoritas = new ArrayList<>();
    private String[] monedas;
    private ApiService apiService = new ApiService();
    private Conversor conversor = new Conversor(apiService);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Conversor de Monedas");
        cargarHistorial();
        cargarFavoritas();
        try {
            monedas = apiService.obtenerMonedasDisponibles("USD");
        } catch (Exception e) {
            monedas = new String[]{"USD", "EUR", "MXN", "JPY", "ARS", "BRL"};
        }
        mostrarMenuPrincipal(primaryStage);
    }

    private void mostrarMenuPrincipal(Stage stage) {
        Label titulo = new Label("Conversor de Monedas");
        titulo.setStyle("-fx-font-size: 24px; -fx-text-fill: #2196F3; -fx-font-weight: bold;");

        Button btnConvertir = new Button("Conversión simple");
        Button btnMultiple = new Button("Conversión múltiple");
        Button btnHistorial = new Button("Ver historial");
        Button btnFavoritas = new Button("Configurar favoritas");

        VBox layout = new VBox(20, titulo, btnConvertir, btnMultiple, btnHistorial, btnFavoritas);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #F5F5F5;");

        btnConvertir.setOnAction(e -> mostrarConversion(stage, false));
        btnMultiple.setOnAction(e -> mostrarConversion(stage, true));
        btnHistorial.setOnAction(e -> mostrarHistorial(stage));
        btnFavoritas.setOnAction(e -> mostrarConfigFavoritas(stage));

        stage.setScene(new Scene(layout, 400, 400));
        stage.show();
    }

    private void mostrarConversion(Stage stage, boolean multiple) {
        Label titulo = new Label(multiple ? "Conversión múltiple" : "Conversión simple");
        titulo.setStyle("-fx-font-size: 20px; -fx-text-fill: #2196F3; -fx-font-weight: bold;");

        ComboBox<String> comboOrigen = new ComboBox<>();
        comboOrigen.setPromptText("Moneda de origen");
        comboOrigen.getItems().clear();
        if (favoritas.isEmpty()) {
            comboOrigen.getItems().addAll(Arrays.asList(monedas));
        } else {
            comboOrigen.getItems().addAll(favoritas);
        }

        ListView<String> listaDestino = new ListView<>();
        listaDestino.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaDestino.getItems().addAll(monedas);
        listaDestino.setPrefHeight(120);

        ComboBox<String> comboDestino = new ComboBox<>();
        comboDestino.setPromptText("Moneda de destino");
        comboDestino.getItems().addAll(monedas);

        TextField campoCantidad = new TextField();
        campoCantidad.setPromptText("Cantidad a convertir");

        Button btnConvertir = new Button("Convertir");
        Button btnVolver = new Button("Volver al menú");

        Label resultado = new Label("");
        resultado.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox layout;
        if (multiple) {
            layout = new VBox(15, titulo, comboOrigen, listaDestino, campoCantidad, btnConvertir, btnVolver, resultado);
        } else {
            layout = new VBox(15, titulo, comboOrigen, comboDestino, campoCantidad, btnConvertir, btnVolver, resultado);
        }
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F5F5F5;");

        btnConvertir.setOnAction(e -> {
            String origen = comboOrigen.getValue();
            if (origen == null) {
                mostrarError(resultado, "Selecciona moneda de origen.");
                return;
            }
            String cantidadStr = campoCantidad.getText();
            double cantidad;
            try {
                cantidad = Double.parseDouble(cantidadStr.replace(",", "."));
                if (cantidad <= 0) throw new NumberFormatException();
            } catch (Exception ex) {
                mostrarError(resultado, "Cantidad inválida.");
                return;
            }
            if (multiple) {
                List<String> destinos = listaDestino.getSelectionModel().getSelectedItems();
                if (destinos.isEmpty()) {
                    mostrarError(resultado, "Selecciona al menos una moneda de destino.");
                    return;
                }
                StringBuilder res = new StringBuilder();
                for (String destino : destinos) {
                    if (origen.equals(destino)) continue;
                    try {
                        double valor = conversor.convertir(cantidad, origen, destino);
                        String registro = String.format("%.2f %s = %.2f %s", cantidad, origen, valor, destino);
                        res.append(registro).append("\n");
                        historial.add(registro);
                    } catch (Exception ex) {
                        res.append("Error con ").append(destino).append(": ").append(ex.getMessage()).append("\n");
                    }
                }
                mostrarExito(resultado, res.toString());
                guardarHistorial();
            } else {
                String destino = comboDestino.getValue();
                if (destino == null || origen.equals(destino)) {
                    mostrarError(resultado, "Selecciona moneda de destino válida.");
                    return;
                }
                try {
                    double valor = conversor.convertir(cantidad, origen, destino);
                    String registro = String.format("%.2f %s = %.2f %s", cantidad, origen, valor, destino);
                    mostrarExito(resultado, registro);
                    historial.add(registro);
                    guardarHistorial();
                } catch (Exception ex) {
                    mostrarError(resultado, "Error: " + ex.getMessage());
                }
            }
        });

        btnVolver.setOnAction(e -> mostrarMenuPrincipal(stage));

        stage.setScene(new Scene(layout, 450, 500));
        stage.show();
    }

    private void mostrarHistorial(Stage stage) {
        Label titulo = new Label("Historial de conversiones");
        titulo.setStyle("-fx-font-size: 20px; -fx-text-fill: #2196F3; -fx-font-weight: bold;");
        ListView<String> lista = new ListView<>();
        lista.getItems().addAll(historial);
        lista.setPrefHeight(200);
        Button btnVolver = new Button("Volver al menú");
        VBox layout = new VBox(15, titulo, lista, btnVolver);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F5F5F5;");
        btnVolver.setOnAction(e -> mostrarMenuPrincipal(stage));
        stage.setScene(new Scene(layout, 450, 350));
        stage.show();
    }

    private void mostrarConfigFavoritas(Stage stage) {
        Label titulo = new Label("Configura tus monedas favoritas (máx. 5)");
        titulo.setStyle("-fx-font-size: 20px; -fx-text-fill: #2196F3; -fx-font-weight: bold;");
        ListView<String> lista = new ListView<>();
        lista.getItems().addAll(monedas);
        lista.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lista.setPrefHeight(200);
        Button btnGuardar = new Button("Guardar favoritas");
        Button btnVolver = new Button("Volver al menú");
        Label info = new Label("");
        VBox layout = new VBox(15, titulo, lista, btnGuardar, btnVolver, info);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F5F5F5;");
        btnGuardar.setOnAction(e -> {
            List<String> seleccionadas = lista.getSelectionModel().getSelectedItems();
            if (seleccionadas.size() > 5) {
                info.setText("Solo puedes seleccionar hasta 5 favoritas.");
                info.setTextFill(Color.RED);
                return;
            }
            favoritas.clear();
            favoritas.addAll(seleccionadas);
            guardarFavoritas();
            info.setText("Favoritas guardadas: " + favoritas);
            info.setTextFill(Color.GREEN);
        });
        btnVolver.setOnAction(e -> mostrarMenuPrincipal(stage));
        stage.setScene(new Scene(layout, 450, 400));
        stage.show();
    }

    private void mostrarError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setTextFill(Color.RED);
    }
    private void mostrarExito(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setTextFill(Color.GREEN);
    }

    private void cargarHistorial() {
        File f = new File("historial.txt");
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                historial.add(linea);
            }
        } catch (IOException e) {}
    }
    private void guardarHistorial() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("historial.txt"))) {
            for (String linea : historial) {
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {}
    }
    private void cargarFavoritas() {
        File f = new File("favoritas.json");
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            Gson gson = new Gson();
            String[] arr = gson.fromJson(br, String[].class);
            favoritas.clear();
            favoritas.addAll(Arrays.asList(arr));
        } catch (IOException e) {}
    }
    private void guardarFavoritas() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("favoritas.json"))) {
            Gson gson = new Gson();
            bw.write(gson.toJson(favoritas));
        } catch (IOException e) {}
    }

    public static void main(String[] args) {
        launch(args);
    }
}
