package com.nimbox.tools.versioning;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecSpec;

public abstract class BumpVersionTask extends DefaultTask {

	// Services

	private final ExecOperations execOperations;

	// Properties

	private BumpType bumpType;
	private String tagPrefix = "v";
	private String remote = "origin";

	// Constructor

	@Inject
	public BumpVersionTask(ExecOperations execOperations) {
		this.execOperations = execOperations;
	}

	// Inputs

	@Input
	public BumpType getBumpType() {
		return bumpType;
	}

	public void setBumpType(BumpType bumpType) {
		this.bumpType = bumpType;
	}

	@Input
	public String getTagPrefix() {
		return tagPrefix;
	}

	public void setTagPrefix(String tagPrefix) {
		this.tagPrefix = tagPrefix;
	}

	@Input
	public String getRemote() {
		return remote;
	}

	public void setRemote(String remote) {
		this.remote = remote;
	}

	// Actions

	@TaskAction
	public void bump() {

		check();

		String currentVersion = getProject().getVersion().toString();
		String base = currentVersion.split("-", 2)[0];

		String[] parts = base.split("\\.");
		int major, minor, patch;

		if (parts.length != 3) {
			major = 0;
			minor = 0;
			patch = 0;
			getLogger().lifecycle("No valid version tag found (got '{}'), starting from 0.0.0", base);
		} else {
			try {
				major = Integer.parseInt(parts[0]);
				minor = Integer.parseInt(parts[1]);
				patch = Integer.parseInt(parts[2]);
			} catch (NumberFormatException e) {
				major = 0;
				minor = 0;
				patch = 0;
				getLogger().lifecycle("Invalid version format '{}', starting from 0.0.0", base);
			}
		}

		String nextVersion;
		switch (bumpType) {
			case PATCH:
				nextVersion = major + "." + minor + "." + (patch + 1);
				break;
			case MINOR:
				nextVersion = major + "." + (minor + 1) + ".0";
				break;
			case MAJOR:
				nextVersion = (major + 1) + ".0.0";
				break;
			default:
				throw new IllegalStateException("Unknown bump type " + bumpType);
		}

		String tag = tagPrefix + nextVersion;

		// git tag

		execOperations.exec((ExecSpec spec) -> {
			spec.setWorkingDir(getProject().getRootDir());
			spec.commandLine("git", "tag", tag);
		});

		// git push origin tag

		execOperations.exec((ExecSpec spec) -> {
			spec.setWorkingDir(getProject().getRootDir());
			spec.commandLine("git", "push", remote, tag);
		});

		getLogger().lifecycle("Created and pushed tag {}", tag);

	}

	private void check() {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		execOperations.exec((ExecSpec spec) -> {
			spec.setWorkingDir(getProject().getRootDir());
			spec.commandLine("git", "status", "--porcelain");
			spec.setStandardOutput(stream);
			spec.setIgnoreExitValue(true);
		});

		String output = stream.toString().trim();
		if (!output.isEmpty()) {
			throw new IllegalStateException(
					"Repository is not clean. Please commit or stash your changes before creating a version tag.");
		}

	}

}
