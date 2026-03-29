package com.vnu.uet.service.mapper;

import com.vnu.uet.domain.MapForm;
import com.vnu.uet.domain.Node;
import com.vnu.uet.service.dto.MapFormDTO;
import com.vnu.uet.service.dto.NodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MapForm} and its DTO {@link MapFormDTO}.
 */
@Mapper(componentModel = "spring")
public interface MapFormMapper extends EntityMapper<MapFormDTO, MapForm> {
    @Mapping(target = "node", source = "node", qualifiedByName = "nodeId")
    MapFormDTO toDto(MapForm s);

    @Named("nodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NodeDTO toDtoNodeId(Node node);
}
