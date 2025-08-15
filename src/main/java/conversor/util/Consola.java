package conversor.util;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class Consola {
    static {
        AnsiConsole.systemInstall();
    }

    public static void limpiarPantalla() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void imprimirTitulo(String texto) {
        System.out.println(Ansi.ansi().fgBrightBlue().a(texto).reset());
    }

    public static void imprimirResultado(String texto) {
        System.out.println(Ansi.ansi().fgBrightGreen().a(texto).reset());
    }

    public static void imprimirError(String texto) {
        System.out.println(Ansi.ansi().fgRed().a(texto).reset());
    }

    public static void imprimirInfo(String texto) {
        System.out.println(Ansi.ansi().fgBrightCyan().a(texto).reset());
    }
}
