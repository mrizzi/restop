package org.restop.crud.deployment;

import io.quarkus.deployment.util.HashUtil;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.rest.data.panache.deployment.RestDataResourceInfo;
import io.quarkus.rest.data.panache.deployment.methods.ListPaginatedMethodImplementor;
import io.quarkus.rest.data.panache.deployment.methods.MethodImplementor;
import io.quarkus.rest.data.panache.deployment.properties.MethodPropertiesAccessor;
import io.quarkus.rest.data.panache.deployment.properties.ResourcePropertiesAccessor;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

import javax.ws.rs.Path;
import java.util.Arrays;
import java.util.List;

class RestopDataResourceImplementor {

    private static final Logger LOGGER = Logger.getLogger(RestopDataResourceImplementor.class);

    private static final List<MethodImplementor> STANDARD_METHOD_IMPLEMENTORS = Arrays.asList(
            new ListPaginatedMethodImplementor());

    private static final List<MethodImplementor> HAL_METHOD_IMPLEMENTORS = Arrays.asList();

    private final IndexView index;

    private final ResourcePropertiesAccessor resourcePropertiesAccessor;

    private final MethodPropertiesAccessor methodPropertiesAccessor;
    public RestopDataResourceImplementor(IndexView index) {
        this.index = index;
        this.resourcePropertiesAccessor = new ResourcePropertiesAccessor(index);
        this.methodPropertiesAccessor = new MethodPropertiesAccessor(index);
    }

    void implement(ClassOutput classOutput, RestDataResourceInfo resourceInfo) {
        String resourceInterfaceName = resourceInfo.getClassInfo().toString();
        String implementationClassName = resourceInterfaceName + "RestopImpl_" + HashUtil.sha1(resourceInterfaceName);
        LOGGER.tracef("Starting generation of '%s'", implementationClassName);
        ClassCreator classCreator = ClassCreator.builder()
                .classOutput(classOutput)
                .className(implementationClassName)
                .interfaces(resourceInterfaceName)
                .build();
        classCreator.addAnnotation(Path.class)
                .addValue("value", resourcePropertiesAccessor.path(resourceInfo.getClassInfo()));
        implementMethods(classCreator, resourceInfo);
        classCreator.close();
        LOGGER.tracef("Completed generation of '%s'", implementationClassName);
    }

    private void implementMethods(ClassCreator classCreator, RestDataResourceInfo resourceInfo) {
        for (MethodImplementor methodImplementor : STANDARD_METHOD_IMPLEMENTORS) {
            methodImplementor.implement(classCreator, index, methodPropertiesAccessor, resourceInfo);
        }
        if (resourcePropertiesAccessor.isHal(resourceInfo.getClassInfo())) {
            for (MethodImplementor methodImplementor : HAL_METHOD_IMPLEMENTORS) {
                methodImplementor.implement(classCreator, index, methodPropertiesAccessor, resourceInfo);
            }
        }
    }
}
