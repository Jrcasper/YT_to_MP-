import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class YT_to_MP3 {

    private static final String YT_DLP_EXE = "yt-dlp.exe";
    private static final String FFMPEG_EXE = "ffmpeg.exe";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java -jar DescargadorYouTube.jar <URL de YouTube>");
            System.exit(1);
        }

        String url = args[0];

        if (!validarURL(url)) {
            System.err.println("URL de YouTube no válida");
            System.exit(1);
        }

        try {
            String rutaBase = obtenerRutaBase();
            String rutaYtDlp = Paths.get(rutaBase, YT_DLP_EXE).toString();
            String rutaFfmpeg = Paths.get(rutaBase, FFMPEG_EXE).toString();

            if (!new File(rutaYtDlp).exists() || !new File(rutaFfmpeg).exists()) {
                throw new IOException("yt-dlp.exe o ffmpeg.exe no encontrados en el directorio base.");
            }

            // Crear carpetas /temp y /musica si no existen
            String dirTemporal = Paths.get(rutaBase, "temp").toString();
            String dirMusica = Paths.get(rutaBase, "musica").toString();
            crearDirectorioSiNoExiste(dirTemporal);
            crearDirectorioSiNoExiste(dirMusica);

            // Limpiar archivos temporales antiguos
            limpiarDirectorio(dirTemporal);

            comprobarVersionYtDlp(rutaYtDlp);

            String tituloVideo = obtenerTituloVideo(url, rutaYtDlp);

            // Verificar si el archivo MP3 ya existe
            String rutaArchivoMp3 = Paths.get(dirMusica, tituloVideo + ".mp3").toString();
            File archivoMp3 = new File(rutaArchivoMp3);
            if (archivoMp3.exists()) {
                System.out.println("El archivo MP3 ya existe: " + rutaArchivoMp3);
                return; // Salir si el archivo ya existe
            }

            String rutaArchivoVideo = descargarVideo(url, rutaYtDlp, dirTemporal);

            convertirAMp3(rutaArchivoVideo, tituloVideo, rutaFfmpeg, dirMusica);

            System.out.println("¡Terminado, ahora modo sexo!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ocurrió un error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static boolean validarURL(String url) {
        System.out.println("Validando URL");
        return url.startsWith("https://www.youtube.com/") || url.startsWith("https://youtu.be/");
    }

    private static String obtenerRutaBase() throws URISyntaxException {
        File archivoJar = new File(YT_to_MP3.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        String rutaBase = archivoJar.getParentFile().getAbsolutePath();

        if (rutaBase.endsWith("bin")) {
            rutaBase = Paths.get(rutaBase, "..", "src").normalize().toString();
        }

        return rutaBase;
    }

    private static void comprobarVersionYtDlp(String rutaYtDlp) throws IOException, InterruptedException {
        ProcessBuilder constructor = new ProcessBuilder(rutaYtDlp, "--version");
        constructor.redirectErrorStream(true);
        Process proceso = constructor.start();

        BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
        String version = lector.readLine();
        int codigoSalida = proceso.waitFor();
        if (codigoSalida != 0) {
            throw new IOException("Falló la comprobación de la versión de yt-dlp. Código de salida: " + codigoSalida);
        }
        System.out.println("Versión de yt-dlp: " + version);
    }

    private static String obtenerTituloVideo(String url, String rutaYtDlp) throws IOException, InterruptedException {
        System.out.println("Obteniendo título (Importante recordarlo)");
        ProcessBuilder constructor = new ProcessBuilder(rutaYtDlp, "--get-title", url);
        constructor.redirectErrorStream(true);
        Process proceso = constructor.start();

        BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream(), "UTF-8"));
        String linea;
        String titulo = null;

        System.out.println("Salida de yt-dlp al obtener el título:");
        while ((linea = lector.readLine()) != null) {
            System.out.println(linea);  // Mostrar cada línea para diagnóstico
            if (!linea.contains("WARNING") && !linea.trim().isEmpty()) {
                titulo = linea;
            }
        }

        int estado = proceso.waitFor();
        if (estado != 0 || titulo == null) {
            throw new IOException("Error: Se detectó un problema al obtener el título del video. Código de salida: " + estado);
        }

        // Reemplazar caracteres no válidos para nombres de archivo con "_"
        String tituloLimpio = titulo.replaceAll("[^a-zA-Z0-9_\\-]", "_");

        return tituloLimpio;
    }

    private static String descargarVideo(String url, String rutaYtDlp, String dirTemporal) throws IOException, InterruptedException {
        System.out.println("Descargando video (en webm que asco)");
        ProcessBuilder constructor = new ProcessBuilder(rutaYtDlp, url, "-o", Paths.get(dirTemporal, "video.%(ext)s").toString());
        constructor.redirectErrorStream(true);
        Process proceso = constructor.start();

        BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
        String linea;
        while ((linea = lector.readLine()) != null) {
            System.out.println(linea);
        }

        int codigoSalida = proceso.waitFor();
        if (codigoSalida != 0) {
            StringBuilder salidaError = new StringBuilder();
            try (BufferedReader lectorError = new BufferedReader(new InputStreamReader(proceso.getErrorStream()))) {
                String lineaError;
                while ((lineaError = lectorError.readLine()) != null) {
                    salidaError.append(lineaError).append("\n");
                }
            }
            throw new IOException("Falló la descarga del video. Código de salida: " + codigoSalida + "\nSalida de error:\n" + salidaError.toString());
        }

        return obtenerRutaArchivoDescargado(dirTemporal);
    }

    private static String obtenerRutaArchivoDescargado(String dirTemporal) {
        File dir = new File(dirTemporal);
        for (File archivo : dir.listFiles()) {
            if (archivo.getName().startsWith("video.") && (archivo.getName().endsWith(".mp4") || archivo.getName().endsWith(".webm") || archivo.getName().endsWith(".mkv"))) {
                return archivo.getAbsolutePath();
            }
        }
        return null;
    }

    private static void convertirAMp3(String rutaArchivoVideo, String titulo, String rutaFfmpeg, String dirMusica) throws IOException, InterruptedException {
        System.out.println("Convirtiendo a mp3 (Carita fachera)");
        if (rutaArchivoVideo == null) {
            throw new IOException("No se encontró el archivo de video descargado.");
        }

        String rutaMp3 = Paths.get(dirMusica, titulo + ".mp3").toString();
        File archivoMp3 = new File(rutaMp3);
        if (archivoMp3.exists()) {
            System.out.println("El archivo MP3 ya existe: " + rutaMp3);
            return; // Salir si el archivo MP3 ya existe
        }

        ProcessBuilder constructor = new ProcessBuilder(rutaFfmpeg, "-i", rutaArchivoVideo, rutaMp3);
        constructor.redirectErrorStream(true);
        Process proceso = constructor.start();

        BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
        String linea;
        while ((linea = lector.readLine()) != null) {
            System.out.println(linea);
        }

        int codigoSalida = proceso.waitFor();
        if (codigoSalida != 0) {
            throw new IOException("Falló la conversión del video a mp3. Código de salida: " + codigoSalida);
        }

        new File(rutaArchivoVideo).delete();
    }

    private static void crearDirectorioSiNoExiste(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static void limpiarDirectorio(String dirPath) {
        File dir = new File(dirPath);
        for (File archivo : dir.listFiles()) {
            if (archivo.isFile()) {
                archivo.delete();
            }
        }
    }
}
