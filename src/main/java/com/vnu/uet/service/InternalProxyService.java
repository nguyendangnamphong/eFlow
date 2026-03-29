package com.vnu.uet.service;

import com.vnu.uet.domain.MapForm;
import com.vnu.uet.domain.Node;
import com.vnu.uet.domain.RelateDemand;
import com.vnu.uet.domain.RelateNode;
import com.vnu.uet.domain.SwitchNode;
import com.vnu.uet.domain.Variable;
import com.vnu.uet.repository.MapFormRepository;
import com.vnu.uet.repository.NodeRepository;
import com.vnu.uet.repository.PerformerRepository;
import com.vnu.uet.repository.RelateDemandRepository;
import com.vnu.uet.repository.RelateNodeRepository;
import com.vnu.uet.repository.SwitchNodeRepository;
import com.vnu.uet.repository.VariableRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InternalProxyService {

    private static final Logger LOG = LoggerFactory.getLogger(InternalProxyService.class);

    private final NodeRepository nodeRepository;
    private final RelateNodeRepository relateNodeRepository;
    private final SwitchNodeRepository switchNodeRepository;
    private final RelateDemandRepository relateDemandRepository;

    private final PerformerRepository performerRepository;
    private final MapFormRepository mapFormRepository;
    private final VariableRepository variableRepository;

    private final ExpressionParser spelParser = new SpelExpressionParser();

    public InternalProxyService(
        NodeRepository nodeRepository,
        RelateNodeRepository relateNodeRepository,
        SwitchNodeRepository switchNodeRepository,
        RelateDemandRepository relateDemandRepository,
        PerformerRepository performerRepository,
        MapFormRepository mapFormRepository,
        VariableRepository variableRepository
    ) {
        this.nodeRepository = nodeRepository;
        this.relateNodeRepository = relateNodeRepository;
        this.switchNodeRepository = switchNodeRepository;
        this.relateDemandRepository = relateDemandRepository;
        this.performerRepository = performerRepository;
        this.mapFormRepository = mapFormRepository;
        this.variableRepository = variableRepository;
    }

    /**
     * Dựa vào dữ liệu biểu mẫu truyền vào, tính toán node tiếp theo sẽ phải đi đến.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> calculateNextNode(Long flowId, Long currentNodeId, Map<String, Object> currentFormData) {
        LOG.debug("Evaluating next node for Flow: {}, CurrentNode: {}", flowId, currentNodeId);

        Optional<Node> currentNodeOpt = nodeRepository.findById(currentNodeId);
        if (currentNodeOpt.isEmpty()) {
            throw new IllegalArgumentException("Khong ton tai Node " + currentNodeId);
        }

        // Lấy danh sách các liên kết ra khỏi node này
        List<RelateNode> outgoingEdges = relateNodeRepository
            .findAll()
            .stream()
            .filter(e -> e.getNode() != null && e.getNode().getId().equals(currentNodeId))
            .collect(Collectors.toList());

        Long nextNodeId = null;

        for (RelateNode edge : outgoingEdges) {
            Boolean hasDemand = edge.getHasDemand();

            if (Boolean.TRUE.equals(hasDemand)) {
                // Đi qua SwitchNode, cần tính điểm (Evaluate SpEL)
                List<SwitchNode> switches = switchNodeRepository
                    .findAll()
                    .stream()
                    .filter(s -> s.getRelateNode() != null && s.getRelateNode().getId().equals(edge.getId()))
                    .collect(Collectors.toList());

                if (!switches.isEmpty()) {
                    SwitchNode switchNode = switches.get(0); // Mỗi RelateNode thường trỏ đến 1 cụm điều kiện thôi

                    // Lấy tất cả điều kiện
                    List<RelateDemand> demands = relateDemandRepository
                        .findAll()
                        .stream()
                        .filter(d -> d.getSwitchNode() != null && d.getSwitchNode().getId().equals(switchNode.getId()))
                        .collect(Collectors.toList());

                    for (RelateDemand demand : demands) {
                        String spelExpression = demand.getRelateDemand();
                        if (evaluateSpel(spelExpression, currentFormData)) {
                            // Nếu điều kiện này Thỏa mãn, ta tìm RelateNode (edge) tiếp theo trỏ từ điều kiện này
                            // Lưu ý: JDL hiện tại chỉ map RelateDemand <- RelateNode, nên mô hình hơi đơn giản.
                            // Trong thực tế demand này sẽ chứa childNodeId ở RelateNode liên kết.
                            // Ở đây ta mô phỏng lấy đích nối từ Edge ban đầu.
                            nextNodeId = edge.getChildNodeId();
                            break;
                        }
                    }
                }
            } else {
                // Trực tiếp đi - không check điều kiện (Trường hợp thẳng)
                nextNodeId = edge.getChildNodeId();
                break;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("flowId", flowId);
        result.put("currentNodeId", currentNodeId);
        result.put("nextNodeId", nextNodeId);
        return result;
    }

    /**
     * Parse and run Spring Expression Language logic
     */
    private boolean evaluateSpel(String expressionStr, Map<String, Object> dataParams) {
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            if (dataParams != null) {
                context.setVariables(dataParams); // set as SpEL variables (#var)
            }
            Expression expression = spelParser.parseExpression(expressionStr);
            Boolean result = expression.getValue(context, Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LOG.warn("Khong the parse bieu thuc SpEL: {}", expressionStr, e);
            return false;
        }
    }

    /**
     * Gather Performers, Forms, and Variables layout for eRequest to execute.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getActionPlan(Long nodeId) {
        Map<String, Object> actionPlan = new HashMap<>();
        actionPlan.put("nodeId", nodeId);

        // 1. Gói Performers
        List<Map<String, Object>> performerList = performerRepository
            .findAllByNodeId(nodeId)
            .stream()
            .map(p -> {
                Map<String, Object> pMap = new HashMap<>();
                pMap.put("performerId", p.getId());
                pMap.put("userId", p.getUserId());
                pMap.put("orderExecution", p.getOrderExecution());
                return pMap;
            })
            .collect(Collectors.toList());
        actionPlan.put("performers", performerList);

        // 2. Gói MapForms
        List<MapForm> mapForms = mapFormRepository.findAllByNodeId(nodeId);
        List<Map<String, Object>> formsPackage = new ArrayList<>();

        for (MapForm mf : mapForms) {
            Map<String, Object> mfData = new HashMap<>();
            mfData.put("mapFormId", mf.getId());
            mfData.put("sourceFormId", mf.getSourceFormId());
            mfData.put("targetFormId", mf.getTargetFormId());

            // 3. Gói Variables (kế thừa)
            List<Map<String, Object>> varList = variableRepository
                .findAllByMapFormId(mf.getId())
                .stream()
                .map(v -> {
                    Map<String, Object> vMap = new HashMap<>();
                    vMap.put("variableId", v.getId());
                    vMap.put("variableSourceFormId", v.getVariableSourceFormId());
                    vMap.put("variableTargetFormId", v.getVariableTargetFormId());
                    return vMap;
                })
                .collect(Collectors.toList());

            mfData.put("variables", varList);
            formsPackage.add(mfData);
        }

        actionPlan.put("forms", formsPackage);

        return actionPlan;
    }
}
