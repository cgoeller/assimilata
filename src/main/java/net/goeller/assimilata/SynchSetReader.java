package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class SynchSetReader {

	ObjectMapper mapper = new ObjectMapper();

	public SynchSetReader() {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public SynchSet read(Path file) throws IOException {
		return mapper.readValue(file.toFile(), SynchSet.class);
	}

	public SynchSet read(String json) throws IOException {
		return mapper.readValue(json, SynchSet.class);
	}

	public String write(SynchSet set) throws IOException {
		return mapper.writeValueAsString(set);
	}
}
