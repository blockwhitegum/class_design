/**
 * Edge.java
 *
 * 表示图中的边对象。
 * 主要属性：
 * 1. startNode：边的起始结点。
 * 2. endNode：边的终止结点。
 * 3. weight：边的权重（可选）。
 * 4. directed：是否是有向边的标识。
 */

public class Edge {

    private Node startNode;  // 边的起始结点
    private Node endNode;    // 边的终止结点
    private double weight;   // 边的权重
    private boolean directed; // 是否有向

    /**
     * 构造方法：有向边，或无向边（通过 directed 决定）。
     * @param startNode 起始结点
     * @param endNode   终止结点
     * @param weight    边的权重
     * @param directed  是否有向：true 表示有向，false 表示无向
     */
    public Edge(Node startNode, Node endNode, double weight, boolean directed) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight = weight;
        this.directed = directed;
    }

    /**
     * 获取起始结点
     * @return 起始结点对象
     */
    public Node getStartNode() {
        return startNode;
    }

    /**
     * 设置起始结点
     * @param startNode 新的起始结点
     */
    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    /**
     * 获取终止结点
     * @return 终止结点对象
     */
    public Node getEndNode() {
        return endNode;
    }

    /**
     * 设置终止结点
     * @param endNode 新的终止结点
     */
    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    /**
     * 获取边的权重
     * @return 边权值
     */
    public double getWeight() {
        return weight;
    }

    /**
     * 设置边的权重
     * @param weight 新的权重值
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * 是否是有向边
     * @return 若是有向边，返回 true；否则返回 false
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * 设置是否为有向边
     * @param directed 是否有向
     */
    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    /**
     * 重写 equals 方法，用于判断两条边是否相同
     * 可根据需求调整判定规则
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;

        // 若边是无向的，可把起始结点和终止结点对调后再比较
        if (!this.isDirected() && !other.isDirected()) {
            // 无向：则只要两个端点相同即可视为同一条边
            // (startA, endA) == (startB, endB) 或 (startA, endA) == (endB, startB)
            return (this.startNode.equals(other.startNode)
                    && this.endNode.equals(other.endNode))
                    || (this.startNode.equals(other.endNode)
                    && this.endNode.equals(other.startNode));
        } else {
            // 有向：需要起点和终点都分别相同
            return this.startNode.equals(other.startNode)
                    && this.endNode.equals(other.endNode)
                    && this.directed == other.directed;
        }
    }

    /**
     * 重写 hashCode 方法，与 equals 保持一致
     * 无向边的哈希可做“排大小”处理，以规避 (start, end) 与 (end, start) 造成的冲突
     */
    @Override
    public int hashCode() {
        if (!directed) {
            // 无向：将两个节点ID排序后进行拼接，以统一表示
            String s1 = (startNode.getId().compareTo(endNode.getId()) <= 0)
                    ? startNode.getId() : endNode.getId();
            String s2 = (s1.equals(startNode.getId()))
                    ? endNode.getId() : startNode.getId();
            return (s1 + "_" + s2).hashCode();
        } else {
            // 有向：直接拼接 start -> end
            return (startNode.getId() + "->" + endNode.getId()).hashCode();
        }
    }

    /**
     * 重写 toString，方便打印边信息
     */
    @Override
    public String toString() {
        if (directed) {
            return "Edge(" + startNode.getId() + " -> "
                    + endNode.getId() + ", w=" + weight + ")";
        } else {
            return "Edge(" + startNode.getId() + " -- "
                    + endNode.getId() + ", w=" + weight + ")";
        }
    }
}