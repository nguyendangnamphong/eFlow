package com.vnu.uet.service.mapper;

import com.vnu.uet.domain.Node;
import com.vnu.uet.domain.Performer;
import com.vnu.uet.service.dto.NodeDTO;
import com.vnu.uet.service.dto.PerformerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Performer} and its DTO {@link PerformerDTO}.
 */
@Mapper(componentModel = "spring")
public interface PerformerMapper extends EntityMapper<PerformerDTO, Performer> {
    @Mapping(target = "node", source = "node", qualifiedByName = "nodeId")
    PerformerDTO toDto(Performer s);

    @Named("nodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NodeDTO toDtoNodeId(Node node);
}
