/**
 * GraphVisualizer.java
 *
 * 用于将 Graph 数据结构可视化，基于 Swing 实现简单的绘制。
 * 你可以根据实际需求使用其他图形库（如 JavaFX）。
 */

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphVisualizer extends JPanel {

    private Graph graph;         // 要可视化的图
    private int nodeRadius = 20; // 结点绘制时的圆形半径

    /**
     * 构造方法，接收一个 Graph 对象
     */
    public GraphVisualizer(Graph graph) {
        this.graph = graph;
        // 设置面板大小，可根据需要调整
        setPreferredSize(new Dimension(800, 600));
    }

    /**
     * 重写 paintComponent 方法，在面板上绘制图中结点和边
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 如果有需要，可以在此设置背景色
        // setBackground(Color.WHITE);  // 通常在构造器或其它地方设置

        // 画边
        drawEdges(g);

        // 画结点
        drawNodes(g);
    }

    /**
     * 绘制所有边
     */
    private void drawEdges(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // 设置线条宽度、颜色等（示例为黑色、1.5px 宽）
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));

        List<Edge> edges = graph.getEdges();
        for (Edge edge : edges) {
            Node start = edge.getStartNode();
            Node end = edge.getEndNode();

            // 获取坐标
            int x1 = (int) start.getX();
            int y1 = (int) start.getY();
            int x2 = (int) end.getX();
            int y2 = (int) end.getY();

            // 绘制线
            g2d.drawLine(x1, y1, x2, y2);

            // 若有权重，绘制权重文本
            double weight = edge.getWeight();
            if (weight != 0) {
                String wText = String.valueOf(weight);
                // 在边的中点附近绘制
                int mx = (x1 + x2) / 2;
                int my = (y1 + y2) / 2;
                g2d.drawString(wText, mx, my);
            }

            // 如果是有向边，画一个箭头或简单三角形
            if (edge.isDirected()) {
                drawArrow(g2d, x1, y1, x2, y2);
            }
        }
    }

    /**
     * 绘制所有结点
     */
    private void drawNodes(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // 设置结点的填充颜色
        g2d.setColor(Color.ORANGE);

        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            int x = (int) node.getX();
            int y = (int) node.getY();

            // 在 (x, y) 的位置绘制一个圆（圆心在 x, y 上还需要做一些调整）
            // 使得 (x, y) 成为圆心而不是左上角
            int r = nodeRadius;
            g2d.fillOval(x - r, y - r, 2*r, 2*r);

            // 绘制结点边框
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x - r, y - r, 2*r, 2*r);

            // 绘制结点文字（ID）
            String nodeId = node.getId();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(nodeId);
            int textHeight = fm.getAscent();
            // 将文本在圆心水平、垂直居中
            int textX = x - textWidth / 2;
            int textY = y + textHeight / 4;
            g2d.drawString(nodeId, textX, textY);

            // 恢复填充色为橙色，便于下一次循环继续绘制
            g2d.setColor(Color.ORANGE);
        }
    }

    /**
     * 在有向边末端绘制箭头
     */
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        // 箭头大小参数，可根据需要调整
        int arrowSize = 8;

        // 计算边的角度
        double theta = Math.atan2(y2 - y1, x2 - x1);

        // 箭头的两个边线与边的夹角
        double angle = Math.toRadians(30);  // 30度夹角，可自行调节

        // 计算箭头两侧线段的终点坐标
        int xA = x2 - (int) (arrowSize * Math.cos(theta - angle));
        int yA = y2 - (int) (arrowSize * Math.sin(theta - angle));
        int xB = x2 - (int) (arrowSize * Math.cos(theta + angle));
        int yB = y2 - (int) (arrowSize * Math.sin(theta + angle));

        // 画两条线，形成箭头
        g2d.drawLine(x2, y2, xA, yA);
        g2d.drawLine(x2, y2, xB, yB);
    }

    /**
     * 刷新或重绘图形时调用
     * 当 Graph 数据更新后，可以调用此方法刷新界面
     */
    public void refresh() {
        // 触发 paintComponent 重绘
        repaint();
    }

    /**
     * 若需要在外部改变绘制参数，如 nodeRadius，可以提供 setter 方法
     */
    public void setNodeRadius(int radius) {
        this.nodeRadius = radius;
    }

    public int getNodeRadius() {
        return this.nodeRadius;
    }
}