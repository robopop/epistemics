package selemca.epistemics.mentalworld.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import selemca.epistemics.mentalworld.engine.MentalWorldEngineState;
import selemca.epistemics.mentalworld.engine.node.ActionNode;
import selemca.epistemics.mentalworld.engine.node.DecisionNode;
import selemca.epistemics.mentalworld.engine.node.DeriverNode;

import javax.servlet.http.HttpSession;
import javax.ws.rs.NotFoundException;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(DeriverNodeController.PATH_PREFIX)
public class DeriverNodeController extends AbstractRestController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    protected static final String PATH_PREFIX = "/epistemics-node";
    protected static final String PATH_NODE = "node";
    protected static final String PATH_DECIDE = "/decide";
    protected static final String PATH_ACTION = "/action";
    protected static final String PATH_NODE_TYPE_NAME = "nodeType";
    protected static final String PATH_NODE_TYPE_PART = "/{" + PATH_NODE_TYPE_NAME + "}";
    protected static final String NODE_INTERFACE_PACKAGE = "selemca.epistemics.mentalworld.engine.node";
    protected static final String NODE_CLASS_NAME_SUFFIX = "DeriverNode";

    @RequestMapping(value = PATH_NODE + PATH_ID_PART + PATH_DECIDE + PATH_NODE_TYPE_PART, method = POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    public boolean decide(@PathVariable(PATH_ID_NAME) String appraisalId, @PathVariable(PATH_NODE_TYPE_NAME) String nodeTypeName, HttpSession httpSession) {
        MentalWorldEngineState engineState = getEngineState(appraisalId, httpSession);
        //noinspection ThrowableResultOfMethodCallIgnored
        return getNode(engineState, nodeTypeName, DecisionNode.class)
                .map(DecisionNode::decide)
                .orElseThrow(() -> missingNode(nodeTypeName));
    }

    @RequestMapping(value = PATH_NODE + PATH_ID_PART + PATH_ACTION + PATH_NODE_TYPE_PART, method = POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    public void action(@PathVariable(PATH_ID_NAME) String appraisalId, @PathVariable(PATH_NODE_TYPE_NAME) String nodeTypeName, HttpSession httpSession) {
        MentalWorldEngineState engineState = getEngineState(appraisalId, httpSession);
        Optional<ActionNode> actionNodeOptional = getNode(engineState, nodeTypeName, ActionNode.class);
        actionNodeOptional.ifPresent(ActionNode::apply);
        if (!actionNodeOptional.isPresent()) {
            throw missingNode(nodeTypeName);
        }
    }

    public RuntimeException missingNode(String nodeTypeName) {
        return new NotFoundException(String.format("Missing node of type: %s", nodeTypeName));
    }

    @SuppressWarnings("unchecked")
    protected <D extends DeriverNode> Optional<D> getNode(MentalWorldEngineState engineState, String nodeTypeName, Class<D> type) {
        String nodeClassName = NODE_INTERFACE_PACKAGE + "." + nodeTypeName + NODE_CLASS_NAME_SUFFIX;
        try {
            Class<?> nodeClass = Class.forName(nodeClassName);
            if (!type.isAssignableFrom(nodeClass)) {
                LOG.warn("Not a decision node: {}", nodeClass);
                return Optional.empty();
            } else {
                return engineState.getDeriverNode((Class<? extends DeriverNode>) nodeClass).map(type::cast);
            }
        } catch (ClassNotFoundException ignore) {
            return Optional.empty();
        }
    }
}