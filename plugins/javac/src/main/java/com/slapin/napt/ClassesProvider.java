package com.slapin.napt;

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Options;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;

class ClassesProvider {

    static Set<String> getClassNames(Context context) {
        final URLClassLoader classLoader = (URLClassLoader) context.get(JavaFileManager.class)
                .getClassLoader(StandardLocation.CLASS_PATH);

        final String kotlinClassesDir = Options.instance(context).get("KotlinClassesDir");

        final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        try (final Stream<String> stream = Arrays.stream(classLoader.getURLs())
                .filter(it -> !it.getFile().endsWith("jar") && it.getFile().contains(kotlinClassesDir))
                .map(url -> {
                    String file = url.getFile();
                    if (isWindows) {
                        file = file.replaceFirst("^/(.:/)", "$1");
                    }
                    return file;
                })
                .map(Paths::get)
                .filter(Files::exists)
                .flatMap(ClassesProvider::walk)
                .map(Path::toString)
                .filter(it -> it.endsWith(".class") && !it.contains("$"))
                .map(ClassesProvider::toClassName)) {
            return stream.collect(Collectors.toSet());
        }
    }

    private static Stream<Path> walk(Path path) {
        final Path normalized = path.normalize();
        try {
            return Files
                    .find(normalized, Integer.MAX_VALUE, (o, attrs) -> attrs.isRegularFile())
                    .map(normalized::relativize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toClassName(String path) {
        return path
                .substring(0, path.length() - ".class".length())
                .replace(File.separatorChar, '.');
    }
}
