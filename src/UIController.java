/**
 * UIController.java
 *
 * 管理 Swing 图形界面，与用户进行简单的交互，并与 Graph 和 GraphVisualizer 关联。
 * 需在 Main 中创建并调用 showUI() 方法，以启动图形化界面。
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIController {

    private Graph graph;
    private GraphVisualizer visualizer;

    // 主窗口
    private JFrame frame;

    /**
     * 构造方法
     * @param graph      用于存储和操作结点、边的数据结构
     * @param visualizer 用于将 Graph 可视化的面板
     */
    public UIController(Graph graph, GraphVisualizer visualizer) {
        this.graph = graph;
        this.visualizer = visualizer;
    }

    /**
     * 创建并显示图形化界面
     */
    public void showUI() {
        // 1. 创建主窗口
        frame = new JFrame("图可视化应用程序 - Swing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 2. 将 GraphVisualizer 面板加入主窗口中心
        frame.add(visualizer, BorderLayout.CENTER);

        // 3. 创建一个操作面板，放置若干按钮，用于简单的增删结点演示
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 3.1 添加结点的示例按钮
        JButton addNodeButton = new JButton("添加结点");
        addNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 可以弹出一个对话框，获取用户输入的结点 ID
                String nodeId = JOptionPane.showInputDialog(frame, "请输入新结点ID：", "添加结点", JOptionPane.PLAIN_MESSAGE);
                if (nodeId != null && !nodeId.trim().isEmpty()) {
                    // 创建新的结点，并指定一个随机坐标（此处仅示例）
                    Node newNode = new Node(nodeId.trim(), Math.random() * 700 + 50, Math.random() * 500 + 50);

                    // 调用 Graph 的 addNode 方法
                    boolean added = graph.addNode(newNode);
                    if (added) {
                        JOptionPane.showMessageDialog(frame, "结点 " + nodeId + " 添加成功！");
                        // 刷新可视化面板
                        visualizer.refresh();
                    } else {
                        JOptionPane.showMessageDialog(frame, "添加失败，已存在相同ID的结点！");
                    }
                }
            }
        });
        controlPanel.add(addNodeButton);

        // 3.2 删除结点的示例按钮
        JButton removeNodeButton = new JButton("删除结点");
        removeNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 让用户输入要删除的结点 ID
                String nodeId = JOptionPane.showInputDialog(frame, "请输入要删除的结点ID：", "删除结点", JOptionPane.PLAIN_MESSAGE);
                if (nodeId != null && !nodeId.trim().isEmpty()) {
                    boolean removed = graph.removeNode(nodeId.trim());
                    if (removed) {
                        JOptionPane.showMessageDialog(frame, "结点 " + nodeId + " 已删除！");
                        // 刷新可视化面板
                        visualizer.refresh();
                    } else {
                        JOptionPane.showMessageDialog(frame, "删除失败，可能不存在该结点ID。");
                    }
                }
            }
        });
        controlPanel.add(removeNodeButton);

        // 3.3 添加边的示例按钮
        JButton addEdgeButton = new JButton("添加边");
        addEdgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 弹出一个对话框，引导用户输入起点ID、终点ID、权重
                JTextField startField = new JTextField();
                JTextField endField = new JTextField();
                JTextField weightField = new JTextField("1.0"); // 默认值

                Object[] message = {
                        "起点ID：", startField,
                        "终点ID：", endField,
                        "权重：", weightField
                };

                int option = JOptionPane.showConfirmDialog(
                        frame, message, "添加边", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String startId = startField.getText().trim();
                    String endId   = endField.getText().trim();
                    double weight  = 1.0;
                    try {
                        weight = Double.parseDouble(weightField.getText().trim());
                    } catch (NumberFormatException ex) {
                        // 若用户输入非数字，则保持默认 1.0
                    }

                    Node startNode = graph.getNodeById(startId);
                    Node endNode   = graph.getNodeById(endId);

                    if (startNode == null || endNode == null) {
                        JOptionPane.showMessageDialog(frame,
                                "起点或终点结点不存在，请先添加对应结点！");
                        return;
                    }

                    // 这里可根据需求决定是否有向
                    boolean directed = false;
                    Edge newEdge = new Edge(startNode, endNode, weight, directed);

                    if (graph.addEdge(newEdge)) {
                        JOptionPane.showMessageDialog(frame,
                                "成功添加边：" + startId + " - " + endId + " (权重=" + weight + ")");
                        visualizer.refresh();
                    } else {
                        JOptionPane.showMessageDialog(frame, "添加边失败，可能已存在相同的边！");
                    }
                }
            }
        });
        controlPanel.add(addEdgeButton);

        // 3.4 删除边的示例按钮
        JButton removeEdgeButton = new JButton("删除边");
        removeEdgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField startField = new JTextField();
                JTextField endField = new JTextField();

                Object[] message = {
                        "起点ID：", startField,
                        "终点ID：", endField
                };

                int option = JOptionPane.showConfirmDialog(
                        frame, message, "删除边", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String startId = startField.getText().trim();
                    String endId   = endField.getText().trim();

                    if (graph.removeEdge(startId, endId)) {
                        JOptionPane.showMessageDialog(frame,
                                "已删除边：" + startId + " - " + endId);
                        visualizer.refresh();
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "删除边失败，可能不存在该边或输入有误。");
                    }
                }
            }
        });
        controlPanel.add(removeEdgeButton);

        // 3.5 刷新按钮
        JButton refreshButton = new JButton("刷新视图");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizer.refresh();
            }
        });
        controlPanel.add(refreshButton);

        // 将操作面板放在窗口顶部（或根据需要放在其它位置）
        frame.add(controlPanel, BorderLayout.NORTH);

        // 4. 设置窗口大小并显示
        frame.pack();
        frame.setLocationRelativeTo(null); // 居中
        frame.setVisible(true);
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