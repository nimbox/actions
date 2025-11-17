package com.nimbox.tools.versioning;

import javax.inject.Inject;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.ProviderFactory;

public abstract class VersioningPlugin implements Plugin<Project> {

	@Inject
	protected abstract ProviderFactory getProviders();

	@Override
	public void apply(Project project) {

		var versionProvider = VersionProvider.create(getProviders(), project.getRootDir());
		var version = versionProvider.get();
		project.setVersion(version);

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

	private void registerBumpTask(Project project, String name, BumpType type, String description) {

		project.getTasks().register(name, BumpVersionTask.class, (task) -> {
			task.setGroup("versioning");
			task.setDescription(description);
			task.setBumpType(type);
		});

	}

}
