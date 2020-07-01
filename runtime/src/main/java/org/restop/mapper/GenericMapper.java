package org.restop.mapper;

@org.mapstruct.Mapper(componentModel = "cdi")
public interface GenericMapper<DTO, ENTITY> {
    ENTITY toResource(DTO dto);
}
