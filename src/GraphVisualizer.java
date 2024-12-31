/**
 * 主要功能：图形化展示图的可视化组件，用于绘制图中的节点和边。
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class GraphVisualizer extends JPanel {

    private Graph graph;
    private int nodeRadius = 20;
    private List<String> highlightNodePath;    // 要高亮的结点 ID 列表
    private List<Edge>  highlightEdgePath;    // 要高亮的边列表 (可选)

    private Node draggedNode = null;  // 当前被拖拽的节点
    private Point mouseOffset = null; // 鼠标与节点的偏移量

    /**
     * 方法功能：获取当前高亮显示的节点路径
     */
    public List<String> getHighlightPath() {
        return this.highlightNodePath;
    }

    /**
     * 方法功能：构造函数，初始化可视化组件
     */
    public GraphVisualizer(Graph graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE); // 设置背景色

        // 添加鼠标监听器，处理拖拽操作
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 判断鼠标是否点击在某个节点上
                Node node = getNodeAtPosition(e.getPoint());
                if (node != null) {
                    draggedNode = node;
                    // 计算鼠标相对于节点的偏移量
                    mouseOffset = new Point(e.getX() - (int) node.getX(), e.getY() - (int) node.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 释放节点
                draggedNode = null;
                mouseOffset = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    // 拖动节点时，更新节点的位置
                    int newX = e.getX() - mouseOffset.x;
                    int newY = e.getY() - mouseOffset.y;
                    draggedNode.setPosition(newX, newY);
                    repaint();  // 重绘图形
                }
            }
        });
    }

    private Node getNodeAtPosition(Point point) {
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            int x = (int) node.getX();
            int y = (int) node.getY();
            int r = nodeRadius;
            // 判断鼠标是否在节点的圆形区域内
            if (Math.pow(point.x - x, 2) + Math.pow(point.y - y, 2) <= r * r) {
                return node;
            }
        }
        return null;
    }

    /**
     * 方法功能：绘制图中的边
     */
    private void drawEdges(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(100, 100, 100)); // 边的颜色
        g2d.setStroke(new BasicStroke(2.0f)); // 边的宽度

        List<Edge> edges = graph.getEdges();
        for (Edge edge : edges) {
            Node start = edge.getStartNode();
            Node end = edge.getEndNode();
            int x1 = (int) start.getX();
            int y1 = (int) start.getY();
            int x2 = (int) end.getX();
            int y2 = (int) end.getY();

            // 绘制边的阴影
            g2d.setColor(new Color(0, 0, 0, 50)); // 半透明的黑色
            g2d.drawLine(x1 + 2, y1 + 2, x2 + 2, y2 + 2);

            // 绘制边
            g2d.setColor(new Color(100, 100, 100)); // 边的颜色
            g2d.drawLine(x1, y1, x2, y2);

            double weight = edge.getWeight();
            if (weight != 0) {
                String wText = String.valueOf(weight);
                int mx = (x1 + x2) / 2;
                int my = (y1 + y2) / 2;
                g2d.setColor(Color.BLUE); // 权重文本的颜色
                g2d.drawString(wText, mx, my);
            }

            if (edge.isDirected()) {
                drawArrow(g2d, x1, y1, x2, y2);
            }
        }
    }

    /**
     * 方法功能：绘制图中的节点
     */
    private void drawNodes(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            int x = (int) node.getX();
            int y = (int) node.getY();
            int width = 60;  // 电脑外框的宽度
            int height = 45; // 电脑外框的高度
            int screenHeight = 25; // 显示屏的高度
            int standHeight = 10;  // 支架的高度

            // 绘制电脑外框（矩形）
            g2d.setColor(Color.GRAY);
            g2d.fillRoundRect(x - width / 2, y - height / 2, width, height, 10, 10); // 圆角矩形

            // 绘制显示屏（浅蓝色矩形）
            g2d.setColor(new Color(173, 216, 230)); // 浅蓝色
            g2d.fillRoundRect(x - width / 2 + 5, y - height / 2 + 5, width - 10, screenHeight, 5, 5); // 圆角矩形

            // 绘制底部支架
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(x - width / 2 + 10, y + height / 2 - standHeight, width - 20, standHeight); // 底部支架

            // 绘制支架支撑点（小圆形）
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillOval(x - 5, y + height / 2 - standHeight + 2, 10, 10); // 支撑点（左）
            g2d.fillOval(x + width / 2 - 5, y + height / 2 - standHeight + 2, 10, 10); // 支撑点（右）

            // 绘制结点文字（ID），调整位置使其不被设备图标覆盖
            String nodeId = node.getId();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(nodeId);
            int textHeight = fm.getAscent();

            // 将 ID 文字绘制在设备上方
            int textX = x - textWidth / 2;
            int textY = y - height / 2 - 5; // 将 ID 文字放置在设备上方，并加上间距

            g2d.setColor(Color.BLACK); // ID文字的颜色
            g2d.drawString(nodeId, textX, textY);
        }
    }


    /**
     * 方法功能：绘制箭头，用于有向边
     */
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        int arrowSize = 8;
        double theta = Math.atan2(y2 - y1, x2 - x1);
        double angle = Math.toRadians(30);

        int xA = x2 - (int) (arrowSize * Math.cos(theta - angle));
        int yA = y2 - (int) (arrowSize * Math.sin(theta - angle));
        int xB = x2 - (int) (arrowSize * Math.cos(theta + angle));
        int yB = y2 - (int) (arrowSize * Math.sin(theta + angle));

        g2d.setColor(Color.RED); // 箭头的颜色
        g2d.drawLine(x2, y2, xA, yA);
        g2d.drawLine(x2, y2, xB, yB);
    }

    /**
     * 方法功能：刷新面板，重新绘制所有内容
     */
    public void refresh() {
        repaint();
    }

    /**
     * 方法功能：设置节点的半径
     */
    public void setNodeRadius(int radius) {
        this.nodeRadius = radius;
    }

    /**
     * 方法功能：获取节点的半径
     */
    public int getNodeRadius() {
        return this.nodeRadius;
    }

    /**
     * 方法功能：设置要高亮显示的节点路径
     */
    public void setHighlightPath(List<String> nodePath) {
        this.highlightNodePath = nodePath;
        // 根据 nodePath 中相邻节点对，在 edges 中找到对应边，存储到 highlightEdgePath
        // 便于后续在绘图时单独着色
        if (nodePath == null || nodePath.size() < 2) {
            highlightEdgePath = null;
            return;
        }
        highlightEdgePath = new ArrayList<>();
        for (int i = 0; i < nodePath.size()-1; i++) {
            String a = nodePath.get(i);
            String b = nodePath.get(i+1);
            // 在 graph 的 edges 中查找 a-b / a->b 边
            for (Edge e : graph.getEdges()) {
                String startId = e.getStartNode().getId();
                String endId   = e.getEndNode().getId();
                if (!e.isDirected()) {
                    // 无向
                    if ((startId.equals(a) && endId.equals(b)) ||
                            (startId.equals(b) && endId.equals(a))) {
                        highlightEdgePath.add(e);
                        break;
                    }
                } else {
                    // 有向
                    if (startId.equals(a) && endId.equals(b)) {
                        highlightEdgePath.add(e);
                        break;
                    }
                }
            }
        }
        repaint();
    }

    /**
     * 方法功能：重写 paintComponent 方法，执行绘制操作
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawEdges(g);
        drawNodes(g);

        if (highlightEdgePath != null) {
            drawHighlightEdges(g);
        }
        if (highlightNodePath != null) {
            drawHighlightNodes(g);
        }
    }

    /**
     * 方法功能：绘制高亮的边
     */
    private void drawHighlightEdges(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3f));
        for (Edge e : highlightEdgePath) {
            Node s = e.getStartNode();
            Node t = e.getEndNode();
            g2d.drawLine((int)s.getX(), (int)s.getY(), (int)t.getX(), (int)t.getY());
            if (e.isDirected()) {
                drawArrow(g2d, (int)s.getX(), (int)s.getY(), (int)t.getX(), (int)t.getY());
            }
        }
    }

    /**
     * 方法功能：绘制高亮的节点
     */
    private void drawHighlightNodes(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        for (String nid : highlightNodePath) {
            Node node = graph.getNodeById(nid);
            if (node == null) continue;
            int x = (int) node.getX();
            int y = (int) node.getY();
            g2d.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
        }
    }
}

