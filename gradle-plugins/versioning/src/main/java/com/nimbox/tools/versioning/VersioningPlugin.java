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

		// Extension versioning

		project.getExtensions()
				.create("versioning", VersioningExtension.class, getProviders(), project.getRootDir());

		// Task printVersion

		project.getTasks().register("version", task -> {
			task.setGroup("versioning");
			task.setDescription("Prints the project version");
			task.doLast(t -> {
				t.getLogger().lifecycle(project.getVersion().toString());
			});
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
