package com.vnu.uet.service;

import com.vnu.uet.domain.Node;
import com.vnu.uet.domain.RelateNode;
import com.vnu.uet.domain.SwitchNode;
import com.vnu.uet.repository.NodeRepository;
import com.vnu.uet.repository.RelateNodeRepository;
import com.vnu.uet.repository.SwitchNodeRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiagramService {

    private static final Logger LOG = LoggerFactory.getLogger(DiagramService.class);

    private final NodeRepository nodeRepository;
    private final RelateNodeRepository relateNodeRepository;
    private final SwitchNodeRepository switchNodeRepository;
    private final FlowService flowService; // reusing cascade deletes from the entity repos?

    // To delete elements safely, we need other repos too:
    private final com.vnu.uet.repository.PerformerRepository performerRepository;
    private final com.vnu.uet.repository.MapFormRepository mapFormRepository;
    private final com.vnu.uet.repository.VariableRepository variableRepository;
    private final com.vnu.uet.repository.RelateDemandRepository relateDemandRepository;

    public DiagramService(
        NodeRepository nodeRepository,
        RelateNodeRepository relateNodeRepository,
        SwitchNodeRepository switchNodeRepository,
        FlowService flowService,
        com.vnu.uet.repository.PerformerRepository performerRepository,
        com.vnu.uet.repository.MapFormRepository mapFormRepository,
        com.vnu.uet.repository.VariableRepository variableRepository,
        com.vnu.uet.repository.RelateDemandRepository relateDemandRepository
    ) {
        this.nodeRepository = nodeRepository;
        this.relateNodeRepository = relateNodeRepository;
        this.switchNodeRepository = switchNodeRepository;
        this.flowService = flowService;
        this.performerRepository = performerRepository;
        this.mapFormRepository = mapFormRepository;
        this.variableRepository = variableRepository;
        this.relateDemandRepository = relateDemandRepository;
    }

    /**
     * Get the entire diagram definition (nodes, edges, switches) for a given flowId.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getFlowDefinition(Long flowId) {
        LOG.debug("Request to get Diagram Definition for flowId: {}", flowId);

        List<Node> nodes = nodeRepository
            .findAll()
            .stream()
            .filter(n -> n.getFlow() != null && n.getFlow().getId().equals(flowId))
            .collect(Collectors.toList());

        List<RelateNode> edges = relateNodeRepository
            .findAll()
            .stream()
            .filter(e -> e.getFlow() != null && e.getFlow().getId().equals(flowId))
            .collect(Collectors.toList());

        List<SwitchNode> switches = switchNodeRepository
            .findAll()
            .stream()
            .filter(s -> s.getFlow() != null && s.getFlow().getId().equals(flowId))
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("flowId", flowId);
        // Note: returning mapped DTOs might be better, but entities work if Jackson mappings avoid cycles.
        response.put("nodes", nodes);
        response.put("edges", edges);
        response.put("switches", switches);
        return response;
    }

    /**
     * Delete diagram elements safely.
     */
    public void deleteElements(List<Long> inputNodeIds, List<Long> inputEdgeIds, List<Long> inputSwitchIds) {
        LOG.debug("Request to delete diagram elements: Nodes {}, Edges {}, Switches {}", inputNodeIds, inputEdgeIds, inputSwitchIds);

        // 1. Delete Switches
        if (inputSwitchIds != null) {
            for (Long switchId : inputSwitchIds) {
                switchNodeRepository
                    .findById(switchId)
                    .ifPresent(switchNode -> {
                        relateDemandRepository.deleteAll(switchNode.getRelateDemands());
                        switchNodeRepository.delete(switchNode);
                    });
            }
        }

        // 2. Delete Edges (RelateNodes)
        if (inputEdgeIds != null) {
            for (Long edgeId : inputEdgeIds) {
                relateNodeRepository
                    .findById(edgeId)
                    .ifPresent(edge -> {
                        relateDemandRepository.deleteAll(edge.getRelateDemands());
                        relateNodeRepository.delete(edge);
                    });
            }
        }

        // 3. Delete Nodes
        if (inputNodeIds != null) {
            for (Long nodeId : inputNodeIds) {
                nodeRepository
                    .findById(nodeId)
                    .ifPresent(node -> {
                        performerRepository.deleteAll(node.getPerformers());
                        node
                            .getMapForms()
                            .forEach(mf -> {
                                variableRepository.deleteAll(mf.getVariables());
                                mapFormRepository.delete(mf);
                            });
                        nodeRepository.delete(node);
                    });
            }
        }
    }
}
