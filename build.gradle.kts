plugins {
	id("com.nimbox.tools.versioning")
}

group = "com.nimbox.tools"

subprojects {
	group = rootProject.group
	version = rootProject.version
}
