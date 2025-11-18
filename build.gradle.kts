plugins {
	id("com.nimbox.tools.versioning")
}

group = "com.nimbox.tools"
version = versioning.version.get()

subprojects {
	group = rootProject.group
	version = rootProject.version
}
