/**
 * Node.java
 *
 * 表示图中的结点对象。
 * 主要属性：
 * 1. id：结点标识（一般是唯一的，如名字/字符串/数字等）。
 * 2. x, y：在可视化时的坐标位置（可选，用于在界面上绘制）。
 * 3. 可扩展其他属性，如名称、颜色、是否访问过等。
 */

public class Node {

    private String id;    // 结点标识
    private double x;     // 可视化时的横坐标
    private double y;     // 可视化时的纵坐标

    /**
     * 构造方法
     * @param id 结点标识（用于区分不同结点）
     */
    public Node(String id) {
        this(id, 0.0, 0.0);
    }

    /**
     * 重载构造方法，可指定坐标
     * @param id 结点标识
     * @param x  结点的 x 坐标
     * @param y  结点的 y 坐标
     */
    public Node(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    /**
    *  设置结点坐标并且触发界面重绘
    */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * 获取结点 ID
     * @return 结点标识字符串
     */
    public String getId() {
        return id;
    }

    /**
     * 设置结点 ID
     * @param id 新的结点标识
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取结点的 x 坐标
     * @return x 坐标
     */
    public double getX() {
        return x;
    }

    /**
     * 设置结点的 x 坐标
     * @param x 新的 x 坐标
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * 获取结点的 y 坐标
     * @return y 坐标
     */
    public double getY() {
        return y;
    }

    /**
     * 设置结点的 y 坐标
     * @param y 新的 y 坐标
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * 重写 equals 方法，用于判断两个结点是否相同（默认根据 id 判断）
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Node)) return false;
        Node other = (Node) obj;
        return this.id.equals(other.id);
    }

    /**
     * 重写 hashCode 方法，与 equals 保持一致
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * 重写 toString 方法，便于在打印或调试时查看结点信息
     */
    @Override
    public String toString() {
        return "Node{id='" + id + "', x=" + x + ", y=" + y + "}";
    }
}