/**
 * Graph.java
 *
 * 用于表示图的数据结构，并提供添加/删除结点和边等操作方法。
 *
 * 功能概述：
 * 1. 维护一个存储所有结点的列表（或集合）。
 * 2. 维护一个存储所有边的列表（或集合）。
 * 3. 提供结点和边的增删改查方法。
 * 4. 可为后续的图算法提供基础数据操作支持。
 */

import java.util.ArrayList;
import java.util.List;

public class Graph {

    // 存储图中所有结点
    private List<Node> nodes;

    // 存储图中所有边
    private List<Edge> edges;

    /**
     * 构造方法：初始化结点和边的集合
     */
    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    /**
     * 添加一个结点
     * @param node 要添加的结点对象
     * @return 是否添加成功（若已存在相同标识的结点，可返回 false）
     */
    public boolean addNode(Node node) {
        // 检查是否已有同名结点
        for (Node existingNode : nodes) {
            if (existingNode.getId().equals(node.getId())) {
                // 若结点标识相同，则不再重复添加
                return false;
            }
        }
        nodes.add(node);
        return true;
    }

    /**
     * 删除一个结点
     * @param nodeId 结点的唯一标识（名称或编号）
     * @return 是否删除成功
     */
    public boolean removeNode(String nodeId) {
        // 首先查找是否存在该结点
        Node targetNode = nodes.stream().filter(n -> n.getId().equals(nodeId)).findFirst().orElse(null);
        if (targetNode == null) {
            // 未找到目标结点
            return false;
        }

        // 如果找到，先删除与其相关的边
        edges.removeIf(edge -> edge.getStartNode().equals(targetNode)
                || edge.getEndNode().equals(targetNode));

        // 再从结点列表中删除
        nodes.remove(targetNode);
        return true;
    }

    /**
     * 添加一条边
     * @param edge 要添加的边对象
     * @return 是否添加成功（若相同边已存在，可返回 false）
     */
    public boolean addEdge(Edge edge) {
        // 检查是否已有完全相同的边
        for (Edge existingEdge : edges) {
            // 对无向边，可以将起点终点对调后比较
            // 此处仅示例性处理，可根据需求调整
            if (existingEdge.equals(edge)) {
                return false;
            }
        }

        // 只有当起点与终点都在图中时，才能成功添加
        if (!nodes.contains(edge.getStartNode()) ||
                !nodes.contains(edge.getEndNode())) {
            return false;
        }

        edges.add(edge);
        return true;
    }

    /**
     * 删除一条边
     * @param startId 起点结点 ID
     * @param endId   终点结点 ID
     * @return 是否删除成功
     */
    public boolean removeEdge(String startId, String endId) {
        // 查找图中符合条件的边
        Edge targetEdge = null;
        for (Edge e : edges) {
            if (e.getStartNode().getId().equals(startId)
                    && e.getEndNode().getId().equals(endId)) {
                targetEdge = e;
                break;
            }
        }
        if (targetEdge == null) {
            return false;
        }
        edges.remove(targetEdge);
        return true;
    }

    /**
     * 根据 nodeId 获取结点对象
     * @param nodeId 结点的唯一标识
     * @return 对应的 Node 对象，若未找到则返回 null
     */
    public Node getNodeById(String nodeId) {
        for (Node n : nodes) {
            if (n.getId().equals(nodeId)) {
                return n;
            }
        }
        return null;
    }

    /**
     * 获取当前所有结点列表（只读）
     * @return 一个包含所有结点的 List
     */
    public List<Node> getNodes() {
        // 返回副本或只读视图，可避免外部直接修改集合
        return new ArrayList<>(nodes);
    }

    /**
     * 获取当前所有边列表（只读）
     * @return 一个包含所有边的 List
     */
    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    /**
     * 打印调试信息：显示所有结点和边
     * 便于在控制台查看当前图结构
     */

}