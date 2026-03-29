package com.vnu.uet.service.mapper;

import com.vnu.uet.domain.MapForm;
import com.vnu.uet.domain.Variable;
import com.vnu.uet.service.dto.MapFormDTO;
import com.vnu.uet.service.dto.VariableDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Variable} and its DTO {@link VariableDTO}.
 */
@Mapper(componentModel = "spring")
public interface VariableMapper extends EntityMapper<VariableDTO, Variable> {
    @Mapping(target = "mapForm", source = "mapForm", qualifiedByName = "mapFormId")
    VariableDTO toDto(Variable s);

    @Named("mapFormId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MapFormDTO toDtoMapFormId(MapForm mapForm);
}
