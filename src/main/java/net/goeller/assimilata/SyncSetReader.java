package net.goeller.assimilata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Path;

public class SyncSetReader {

    private ObjectMapper jsonMapper = new ObjectMapper();
    private ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public SyncSetReader() {
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public SyncSet read(Path file) throws IOException {
        if (file.endsWith(".yml")) {
            return yamlMapper.readValue(file.toFile(), SyncSet.class);

        } else {
            return jsonMapper.readValue(file.toFile(), SyncSet.class);
        }
    }

    public SyncSet readJson(String content) throws IOException {
        return jsonMapper.readValue(content, SyncSet.class);
    }

    public SyncSet readYaml(String content) throws IOException {
        return yamlMapper.readValue(content, SyncSet.class);
    }

    public String writeJson(SyncSet set) throws IOException {
        return jsonMapper.writeValueAsString(set);
    }
}
