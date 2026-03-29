package com.vnu.uet.service.mapper;

import com.vnu.uet.domain.Flow;
import com.vnu.uet.domain.Node;
import com.vnu.uet.service.dto.FlowDTO;
import com.vnu.uet.service.dto.NodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Node} and its DTO {@link NodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface NodeMapper extends EntityMapper<NodeDTO, Node> {
    @Mapping(target = "flow", source = "flow", qualifiedByName = "flowId")
    NodeDTO toDto(Node s);

    @Named("flowId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FlowDTO toDtoFlowId(Flow flow);
}
