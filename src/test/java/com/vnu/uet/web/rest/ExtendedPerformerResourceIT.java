package com.vnu.uet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.Node;
import com.vnu.uet.domain.Performer;
import com.vnu.uet.repository.NodeRepository;
import com.vnu.uet.repository.PerformerRepository;
import com.vnu.uet.service.dto.NodeDTO;
import com.vnu.uet.service.dto.PerformerDTO;
import com.vnu.uet.service.mapper.PerformerMapper;
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
 * Integration tests for the {@link ExtendedPerformerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExtendedPerformerResourceIT {

    private static final String API_URL = "/api/node";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private PerformerRepository performerRepository;

    @Autowired
    private PerformerMapper performerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPerformerMockMvc;

    private Node node;

    @BeforeEach
    public void initTest() {
        node = new Node().nodeType("assign");
        node = nodeRepository.saveAndFlush(node);
    }

    @Test
    @Transactional
    void addPerformerToNode() throws Exception {
        Performer performer = new Performer().userId("user1@vnu.edu.vn").orderExecution(1L);
        PerformerDTO performerDTO = performerMapper.toDto(performer);
        performerDTO.setNode(new NodeDTO());
        performerDTO.getNode().setId(node.getId());

        restPerformerMockMvc
            .perform(
                post(API_URL + "/{nodeId}/performer", node.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(performerDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.userId").value("user1@vnu.edu.vn"));
    }

    @Test
    @Transactional
    void getPerformersForNode() throws Exception {
        Performer performer = new Performer().userId("user1@vnu.edu.vn").orderExecution(1L).node(node);
        performer = performerRepository.saveAndFlush(performer);

        restPerformerMockMvc
            .perform(get(API_URL + "/{nodeId}/performers", node.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*].id").value(hasItem(performer.getId().intValue())))
  