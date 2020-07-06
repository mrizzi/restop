package org.restop.crud.deployment;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.rest.data.panache.deployment.RestDataResourceBuildItem;

import java.util.List;

public class RestopDataProcessor {

    @BuildStep
    void implementResources(CombinedIndexBuildItem index, List<RestDataResourceBuildItem> resourceBuildItems,
            BuildProducer<GeneratedBeanBuildItem> implementationsProducer) {
        ClassOutput classOutput = new GeneratedBeanGizmoAdaptor(implementationsProducer);
        RestopDataResourceImplementor implementor = new RestopDataResourceImplementor(index.getIndex());
        for (RestDataResourceBuildItem resourceBuildItem : resourceBuildItems) {
            implementor.implement(classOutput, resourceBuildItem.getResourceInfo());
        }
    }

}
