import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Main {
    private static final String YT_DLP_EXE = "yt-dlp.exe";
    private static final String FFMPEG_EXE = "ffmpeg.exe";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar YouTubeDownloader.jar <YouTube URL>");
            System.exit(1);
        }

        String url = args[0];

        if (!validateURL(url)) {
            System.err.println("Invalid YouTube URL");
            System.exit(1);
        }

        try {
            String basePath = getBasePath();
            String ytDlpPath = Paths.get(basePath, YT_DLP_EXE).toString();
            String ffmpegPath = Paths.get(basePath, FFMPEG_EXE).toString();

            if (!new File(ytDlpPath).exists() || !new File(ffmpegPath).exists()) {
                throw new IOException("yt-dlp.exe or ffmpeg.exe not found in base directory.");
            }

            // Crear carpetas /temp y /musica si no existen
            String tempDir = Paths.get(basePath, "temp").toString();
            String musicDir = Paths.get(basePath, "musica").toString();
            createDirectoryIfNotExists(tempDir);
            createDirectoryIfNotExists(musicDir);

            // Limpiar archivos temporales antiguos
            cleanDirectory(tempDir);

            checkYtDlpVersion(ytDlpPath);

            String videoTitle = getVideoTitle(url, ytDlpPath);

            String videoFilePath = downloadVideo(url, ytDlpPath, tempDir);

            convertToMp3(videoFilePath, videoTitle, ffmpegPath, musicDir);

            System.out.println("Terminado, ahora modo sexo!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error occurred: " + e.getMessage());
            System.exit(1);
        }
    }

    private static boolean validateURL(String url) {
        System.out.println("Validando URL");
        return url.startsWith("https://www.youtube.com/") || url.startsWith("https://youtu.be/");
    }

    private static String getBasePath() throws URISyntaxException {
        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        String basePath = jarFile.getParentFile().getAbsolutePath();

        if (basePath.endsWith("bin")) {
            basePath = Paths.get(basePath, "..", "src").normalize().toString();
        }

        return basePath;
    }

    private static void checkYtDlpVersion(String ytDlpPath) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(ytDlpPath, "--version");
        builder.redirectErrorStream(true);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String version = reader.readLine();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Failed to check yt-dlp version. Exit code: " + exitCode);
        }
        System.out.println("yt-dlp version: " + version);
    }

    private static String getVideoTitle(String url, String ytDlpPath) throws IOException, InterruptedException {
        System.out.println("Obteniendo titulo (Importante recordarlo)");
        ProcessBuilder builder = new ProcessBuilder(ytDlpPath, "--get-title", url);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String title = reader.readLine().replace(" ", "_").replaceAll("[^a-zA-Z0-9_\\-]", "");

        process.waitFor();
        return title;
    }

    private static String downloadVideo(String url, String ytDlpPath, String tempDir) throws IOException, InterruptedException {
        System.out.println("Descargando video(en webm que asco)");
        ProcessBuilder builder = new ProcessBuilder(ytDlpPath, url, "-o", Paths.get(tempDir, "video.%(ext)s").toString());
        builder.redirectErrorStream(true);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorOutput.append(errorLine).append("\n");
                }
            }
            throw new IOException("Failed to download video. Exit code: " + exitCode + "\nError output:\n" + errorOutput.toString());
        }

        return getDownloadedVideoPath(tempDir);
    }

    private static String getDownloadedVideoPath(String tempDir) {
        File dir = new File(tempDir);
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("video.") && (file.getName().endsWith(".mp4") || file.getName().endsWith(".webm"))) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    private static void convertToMp3(String videoFilePath, String title, String ffmpegPath, String musicDir) throws IOException, InterruptedException {
        System.out.println("Convirtiendo a mp3 (Carita fachera)");
        if (videoFilePath == null) {
            throw new IOException("Downloaded video file not found.");
        }

        ProcessBuilder builder = new ProcessBuilder(ffmpegPath, "-i", videoFilePath, Paths.get(musicDir, title + ".mp3").toString());
        builder.redirectErrorStream(true);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Failed to convert video to mp3. Exit code: " + exitCode);
        }

        new File(videoFilePath).delete();
    }

    private static void createDirectoryIfNotExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static void cleanDirectory(String dirPath) {
        File dir = new File(dirPath);
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }
}
