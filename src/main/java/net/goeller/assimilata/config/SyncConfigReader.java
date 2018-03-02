package net.goeller.assimilata.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Path;

public final class SyncConfigReader {

  private ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  public SyncConfig readFile(Path file) throws IOException {
    return yamlMapper.readValue(file.toFile(), SyncConfig.class);
  }

  public SyncConfig readString(String content) throws IOException {
    return yamlMapper.readValue(content, SyncConfig.class);
  }

  public String write(SyncConfig set) throws IOException {
    return yamlMapper.writeValueAsString(set);
  }
}
