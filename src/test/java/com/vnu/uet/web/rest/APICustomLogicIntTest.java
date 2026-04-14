package com.vnu.uet.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.*;
import com.vnu.uet.repository.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class APICustomLogicIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private SwitchNodeRepository switchNodeRepository;

    @Autowired
    private RelateNodeRepository relateNodeRepository;

    @Autowired
    private RelateDemandRepository relateDemandRepository;

    @Autowired
    private PerformerRepository performerRepository;

    @Autowired
    private MapFormRepository mapFormRepository;

    @Autowired
    private VariableRepository variableRepository;

    private static final List<TestDetail> testDetails = new ArrayList<>();

    private static class TestDetail {
        String endpoint;
        String scenario;
        String statusIcon;
        String resultType;
        String expected;
        String actual;
        String curl;
        String response;
        int httpStatus;

        public TestDetail(String endpoint, String scenario, String statusIcon, String resultType, String expected, String actual, String curl, String response, int httpStatus) {
            this.endpoint = endpoint;
            this.scenario = scenario;
            this.statusIcon = statusIcon;
            this.resultType = resultType;
            this.expected = expected;
            this.actual = actual;
            this.curl = curl;
            this.response = response;
            this.httpStatus = httpStatus;
        }
    }

    private Flow flow;
    private Node node;

    @BeforeEach
    public void initTest() {
        flow = new Flow().flowName("Logic Test Flow");
        flow = flowRepository.saveAndFlush(flow);
        node = new Node().nodeType("assign").flow(flow);
        node = nodeRepository.saveAndFlush(node);
    }

    private String toCurl(String method, String url, String body) {
        StringBuilder curl = new StringBuilder("curl -X ").append(method).append(" '").append(url).append("'");
        curl.append(" -H 'Content-Type: application/json'");
        if (body != null && !body.isEmpty()) {
            curl.append(" -d '").append(body.replace("'", "\\'")).append("'");
        }
        return curl.toString();
    }

    private MvcResult performAndLog(String method, String url, String body, String scenario, String expected) throws Exception {
        String curl = toCurl(method, url, body);
        MvcResult result;
        if ("POST".equalsIgnoreCase(method)) {
            result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body != null ? body : "")).andReturn();
        } else {
            result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON).content(body != null ? body : "")).andReturn();
        }

        int status = result.getResponse().getStatus();
        String responseRaw = result.getResponse().getContentAsString();
        String responsePretty = responseRaw;

        try {
            if (!responseRaw.isEmpty()) {
                Object json = objectMapper.readValue(responseRaw, Object.class);
                responsePretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            }
        } catch (Exception e) {}

        boolean isSuccess = status >= 200 && status < 300;
        String statusIcon = isSuccess ? "🟢" : "🔴";
        String resultType = isSuccess ? "Thành Công" : "Lỗi Hệ Thống";
        String actual = isSuccess ? "HTTP " + status : "Lỗi HTTP " + status;

        testDetails.add(new TestDetail(method + " " + url, scenario, statusIcon, resultType, expected, actual, curl, responsePretty.isEmpty() ? "(Trống)" : responsePretty, status));
        return result;
    }

    @Test
    @Transactional
    void testNextNode_BranchingLogic() throws Exception {
        SwitchNode switchNode = new SwitchNode().flow(flow);
        switchNode = switchNodeRepository.saveAndFlush(switchNode);

        RelateNode edgeToSwitch = new RelateNode().flow(flow).node(node).childNodeId(switchNode.getId()).hasDemand(true);
        relateNodeRepository.saveAndFlush(edgeToSwitch);

        Node targetNode = new Node().nodeType("assign").flow(flow);
        targetNode = nodeRepository.saveAndFlush(targetNode);

        RelateNode edgeFromSwitch = new RelateNode().flow(flow).node(new Node().id(switchNode.getId())).childNodeId(targetNode.getId());
        edgeFromSwitch = relateNodeRepository.saveAndFlush(edgeFromSwitch);

        RelateDemand demand = new RelateDemand().relateDemand("amount > 1000").switchNode(switchNode).relateNode(edgeFromSwitch);
        relateDemandRepository.saveAndFlush(demand);

        Map<String, Object> formData = new HashMap<>();
        formData.put("amount", 1500);
        String payload = objectMapper.writeValueAsString(formData);

        MvcResult result = performAndLog("GET", "/api/internal/flow/" + flow.getId() + "/next-node?currentNodeId=" + node.getId(), payload, 
            "Rẽ nhánh Logic: amount > 1000", "Chuyển đến đúng targetNodeId dựa trên demand");
        
        if (!result.getResponse().getContentAsString().contains(targetNode.getId().toString())) {
            TestDetail last = testDetails.get(testDetails.size() - 1);
            last.statusIcon = "🔴";
            last.resultType = "Lỗi Logic Content";
            last.actual = "Hệ thống trả về nextNodeId sai hoặc thiếu";
        }
    }

    @Test
    @Transactional
    void testActionPlan_InheritanceAndPerformers() throws Exception {
        Performer performer = new Performer().userId("user1").orderExecution(1L).node(node);
        performerRepository.saveAndFlush(performer);

        MapForm mapForm = new MapForm().targetFormId("TF001").sourceFormId("SF001").node(node);
        mapForm = mapFormRepository.saveAndFlush(mapForm);

        Variable variable = new Variable().variableSourceFormId("srcVar").variableTargetFormId("tgtVar").mapForm(mapForm);
        variableRepository.saveAndFlush(variable);

        MvcResult result = performAndLog("GET", "/api/internal/node/" + node.getId() + "/action-plan", null, 
            "Lấy Action Plan: Performer và Form Mapping", "Trả về đầy đủ thông tin người thực hiện và ánh xạ biến");

        String response = result.getResponse().getContentAsString();
        if (!response.contains("user1") || !response.contains("TF001")) {
            TestDetail last = testDetails.get(testDetails.size() - 1);
            last.statusIcon = "🔴";
            last.resultType = "Thiếu Dữ Liệu";
            last.actual = "Response không chứa đầy đủ thông tin performer/map-form";
        }
    }

    @AfterAll
    static void generateReport() {
        try {
            Path path = Paths.get("target/api-test-results.md");
            Files.createDirectories(path.getParent());
            long total = testDetails.size();
            long success = testDetails.stream().filter(d -> d.statusIcon.equals("🟢")).count();
            long failed = total - success;

            StringBuilder md = new StringBuilder();
            md.append("# 🚀 CHI TIẾT KẾT QUẢ KIỂM THỬ API LOGIC - EFLOW\n\n");
            md.append("## 📊 TỔNG QUAN\n");
            md.append("> [!IMPORTANT]\n");
            md.append(String.format("> - **Tổng số kịch bản:** `%d` \n", total));
            md.append(String.format("> - **Thành công:** 🟢 `%d` \n", success));
            md.append(String.format("> - **Thất bại/Cảnh báo:** 🔴 `%d` \n\n", failed));
            md.append("--- \n\n");

            for (int i = 0; i < testDetails.size(); i++) {
                TestDetail d = testDetails.get(i);
                md.append(String.format("### %d. %s %s\n", i + 1, d.statusIcon, d.scenario));
                md.append(String.format("- **Endpoint:** `%s`\n", d.endpoint));
                md.append(String.format("- **Kết quả:** `%s`\n", d.resultType));
                md.append(String.format("- **Dự đoán:** %s\n", d.expected));
                md.append(String.format("- **Thực tế:** %s\n\n", d.actual));
                md.append("<details><summary><b>🛠 DIAGNOSTIC</b></summary>\n\n");
                md.append("#### 📥 Input (cUrl)\n```bash\n").append(d.curl).append("\n```\n\n");
                md.append(String.format("#### 📤 Output (Response) - HTTP `%d`\n```json\n%s\n```\n\n", d.httpStatus, d.response));
                md.append("</details>\n\n---\n\n");
            }
            Files.writeString(path, md.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
