package quiz.shttpd;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileList {
    public static final String ROOT_PATH = "/Users/jangjaehun/Team-03/java-network-programming";

    public static Set<String> fileSet;



    public static void addFile() {
        fileSet = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(FileList.ROOT_PATH))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    FileList.fileSet.add(path.getFileName().toString());
                }
            }
        } catch (IOException ignore) {
        }
    }
}
