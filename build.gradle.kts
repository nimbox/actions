plugins {
	id("com.nimbox.build.versioning")
}

group = "com.nimbox.build"

subprojects {
	group = rootProject.group
	version = rootProject.version
}
