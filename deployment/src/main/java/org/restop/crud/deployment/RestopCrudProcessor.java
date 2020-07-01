package org.restop.crud.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class RestopCrudProcessor {

    private static final String FEATURE = "restop";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    ReflectiveClassBuildItem reflectionPagination() {
        return new ReflectiveClassBuildItem(false, true, "org.restop.pagination.Pagination");
    }

    @BuildStep
    ReflectiveClassBuildItem reflectionMeta() {
        return new ReflectiveClassBuildItem(false, true, "org.restop.pagination.Meta");
    }

    @BuildStep
    ReflectiveClassBuildItem reflectionLinks() {
        return new ReflectiveClassBuildItem(false, true, "org.restop.pagination.Links");
    }

    @BuildStep
    NativeImageProxyDefinitionBuildItem uriInfoProxies() {
        return new NativeImageProxyDefinitionBuildItem("javax.ws.rs.core.UriInfo");
    }
}
