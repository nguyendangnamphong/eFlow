package com.vnu.uet.service.mapper;

import com.vnu.uet.domain.Flow;
import com.vnu.uet.domain.Node;
import com.vnu.uet.domain.RelateNode;
import com.vnu.uet.service.dto.FlowDTO;
import com.vnu.uet.service.dto.NodeDTO;
import com.vnu.uet.service.dto.RelateNodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RelateNode} and its DTO {@link RelateNodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface RelateNodeMapper extends EntityMapper<RelateNodeDTO, RelateNode> {
    @Mapping(target = "flow", source = "flow", qualifiedByName = "flowId")
    @Mapping(target = "node", source = "node", qualifiedByName = "nodeId")
    RelateNodeDTO toDto(RelateNode s);

    @Named("flowId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FlowDTO toDtoFlowId(Flow flow);

    @Named("nodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NodeDTO toDtoNodeId(Node node);
}
