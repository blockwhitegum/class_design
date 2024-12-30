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
    public static class ShortestPathResult {
        private List<String> path;   // 节点ID顺序
        private double distance;     // 最终距离/权重和

        public ShortestPathResult(List<String> path, double distance) {
            this.path = path;
            this.distance = distance;
        }

        public List<String> getPath() {
            return path;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public String toString() {
            return "路径=" + path + "，总距离=" + distance;
        }
    }
    /**
     * 使用 BFS 求 startNodeId 到 endNodeId 的最短路径（无权图）
     */
    public static ShortestPathResult findShortestPathBFS(Graph graph, String startNodeId, String endNodeId) {
        // 若两个节点相同，直接返回
        if (startNodeId.equals(endNodeId)) {
            return new ShortestPathResult(Collections.singletonList(startNodeId), 0.0);
        }

        // 先构建邻接表
        Map<String, List<AdjNode>> adjacencyList = buildAdjacencyList(graph);
        // 用来记录路径
        Map<String, String> parent = new HashMap<>();
        // 用来标记已访问
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        visited.add(startNodeId);
        queue.offer(startNodeId);
        parent.put(startNodeId, null); // 起点没有父节点

        boolean found = false;
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(endNodeId)) {
                found = true;
                break;
            }
            List<AdjNode> neighbors = adjacencyList.get(current);
            if (neighbors != null) {
                for (AdjNode adj : neighbors) {
                    String neighId = adj.getNodeId();
                    if (!visited.contains(neighId)) {
                        visited.add(neighId);
                        parent.put(neighId, current);
                        queue.offer(neighId);
                    }
                }
            }
        }

        if (!found) {
            // 无法到达
            return null;
        }

        // 反向回溯，得到路径
        List<String> path = new ArrayList<>();
        String node = endNodeId;
        while (node != null) {
            path.add(node);
            node = parent.get(node);
        }
        Collections.reverse(path);

        // 无权图的“最短路径”距离，即边数
        double distance = path.size() - 1;
        return new ShortestPathResult(path, distance);
    }
    /**
     * 使用 Dijkstra 求 startNodeId 到 endNodeId 的最短路径
     */
    public static ShortestPathResult findShortestPathDijkstra(Graph graph, String startNodeId, String endNodeId) {
        // 构建邻接表
        Map<String, List<AdjNode>> adjacencyList = buildAdjacencyList(graph);

        // 距离表 & 前驱记录
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();

        // 初始化 dist
        for (Node node : graph.getNodes()) {
            dist.put(node.getId(), Double.POSITIVE_INFINITY);
            parent.put(node.getId(), null);
        }
        dist.put(startNodeId, 0.0);

        // 优先队列：以当前最小距离为优先级
        PriorityQueue<String> pq = new PriorityQueue<>((a,b) -> dist.get(a).compareTo(dist.get(b)));
        pq.offer(startNodeId);

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (current.equals(endNodeId)) {
                break;
            }
            List<AdjNode> neighbors = adjacencyList.get(current);
            if (neighbors != null) {
                for (AdjNode adj : neighbors) {
                    String neighId = adj.getNodeId();
                    double newDist = dist.get(current) + adj.getWeight();
                    if (newDist < dist.get(neighId)) {
                        dist.put(neighId, newDist);
                        parent.put(neighId, current);
                        // 更新优先队列
                        pq.remove(neighId);
                        pq.offer(neighId);
                    }
                }
            }
        }

        double finalDist = dist.get(endNodeId);
        if (finalDist == Double.POSITIVE_INFINITY) {
            // 不可达
            return null;
        }

        // 回溯路径
        List<String> path = new ArrayList<>();
        String node = endNodeId;
        while (node != null) {
            path.add(node);
            node = parent.get(node);
        }
        Collections.reverse(path);

        return new ShortestPathResult(path, finalDist);
    }
    public static class FloydResult {
        // 距离矩阵
        public double[][] dist;
        // 用于回溯路径的中间节点
        public int[][] next;
        // 节点列表
        public List<Node> nodeOrder;
    }

    /**
     * 预处理 Floyd-Warshall，生成所有节点对之间的最短路径信息
     */
    public static FloydResult floydWarshall(Graph graph) {
        // 获取节点列表及邻接矩阵
        AdjacencyMatrix adjacency = buildAdjacencyMatrix(graph);
        List<Node> nodeList = adjacency.getNodeOrder();
        List<List<Double>> mat = adjacency.getMatrix();
        int n = nodeList.size();

        double[][] dist = new double[n][n];
        int[][] next = new int[n][n];

        // 初始化 dist 与 next
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = mat.get(i).get(j);
                if (i != j && dist[i][j] != Double.POSITIVE_INFINITY) {
                    next[i][j] = j;
                } else {
                    next[i][j] = -1;
                }
            }
        }

        // 核心三重循环
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        // 封装结果
        FloydResult fr = new FloydResult();
        fr.dist = dist;
        fr.next = next;
        fr.nodeOrder = nodeList;
        return fr;
    }
    /**
     * 从 FloydResult 中重构 startId->endId 的最短路径
     */
    public static ShortestPathResult rebuildFloydPath(FloydResult fr, String startId, String endId) {
        List<Node> nodeOrder = fr.nodeOrder;
        int startIndex = -1, endIndex = -1;
        for (int i = 0; i < nodeOrder.size(); i++) {
            if (nodeOrder.get(i).getId().equals(startId)) {
                startIndex = i;
            }
            if (nodeOrder.get(i).getId().equals(endId)) {
                endIndex = i;
            }
        }
        if (startIndex == -1 || endIndex == -1) return null;
        if (fr.dist[startIndex][endIndex] == Double.POSITIVE_INFINITY) {
            return null; // 不可达
        }

        // 回溯路径
        List<String> path = new ArrayList<>();
        int current = startIndex;
        while (current != endIndex) {
            path.add(nodeOrder.get(current).getId());
            current = fr.next[current][endIndex];
            if (current < 0) return null;
        }
        // 加上终点
        path.add(nodeOrder.get(endIndex).getId());

        double distance = fr.dist[startIndex][endIndex];
        return new ShortestPathResult(path, distance);
    }
}