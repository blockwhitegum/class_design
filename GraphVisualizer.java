import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class GraphVisualizer extends JPanel {

    private Graph graph;
    private int nodeRadius = 20;
    private List<String> highlightNodePath;    // 要高亮的结点 ID 列表
    private List<Edge>  highlightEdgePath;    // 要高亮的边列表 (可选)

    public List<String> getHighlightPath() {
        return this.highlightNodePath;
    }

    public GraphVisualizer(Graph graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE); // 设置背景色
    }



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

    private void drawNodes(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            int x = (int) node.getX();
            int y = (int) node.getY();
            int r = nodeRadius;

            // 绘制结点的渐变效果
            GradientPaint gradient = new GradientPaint(x - r, y - r, Color.ORANGE, x + r, y + r, Color.YELLOW);
            g2d.setPaint(gradient);
            g2d.fillOval(x - r, y - r, 2 * r, 2 * r);

            // 绘制结点边框
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x - r, y - r, 2 * r, 2 * r);

            // 绘制结点文字（ID）
            String nodeId = node.getId();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(nodeId);
            int textHeight = fm.getAscent();
            int textX = x - textWidth / 2;
            int textY = y + textHeight / 4;
            g2d.setColor(Color.BLACK); // ID文字的颜色
            g2d.drawString(nodeId, textX, textY);
        }
    }

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

    public void refresh() {
        repaint();
    }

    public void setNodeRadius(int radius) {
        this.nodeRadius = radius;
    }

    public int getNodeRadius() {
        return this.nodeRadius;
    }
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

    private void drawHighlightEdges(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);  // 高亮颜色
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

    private void drawHighlightNodes(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        for (String nid : highlightNodePath) {
            Node node = graph.getNodeById(nid);
            if (node == null) continue;
            int x = (int) node.getX();
            int y = (int) node.getY();
            g2d.fillOval(x - nodeRadius, y - nodeRadius, 2*nodeRadius, 2*nodeRadius);
        }
    }

}
