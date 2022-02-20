package it.gsync.common.dependencies;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.MalformedURLException;
import java.net.URL;

@RequiredArgsConstructor
@Getter
public class Dependency {

    private final String group,artifact,version,checkClass;


    public URL toMavenURL() throws MalformedURLException {
        String id = group.replace('.','/') + "/" + artifact + "/" + version + "/";
        URL url = new URL("https://repo1.maven.org/maven2/" + id + getFileName());
        return url;
    }

    public String getFileName() {
        return artifact + "-" + version + ".jar";
    }

}
