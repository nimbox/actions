package com.nimbox.tools.versioning;

import java.io.File;

import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

public class VersionProvider {

	public static Provider<String> create(ProviderFactory providers, File rootDir) {

		return providers
				.exec(spec -> {
					spec.setWorkingDir(rootDir);
					spec.commandLine(
							"git",
							"describe",
							"--tags",
							"--match", "v*",
							"--always",
							"--dirty",
							"--first-parent");
				})
				.getStandardOutput()
				.getAsText()
				.map(raw -> {
					String trimmed = raw.trim();
					if (trimmed.startsWith("v")) {
						trimmed = trimmed.substring(1);
					}

					if (trimmed.isEmpty()) {
						return "0.0.0";
					}

					var parts = trimmed.split("-", 2);

					if (parts[0].matches("^\\d+\\.\\d+\\.\\d+$")) {
						return trimmed;
					} else {
						return "0.0.0-" + trimmed;
					}
				});

	}

}
