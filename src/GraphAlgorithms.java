/**
 * GraphAlgorithms.java
 *
 * 在已有的 Graph 数据结构上实现常见的图算法：
 * 1. 建立邻接表（Adjacency List）
 * 2. 建立邻接矩阵（Adjacency Matrix）
 * 3. 深度优先搜索（DFS）
 * 4. 广度优先搜索（BFS）
 *
 * 可根据需要添加更多算法，如最短路径、拓扑排序等。
 */

import java.util.*;

public class GraphAlgorithms {

    /**
     * 根据当前 Graph 构建邻接表。
     * @param graph 当前的图
     * @return 一个 Map，key 为节点 ID，value 为所有相邻节点（及边的权重）的列表
     */
    public static Map<String, List<AdjNode>> buildAdjacencyList(Graph graph) {
        Map<String, List<AdjNode>> adjacencyList = new HashMap<>();

        // 为图中每个节点 ID 初始化一个空列表
        for (Node node : graph.getNodes()) {
            adjacencyList.put(node.getId(), new ArrayList<>());
        }

        // 遍历图中的边，根据边的有向/无向性质填入邻接表
        for (Edge edge : graph.getEdges()) {
            String startId = edge.getStartNode().getId();
            String endId   = edge.getEndNode().getId();
            double weight  = edge.getWeight();

            // 对于起始节点，将终止节点加入其邻接列表
            adjacencyList.get(startId).add(new AdjNode(endId, weight));

            // 如果是无向边，则还要反向加入
            if (!edge.isDirected()) {
                adjacencyList.get(endId).add(new AdjNode(startId, weight));
            }
        }

        return adjacencyList;
    }

    /**
     * 根据当前 Graph 构建邻接矩阵。
     * 仅适用于节点数较少时，或后续需要使用 Floyd 等算法时。
     * @param graph 当前的图
     * @return 一个二维矩阵（List<List<Double>>），
     *         若 i, j 之间无边，则可用 Double.POSITIVE_INFINITY 表示。
     *         同时返回节点序列表 nodeOrder，可让外部知道索引对应哪个节点 ID。
     */
    public static AdjacencyMatrix buildAdjacencyMatrix(Graph graph) {
        // 获取图中所有节点并固定顺序
        List<Node> nodeList = new ArrayList<>(graph.getNodes());
        int n = nodeList.size();

        // 创建一个 n*n 的矩阵，初始化为无穷大，表示没有直接边
        List<List<Double>> matrix = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Double> row = new ArrayList<>(Collections.nCopies(n, Double.POSITIVE_INFINITY));
            matrix.add(row);
        }

        // 自身到自身的距离可设为0（如果不需要也可保留无穷大）
        for (int i = 0; i < n; i++) {
            matrix.get(i).set(i, 0.0);
        }

        // 根据图中的边填充矩阵
        for (Edge edge : graph.getEdges()) {
            Node startNode = edge.getStartNode();
            Node endNode   = edge.getEndNode();
            double weight  = edge.getWeight();

            int startIndex = nodeList.indexOf(startNode);
            int endIndex   = nodeList.indexOf(endNode);

            // 设置矩阵值
            matrix.get(startIndex).set(endIndex, weight);

            // 若无向边，则对称设置
            if (!edge.isDirected()) {
                matrix.get(endIndex).set(startIndex, weight);
            }
        }

        // 封装返回结果
        return new AdjacencyMatrix(matrix, nodeList);
    }

    /**
     * 对图进行深度优先搜索（DFS）。
     * @param graph 当前的图
     * @param startNodeId 起始节点 ID
     * @return 返回遍历节点的 ID 顺序列表
     */
    public static List<String> depthFirstSearch(Graph graph, String startNodeId) {
        // 首先获取邻接表
        Map<String, List<AdjNode>> adjacencyList = buildAdjacencyList(graph);

        // DFS 使用栈/递归，这里演示递归方式
        List<String> visitedOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        dfsRecursive(startNodeId, adjacencyList, visited, visitedOrder);

        return visitedOrder;
    }

    /**
     * 递归 DFS 辅助函数
     */
    private static void dfsRecursive(String current,
                                     Map<String, List<AdjNode>> adjacencyList,
                                     Set<String> visited,
                                     List<String> visitedOrder) {
        // 标记当前节点已访问
        visited.add(current);
        visitedOrder.add(current);

        // 遍历相邻节点
        List<AdjNode> neighbors = adjacencyList.get(current);
        if (neighbors == null) return;  // 该节点无邻接表记录或不存在

        for (AdjNode adj : neighbors) {
            String neighborId = adj.getNodeId();
            if (!visited.contains(neighborId)) {
                dfsRecursive(neighborId, adjacencyList, visited, visitedOrder);
            }
        }
    }

    /**
     * 对图进行广度优先搜索（BFS）。
     * @param graph 当前的图
     * @param startNodeId 起始节点 ID
     * @return 返回遍历节点的 ID 顺序列表
     */
    public static List<String> breadthFirstSearch(Graph graph, String startNodeId) {
        // 获取邻接表
        Map<String, List<AdjNode>> adjacencyList = buildAdjacencyList(graph);

        List<String> visitedOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        // 启动 BFS
        visited.add(startNodeId);
        queue.offer(startNodeId);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            visitedOrder.add(current);

            List<AdjNode> neighbors = adjacencyList.get(current);
            if (neighbors != null) {
                for (AdjNode adj : neighbors) {
                    String neighborId = adj.getNodeId();
                    if (!visited.contains(neighborId)) {
                        visited.add(neighborId);
                        queue.offer(neighborId);
                    }
                }
            }
        }

        return visitedOrder;
    }

    // -------------------
    // 辅助类与数据结构：
    // -------------------

    /**
     * 邻接表中用于存储 (目标节点ID, 权重) 的小结构。
     * 也可以直接用 Map<String,Double>，这里显式用类便于扩展。
     */
    public static class AdjNode {
        private String nodeId;
        private double weight;

        public AdjNode(String nodeId, double weight) {
            this.nodeId = nodeId;
            this.weight = weight;
        }

        public String getNodeId() {
            return nodeId;
        }

        public double getWeight() {
            return weight;
        }
    }

    /**
     * 邻接矩阵返回数据结构，包含：
     * 1. matrix：Double 的二维列表
     * 2. nodeOrder：节点列表，以确定 matrix 的行列索引对应哪个 Node
     */
    public static class AdjacencyMatrix {
        private List<List<Double>> matrix;
        private List<Node> nodeOrder;

        public AdjacencyMatrix(List<List<Double>> matrix, List<Node> nodeOrder) {
            this.matrix = matrix;
            this.nodeOrder = nodeOrder;
        }

        public List<List<Double>> getMatrix() {
            return matrix;
        }

        public List<Node> getNodeOrder() {
            return nodeOrder;
        }

        /**
         * 用于调试或查看
         */
        public void printMatrix() {
            System.out.println("邻接矩阵：");
            // 先打印列标题
            System.out.print("    ");
            for (Node n : nodeOrder) {
                System.out.printf("%8s", n.getId());
            }
            System.out.println();

            // 打印每行
            for (int i = 0; i < nodeOrder.size(); i++) {
                System.out.printf("%4s", nodeOrder.get(i).getId());
                for (int j = 0; j < nodeOrder.size(); j++) {
                    double val = matrix.get(i).get(j);
                    if (val == Double.POSITIVE_INFINITY) {
                        System.out.printf("%8s", "∞");
                    } else {
                        System.out.printf("%8.2f", val);
                    }
                }
                System.out.println();
            }
        }
    }
}