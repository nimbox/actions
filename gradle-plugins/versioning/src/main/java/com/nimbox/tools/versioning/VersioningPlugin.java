package com.nimbox.tools.versioning;

import javax.inject.Inject;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

public abstract class VersioningPlugin implements Plugin<Project> {

	@Inject
	protected abstract ProviderFactory getProviders();

	@Override
	public void apply(Project project) {

		Provider<String> gitVersionProvider = getProviders()
				.exec(spec -> {
					spec.setWorkingDir(project.getRootDir());
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

					if (isVersion(parts[0])) {
						return trimmed;
					} else {
						return "0.0.0-" + trimmed;
					}

				});

		// You probably *do* want this eagerly to set project.version:

		String gitVersion = gitVersionProvider.get();
		project.setVersion(gitVersion);

		// Task printVersion

		project.getTasks().register("version", task -> {
			task.setGroup("versioning");
			task.setDescription("Prints project.version derived from git");
			task.doLast(t -> System.out.println(project.getVersion()));
		});

		// Tasks versionPatch, versionMinor, versionMajor

		registerBumpTask(project,
				"versionPatch",
				BumpType.PATCH,
				"Create and push a patch version tag from the current version");

		registerBumpTask(project,
				"versionMinor",
				BumpType.MINOR,
				"Create and push a minor version tag from the current version");

		registerBumpTask(project,
				"versionMajor",
				BumpType.MAJOR,
				"Create and push a major version tag from the current version");

	}

	private boolean isVersion(String version) {
		return version.matches("^\\d+\\.\\d+\\.\\d+$");
	}

	private void registerBumpTask(Project project, String name, BumpType type, String description) {

		project.getTasks().register(name, BumpVersionTask.class, (task) -> {
			task.setGroup("versioning");
			task.setDescription(description);
			task.setBumpType(type);
		});

	}

}
