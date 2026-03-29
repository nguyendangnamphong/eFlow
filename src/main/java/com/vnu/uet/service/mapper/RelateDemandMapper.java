package com.vnu.uet.service.mapper;

import com.vnu.uet.domain.RelateDemand;
import com.vnu.uet.domain.RelateNode;
import com.vnu.uet.domain.SwitchNode;
import com.vnu.uet.service.dto.RelateDemandDTO;
import com.vnu.uet.service.dto.RelateNodeDTO;
import com.vnu.uet.service.dto.SwitchNodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RelateDemand} and its DTO {@link RelateDemandDTO}.
 */
@Mapper(componentModel = "spring")
public interface RelateDemandMapper extends EntityMapper<RelateDemandDTO, RelateDemand> {
    @Mapping(target = "relateNode", source = "relateNode", qualifiedByName = "relateNodeId")
    @Mapping(target = "switchNode", source = "switchNode", qualifiedByName = "switchNodeId")
    RelateDemandDTO toDto(RelateDemand s);

    @Named("relateNodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RelateNodeDTO toDtoRelateNodeId(RelateNode relateNode);

    @Named("switchNodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SwitchNodeDTO toDtoSwitchNodeId(SwitchNode switchNode);
}
