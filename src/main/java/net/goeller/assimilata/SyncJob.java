package net.goeller.assimilata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class SyncJob {

	private final List<Entry> synchEntries = new ArrayList<>();
	private final SyncSet syncSet;

	public SyncJob(final SyncSet syncSet) {
		this.syncSet = syncSet;
	}

	public SyncSet getSyncSet() {
		return syncSet;
	}

	public void copy(Path from, Path to) {
		synchEntries.add(new Entry(from, to, FileOption.copy));
	}

	public void delete(Path fileToDelete) {
		synchEntries.add(new Entry(fileToDelete, FileOption.delete));
	}

	public void mkdir(Path dir) {
		synchEntries.add(new Entry(dir, FileOption.mkdir));
	}

	public List<Entry> getSynchEntries() {
		return synchEntries;
	}

	enum FileOption {
		copy, delete, mkdir;
	}

	class Entry {
		private Path path1;
		private Path path2;
		private FileOption fileOption;

		public Entry(Path path1, Path path2, FileOption fileOption) {
			this.path1 = path1;
			this.path2 = path2;
			this.fileOption = fileOption;
		}

		public Entry(Path path1, FileOption fileOption) {
			this.path1 = path1;
			this.fileOption = fileOption;
		}

		public Path getPath1() {
			return path1;
		}

		public Path getPath2() {
			return path2;
		}

		public FileOption getFileOption() {
			return fileOption;
		}

		public void execute() throws IOException {
			switch (fileOption) {
			case copy:
				Files.copy(path1, path2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
				break;
			case delete:
				Files.delete(path1);
				break;
			case mkdir:
				Files.createDirectory(path1);
				break;
			default:
				throw new IllegalStateException("Invalid file option: " + fileOption);
			}
		}

	}

}
