plugins {
	id("java-gradle-plugin")
}

gradlePlugin {
	plugins {
		create("versioning") {
			id = "com.nimbox.build.versioning"
			implementationClass = "com.nimbox.build.versioning.VersioningPlugin"
		}
	}
}
