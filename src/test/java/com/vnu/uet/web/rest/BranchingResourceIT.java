package com.vnu.uet.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.RelateDemand;
import com.vnu.uet.domain.SwitchNode;
import com.vnu.uet.repository.RelateDemandRepository;
import com.vnu.uet.repository.SwitchNodeRepository;
import com.vnu.uet.service.dto.RelateDemandDTO;
import com.vnu.uet.service.dto.SwitchNodeDTO;
import com.vnu.uet.service.mapper.RelateDemandMapper;
import com.vnu.uet.service.mapper.SwitchNodeMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BranchingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BranchingResourceIT {

    private static final String API_URL = "/api";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SwitchNodeRepository switchNodeRepository;

    @Autowired
    private RelateDemandRepository relateDemandRepository;

    @Autowired
    private SwitchNodeMapper switchNodeMapper;

    @Autowired
    private RelateDemandMapper relateDemandMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBranchingMockMvc;

    private SwitchNode switchNode;

    @BeforeEach
    public void initTest() {
        switchNode = new SwitchNode();
        switchNode = switchNodeRepository.saveAndFlush(switchNode);
    }

    @Test
    @Transactional
    void configSwitchNode() throws Exception {
        switchNode.formId("form123").variableId("var456");
        SwitchNodeDTO switchNodeDTO = switchNodeMapper.toDto(switchNode);

        restBranchingMockMvc
            .perform(
                post(API_URL + "/switch/{switchId}/config", switchNode.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(switchNodeDTO))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.formId").value("form123"));
    }

    @Test
    @Transactional
    void saveRelateDemand() throws Exception {
        RelateDemand relateDemand = new RelateDemand().relateDemand("a == b");
        RelateDemandDTO relateDemandDTO = relateDemandMapper.toDto(relateDemand);
        relateDemandDTO.setSwitchNode(new SwitchNodeDTO());
        relateDemandDTO.getSwitchNode().setId(switchNode.getId());

        restBranchingMockMvc
            .perform(
                post(API_URL + "/relate-demand").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(relateDemandDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Transactional
    void getDemandsForSwitch() throws Exception {
        RelateDemand relateDemand = new RelateDemand().relateDemand("a == b").switchNode(switchNode);
        relateDemand = relateDemandRepository.saveAndFlush(relateDemand);

        restBranchingMockMvc
            .perform(get(API_URL + "/switch/{switchId}/demands", switchNode.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*].id").value(hasItem(relateDemand.getId().intValue())))
            .andExpect(jsonPath("$.[*].relateDemand").value(hasItem("a == b")));
    }
}
