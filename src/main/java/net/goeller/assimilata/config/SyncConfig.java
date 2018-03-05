package net.goeller.assimilata.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public final class SyncConfig {
  private final boolean dryRun;
  private final List<String> ignoreList;
  private final List<SyncTask> tasks;
}
