plugins {
	id("java-gradle-plugin")
}

gradlePlugin {
	plugins {
		create("versioning") {
			id = "com.nimbox.tools.versioning"
			implementationClass = "com.nimbox.tools.versioning.VersioningPlugin"
		}
	}
}
