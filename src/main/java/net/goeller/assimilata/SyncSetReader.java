package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class SyncSetReader {

	ObjectMapper mapper = new ObjectMapper();

	public SyncSetReader() {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public SyncSet read(Path file) throws IOException {
		return mapper.readValue(file.toFile(), SyncSet.class);
	}

	public SyncSet read(String json) throws IOException {
		return mapper.readValue(json, SyncSet.class);
	}

	public String write(SyncSet set) throws IOException {
		return mapper.writeValueAsString(set);
	}
}
