package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.EnoObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class EnoModelIterator {

    private static final String JAVA_CLASS_PATH = "java.class.path";
    private static final String PATH_SEPARATOR = "path.separator";
    private static final String JDK_MODULE_PATH = "jdk.module.path";
    private Set<String> classpathWithShortenedUris;
    private ClassLoader classloader;
    private final Pattern shortenUriPattern = Pattern.compile("/[^/]");
    private final Pattern removableTokensInClassNames = Pattern.compile("\\$[0-9]+");

    public Stream<Class<? extends EnoObject>> stream(){
        return findAllPublicClassesFromPackageAndSubPackages(EnoObject.class.getPackage())
                .filter(EnoObject.class::isAssignableFrom)
                .map(clazz->clazz.asSubclass(EnoObject.class));
    }

    protected Stream<Class<?>> findAllPublicClassesFromPackageAndSubPackages(@NonNull Package packageToSearch){
        var packagePathWithSlash=packageToSearch.getName().replace(".","/");
        var searchPath="classpath*:"+packagePathWithSlash+"/**/*.class";
        try {
            return Arrays.stream((new PathMatchingResourcePatternResolver()).getResources(searchPath))
                    .map(r->findBinaryClassName(r, packagePathWithSlash))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(this::loadClass)
                    .filter(Optional::isPresent)
                    .filter(c-> Modifier.isPublic(c.get().getModifiers()))
                    .distinct()
                    .map(Optional::get);
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'accéder au classpath pour rechercher "+searchPath,e);
        }
    }

    private Optional<Class<?>> loadClass(String s) {
        try {
            return Optional.of(getClassLoader().loadClass(s));
        } catch (ClassNotFoundException e) {
            log.atWarn().log(()->"Impossible d'instancier la classe "+s+" : "+e.getMessage());
            return Optional.empty();
        }
    }

    private ClassLoader getClassLoader() {
        if (this.classloader==null){
            this.classloader=getClass().getClassLoader();
        }
        return this.classloader;
    }


    private Optional<String> findBinaryClassName(Resource resource, String packagePathWithSlash) {
        try {
            return extractPackageNameAndClassName(resource.getURI(), packagePathWithSlash).map(this::changeToBinaryName);
        } catch (IOException e) {
            log.atWarn().log(()->"Impossible d'accéder à la ressource "+resource+" : "+e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> extractPackageNameAndClassName(URI uri, String packagePathWithSlash) {

        var prefixClassPath=findPrefix(uri, getClassPathWithShortenedUris());
        if (prefixClassPath.isPresent()){
            var shortenUriPath=shortenUri(uri.getSchemeSpecificPart());
            var offsetToSearchPackage=shortenUriPath.indexOf(prefixClassPath.get())+prefixClassPath.get().length();
            var firstIndexOfPackage=shortenUriPath.substring(offsetToSearchPackage).indexOf(packagePathWithSlash);
            return firstIndexOfPackage==-1?Optional.empty():Optional.of(shortenUriPath.substring(offsetToSearchPackage+firstIndexOfPackage));
        }
        return Optional.empty();

    }

    private String changeToBinaryName(String packageAndClassName) {
        return removableTokensInClassNames
                .matcher(packageAndClassName
                        .replace("/",".")
                        .replace(".class", "")
                )
                .replaceAll("");
    }

    protected Optional<String> findPrefix(URI uri, Set<String> classpath) {

        var uriPath=shortenUri(uri.getSchemeSpecificPart());
        var prefixes= classpath.stream()
                .filter(uriPath::contains).toList();

        switch (prefixes.size()) {
            case 0 -> {
                log.atWarn().log(() -> "No resource in class path or module path for " + uri);
                return Optional.empty();
            }
            case 1 -> {
                return Optional.of(prefixes.get(0));
            }

            default -> {
                log.atWarn().log(() -> "Two many resource in class path or module path for " + uri + " : " + prefixes);
                return Optional.empty();
            }
        }
    }

    private Set<String> getClassPathWithShortenedUris() {
        if (this.classpathWithShortenedUris ==null){
            this.classpathWithShortenedUris =Stream.concat(Arrays.stream(System.getProperty(JAVA_CLASS_PATH).split(System.getProperty(PATH_SEPARATOR))),
                    Arrays.stream(Objects.requireNonNullElse(System.getProperty(JDK_MODULE_PATH),"").split(System.getProperty(PATH_SEPARATOR))))
                    .filter(Objects::nonNull)
                    .filter(Predicate.not(String::isEmpty))
                    .map(Path::of)
                    .map(Path::toUri)
                    .map(URI::getSchemeSpecificPart)
                    .map(this::shortenUri)
                    .collect(Collectors.toSet())
            ;
        }
        return this.classpathWithShortenedUris;
    }

    protected String shortenUri(@NonNull String uri) {
        var matcher=shortenUriPattern.matcher(uri);
        matcher.find();
        var startMatchingIndex=matcher.start();
        return uri.substring(startMatchingIndex);
    }

}
