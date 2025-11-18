package com.nimbox.tools.versioning;

import java.io.File;

import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

public abstract class VersioningExtension {

    private final ProviderFactory providers;
    private final File rootDir;

    public VersioningExtension(ProviderFactory providers, File rootDir) {
        this.providers = providers;
        this.rootDir = rootDir;
    }

    public Provider<String> getVersion() {
        return VersionProvider.create(providers, rootDir);
    }

}
