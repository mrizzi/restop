package org.restop.openapi;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Operation;

public class ChangeOperationFilter implements OASFilter {

    @Override
    public Operation filterOperation(Operation operation) {
        operation.setSummary("Marco - " + operation.getSummary());
        return operation;
    }

}
