package com.vnu.uet.service.mapper;

import com.vnu.uet.domain.Flow;
import com.vnu.uet.domain.RelateNode;
import com.vnu.uet.domain.SwitchNode;
import com.vnu.uet.service.dto.FlowDTO;
import com.vnu.uet.service.dto.RelateNodeDTO;
import com.vnu.uet.service.dto.SwitchNodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SwitchNode} and its DTO {@link SwitchNodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface SwitchNodeMapper extends EntityMapper<SwitchNodeDTO, SwitchNode> {
    @Mapping(target = "flow", source = "flow", qualifiedByName = "flowId")
    @Mapping(target = "relateNode", source = "relateNode", qualifiedByName = "relateNodeId")
    SwitchNodeDTO toDto(SwitchNode s);

    @Named("flowId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FlowDTO toDtoFlowId(Flow flow);

    @Named("relateNodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RelateNodeDTO toDtoRelateNodeId(RelateNode relateNode);
}
