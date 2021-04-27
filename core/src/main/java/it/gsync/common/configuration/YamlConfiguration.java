package it.gsync.common.configuration;

import it.gsync.common.configuration.sections.ConfigurationSection;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;


/*
    Velocity doesn't have any configuration utils so i created one
 */

@Getter
public class YamlConfiguration extends Yaml {


    private File file;
    private Map<String,Object> configMap;
    private Map<String, ConfigurationSection> sections;

    public YamlConfiguration(File path,String fileName,String rename) {
        file = new File(path,rename);
        sections = new LinkedHashMap<>();
        if(!file.exists()) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            try {
                Files.copy(inputStream, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!fileName.equals(rename)) {
                file.renameTo(new File(path,rename));
            }
        }
        try {
            InputStream fileStream = new FileInputStream(file);
            configMap = (Map<String, Object>) load(fileStream);
            for(Map.Entry<String,Object> entry : configMap.entrySet()) {
                if(entry.getValue() instanceof LinkedHashMap) {
                    sections.put(entry.getKey(),new ConfigurationSection(entry.getKey(),(Map<String, Object>) entry.getValue()));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            dump(configMap,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection getSection(String path) {
        if (path.contains(".")) {
            String[] array = path.split("\\.");
            String[] sectionNames = Arrays.copyOfRange(array,1,array.length);
            ConfigurationSection section = sections.get(array[0]);
            if(section == null) {
                throw  new IllegalArgumentException("This is not a configuration section");
            }
            for(String sec : sectionNames) {
                section = section.getNestedSections().get(sec);
            }
            return section;
        } else {
            ConfigurationSection section = sections.get(path);
            if(section == null) {
                throw  new IllegalArgumentException("This is not a configuration section");
            }
            return section;
        }
    }


    public List<ConfigurationSection> getSections(String path) {
        List<ConfigurationSection> toRet = new ArrayList<>();
        if (path.contains(".")) {
            String[] array = path.split("\\.");
            String[] sectionNames = Arrays.copyOfRange(array,1,array.length);
            ConfigurationSection section = sections.get(array[0]);
            if(section == null) {
                throw  new IllegalArgumentException("This is not a configuration section");
            }
            for(String sec : sectionNames) {
                section = section.getNestedSections().get(sec);
            }
            toRet.addAll(section.getNestedSections().values());
            return toRet;
        } else if(path.length() == 0) {
            toRet.addAll(sections.values());
            return toRet;
        } else {
            ConfigurationSection section = sections.get(path);
            if(section == null) {
                throw  new IllegalArgumentException("This is not a configuration section");
            }
            toRet.addAll(section.getNestedSections().values());
            return toRet;
        }
    }

    public <T> T get(String path,Class<T> clazz) {
        if(path.contains(".")) {
            String[] array = path.split("\\.");
            String[] sectionNames = Arrays.copyOfRange(array,1,array.length - 1);
            String actualObjPath = array[array.length - 1];
            ConfigurationSection section = sections.get(array[0]);
            for(String sec : sectionNames) {
                section = section.getNestedSections().get(sec);
            }
            if(section.getConfigMap().get(actualObjPath).getClass().isInstance(clazz)) {
                throw new ClassCastException("Path is ok,but the object doesn't correspond with the class you want to cast it with");
            }
            return (T) section.getConfigMap().get(actualObjPath);
        } else {
            if(configMap.get(path).getClass().isInstance(clazz)) {
                throw new ClassCastException("the object doesn't correspond with the class you want to cast it with");
            }
            return (T) configMap.get(path);
        }
    }

    public Object get(String path) {
        if(path.contains(".")) {
            String[] array = path.split("\\.");
            String[] sectionNames = Arrays.copyOfRange(array,1,array.length - 1);
            String actualObjPath = array[array.length - 1];
            ConfigurationSection section = sections.get(array[0]);
            for(String sec : sectionNames) {
                section = section.getNestedSections().get(sec);
            }
            return section.getConfigMap().get(actualObjPath);
        } else {
            return configMap.get(path);
        }
    }


}
