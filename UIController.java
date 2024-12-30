import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UIController {

    private Graph graph;
    private GraphVisualizer visualizer;
    private List<String> lastHighlightPath = null;  // 用于记录上一次的高亮路径
    private JFrame frame;

    public UIController(Graph graph, GraphVisualizer visualizer) {
        this.graph = graph;
        this.visualizer = visualizer;
    }

    public void showUI() {
        // 创建主窗口
        frame = new JFrame("网络拓扑图");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 将 GraphVisualizer 面板加入主窗口中心
        frame.add(visualizer, BorderLayout.CENTER);

        // 创建操作面板
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 添加结点按钮
        JButton addNodeButton = new JButton("添加设备");
        addNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nodeId = JOptionPane.showInputDialog(frame, "请输入新设备名称：", "添加设备", JOptionPane.PLAIN_MESSAGE);
                if (nodeId != null && !nodeId.trim().isEmpty()) {
                    Node newNode = new Node(nodeId.trim(), Math.random() * 700 + 50, Math.random() * 500 + 50);
                    boolean added = graph.addNode(newNode);
                    if (added) {
                        JOptionPane.showMessageDialog(frame, "设备" + nodeId + " 添加成功！");
                        visualizer.refresh();
                        recordLastOperationState();  // 更新状态
                    } else {
                        JOptionPane.showMessageDialog(frame, "添加失败，已存在相同名称的设备！");
                    }
                }
            }
        });
        controlPanel.add(addNodeButton);

        // 删除结点按钮
        JButton removeNodeButton = new JButton("删除设备");
        removeNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nodeId = JOptionPane.showInputDialog(frame, "请输入要删除的设备名称：", "删除设备", JOptionPane.PLAIN_MESSAGE);
                if (nodeId != null && !nodeId.trim().isEmpty()) {
                    boolean removed = graph.removeNode(nodeId.trim());
                    if (removed) {
                        JOptionPane.showMessageDialog(frame, "设备" + nodeId + " 已删除！");
                        visualizer.refresh();
                        recordLastOperationState();  // 更新状态
                    } else {
                        JOptionPane.showMessageDialog(frame, "删除失败，可能不存在该设备。");
                    }
                }
            }
        });
        controlPanel.add(removeNodeButton);

        // 添加边按钮
        JButton addEdgeButton = new JButton("添加设备连接");
        addEdgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField startField = new JTextField();
                JTextField endField = new JTextField();
                JTextField weightField = new JTextField("1.0");

                Object[] message = {
                        "起始设备：", startField,
                        "终止设备：", endField,
                        "权重：", weightField
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "添加设备连接", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String startId = startField.getText().trim();
                    String endId = endField.getText().trim();
                    double weight = 1.0;
                    try {
                        weight = Double.parseDouble(weightField.getText().trim());
                    } catch (NumberFormatException ex) {
                        // 保持默认 1.0
                    }

                    Node startNode = graph.getNodeById(startId);
                    Node endNode = graph.getNodeById(endId);

                    if (startNode == null || endNode == null) {
                        JOptionPane.showMessageDialog(frame, "起始设备或终止设备不存在，请先添加对应设备！");
                        return;
                    }

                    boolean directed = false;
                    Edge newEdge = new Edge(startNode, endNode, weight, directed);

                    if (graph.addEdge(newEdge)) {
                        JOptionPane.showMessageDialog(frame, "成功连接设备：" + startId + " - " + endId + " (权重=" + weight + ")");
                        visualizer.refresh();
                        recordLastOperationState();  // 更新状态
                    } else {
                        JOptionPane.showMessageDialog(frame, "连接设备失败，可能已存在相同的连接！");
                    }
                }
            }
        });
        controlPanel.add(addEdgeButton);

        // 删除边按钮
        JButton removeEdgeButton = new JButton("删除设备连接");
        removeEdgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField startField = new JTextField();
                JTextField endField = new JTextField();

                Object[] message = {
                        "起始设备：", startField,
                        "终止设备：", endField
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "删除设备连接", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String startId = startField.getText().trim();
                    String endId = endField.getText().trim();

                    if (graph.removeEdge(startId, endId)) {
                        JOptionPane.showMessageDialog(frame, "已删除设备连接：" + startId + " - " + endId);
                        visualizer.refresh();
                        recordLastOperationState();  // 更新状态
                    } else {
                        JOptionPane.showMessageDialog(frame, "删除设备连接失败，可能不存在该连接或输入有误。");
                    }
                }
            }
        });
        controlPanel.add(removeEdgeButton);

        // 查询最短路径按钮
        JButton shortestPathButton = new JButton("查询最短路径");
        shortestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField startField = new JTextField();
                JTextField endField = new JTextField();

                String[] algoOptions = {"BFS(无权)", "Dijkstra(非负权)", "Floyd-Warshall"};
                JComboBox<String> algoCombo = new JComboBox<>(algoOptions);

                Object[] message = {
                        "起点ID：", startField,
                        "终点ID：", endField,
                        "选择算法：", algoCombo
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "最短路径查询", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String startId = startField.getText().trim();
                    String endId = endField.getText().trim();
                    String algo = (String) algoCombo.getSelectedItem();
                    if (startId.isEmpty() || endId.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "起点或终点 ID 不能为空！");
                        return;
                    }

                    GraphAlgorithms.ShortestPathResult result = null;
                    switch (algo) {
                        case "BFS(无权)":
                            result = GraphAlgorithms.findShortestPathBFS(graph, startId, endId);
                            break;
                        case "Dijkstra(非负权)":
                            result = GraphAlgorithms.findShortestPathDijkstra(graph, startId, endId);
                            break;
                        case "Floyd-Warshall":
                            GraphAlgorithms.FloydResult fr = GraphAlgorithms.floydWarshall(graph);
                            result = GraphAlgorithms.rebuildFloydPath(fr, startId, endId);
                            break;
                        default:
                            break;
                    }

                    if (result == null) {
                        JOptionPane.showMessageDialog(frame, "未找到有效路径，可能两节点间不可达！");
                    } else {
                        // 显示查询结果
                        JOptionPane.showMessageDialog(frame, "找到路径：" + result.getPath() + "\n总距离=" + result.getDistance());

                        // 高亮显示最短路径
                        visualizer.setHighlightPath(result.getPath());
                        // 更新记录的高亮路径
                        lastHighlightPath = result.getPath();
                    }
                }
            }
        });
        controlPanel.add(shortestPathButton);

// 刷新视图按钮
        JButton refreshButton = new JButton("刷新网络拓扑图");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 清除高亮路径
                visualizer.setHighlightPath(null);
                // 重绘视图
                visualizer.refresh();
                // 清空记录的高亮路径，确保下次不会恢复
                lastHighlightPath = null;
            }
        });
        controlPanel.add(refreshButton);


        // 将操作面板放在窗口顶部
        frame.add(controlPanel, BorderLayout.NORTH);

        // 设置窗口大小并显示
        frame.pack();
        frame.setLocationRelativeTo(null); // 居中
        frame.setVisible(true);
    }

    /**
     * 记录上次操作的状态，以便在刷新时恢复。
     */
    private void recordLastOperationState() {
        // 保存当前高亮路径
        this.lastHighlightPath = visualizer.getHighlightPath();
    }

    /**
     * 若需要动态关闭或销毁界面，可提供相应方法
     */
    public void closeUI() {
        if (frame != null) {
            frame.dispose();
        }
    }
}
