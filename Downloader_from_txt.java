import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Downloader_from_txt {

    public static void main(String[] args) {
        // Ruta del archivo URLs.txt
        String rutaArchivo = "URLs.txt";
        
        // Ruta del jar YT_To_MP3.jar
        String rutaJar = "YT_to_MP3.jar";
        
        // Leer el archivo URLs.txt
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String url;
            while ((url = br.readLine()) != null) {
                // Ejecutar el jar con cada URL
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", rutaJar, url);
                processBuilder.directory(new File(System.getProperty("user.dir")));
                Process process = processBuilder.start();
                
                // Capturar la salida del proceso
                BufferedReader lectorSalida = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader lectorError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                
                String linea;
                while ((linea = lectorSalida.readLine()) != null) {
                    System.out.println(linea);
                }
                while ((linea = lectorError.readLine()) != null) {
                    System.err.println(linea);
                }
                
                // Esperar a que termine el proceso
                int codigoSalida = process.waitFor();
                if (codigoSalida == 0) {
                    System.out.println("Descarga completada para: " + url);
                } else {
                    System.out.println("Error al descargar: " + url);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
