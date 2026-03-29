package com.vnu.uet.service.mapper;

import com.vnu.uet.domain.Flow;
import com.vnu.uet.service.dto.FlowDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Flow} and its DTO {@link FlowDTO}.
 */
@Mapper(componentModel = "spring")
public interface FlowMapper extends EntityMapper<FlowDTO, Flow> {}
