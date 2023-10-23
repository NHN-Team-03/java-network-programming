package com.nhnacadmemy.shttpd;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileList {

    static {
        filePath = "/Users/seungjo/NHN-Academy/java-network-programming/seungjo/src/main/java/com/nhnacadmemy/shttpd";
        refreshFileList();
    }

    private FileList() {
    }

    public static final String filePath;

    private static List<String> fileLists;


    private static void refreshFileList() {
        fileLists = Stream.of(new File(filePath).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toList());
    }

    public static List<String> getFileLists() {
        refreshFileList();
        return fileLists;
    }

    public static File findFile(String fileName) {

        if (fileLists.contains(fileName)) {
            return new File(filePath + "/" + fileName);
        } else {
            return null;
        }
    }


}
