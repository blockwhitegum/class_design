/**
 * ConsoleInterface.java
 *
 * 通过控制台与用户交互，解析用户指令并对 Graph 进行操作。
 * 支持示例命令：
 *   - add node A
 *   - add edge A B 2
 *   - remove node A
 *   - remove edge A B
 *   - print graph
 *   - exit
 */

import java.util.Scanner;

public class ConsoleInterface {

    private Graph graph;
    private Scanner scanner;
    private boolean running;

    /**
     * 构造方法，传入要操作的 Graph 对象
     * @param graph 要进行操作的图数据结构
     */
    public ConsoleInterface(Graph graph) {
        this.graph = graph;
        this.scanner = new Scanner(System.in);
        this.running = false;
    }

    /**
     * 启动控制台交互
     */
    public void start() {
        running = true;
        System.out.println("控制台交互已启动。可输入指令（如：add node A、add edge A B 2、remove edge A B、print graph、exit）：");

        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            processCommand(input);
        }

        scanner.close();
        System.out.println("控制台交互已结束。");
    }

    /**
     * 解析并执行用户输入的命令
     * @param command 用户输入的命令行字符串
     */
    private void processCommand(String command) {
        if (command.isEmpty()) {
            return;
        }

        // 将命令分割为若干部分
        String[] parts = command.split("\\s+");
        String mainCmd = parts[0].toLowerCase();  // 主命令（如 add/remove/print/exit等）

        switch (mainCmd) {
            case "add":
                handleAddCommand(parts);
                break;
            case "remove":
                handleRemoveCommand(parts);
                break;
            case "print":
                handlePrintCommand(parts);
                break;
            case "exit":
                running = false;
                break;
            default:
                System.out.println("未知命令：" + mainCmd + "，请重试。");
                break;
        }
    }

    /**
     * 处理 add 系列命令
     * @param parts 命令拆分数组
     */
    private void handleAddCommand(String[] parts) {
        // 格式可能是：
        //   add node A
        //   add edge A B 2
        if (parts.length < 3) {
            System.out.println("add 命令格式错误，请参考：add node A 或 add edge A B 2");
            return;
        }

        String subCmd = parts[1].toLowerCase(); // node 或 edge

        if ("node".equals(subCmd)) {
            // add node A
            if (parts.length != 3) {
                System.out.println("命令格式错误，请使用：add node <NodeId>");
                return;
            }
            String nodeId = parts[2];
            Node newNode = new Node(nodeId);

            if (graph.addNode(newNode)) {
                System.out.println("已添加结点：" + nodeId);
            } else {
                System.out.println("添加结点失败，可能已存在相同 ID 的结点。");
            }
        } else if ("edge".equals(subCmd)) {
            // add edge A B 2 或不带权重的命令可自行扩展
            if (parts.length < 4) {
                System.out.println("命令格式错误，请使用：add edge <StartNodeId> <EndNodeId> <Weight>");
                return;
            }
            String startId = parts[2];
            String endId   = parts[3];
            double weight  = 1.0;  // 默认权重
            boolean directed = false;  // 可扩展解析有向标记

            // 如果还含有第四个或更多参数，则解析为权重
            if (parts.length >= 5) {
                try {
                    weight = Double.parseDouble(parts[4]);
                } catch (NumberFormatException e) {
                    System.out.println("边权重解析失败，请输入数值类型的权重。默认为 1.0。");
                }
            }

            Node startNode = graph.getNodeById(startId);
            Node endNode   = graph.getNodeById(endId);

            if (startNode == null || endNode == null) {
                System.out.println("添加边失败：起始或终止结点不存在。");
                return;
            }

            // 可根据需求决定有向/无向
            Edge newEdge = new Edge(startNode, endNode, weight, directed);

            if (graph.addEdge(newEdge)) {
                System.out.println("已添加边：" + startId + " - " + endId + " (权重=" + weight + ")");
            } else {
                System.out.println("添加边失败，可能已存在相同的边或结点未找到。");
            }
        } else {
            System.out.println("未知 add 子命令：" + subCmd + "，请使用 node 或 edge。");
        }
    }

    /**
     * 处理 remove 系列命令
     * @param parts 命令拆分数组
     */
    private void handleRemoveCommand(String[] parts) {
        // 格式可能是：
        //   remove node A
        //   remove edge A B
        if (parts.length < 3) {
            System.out.println("remove 命令格式错误，请参考：remove node A 或 remove edge A B");
            return;
        }

        String subCmd = parts[1].toLowerCase(); // node 或 edge

        if ("node".equals(subCmd)) {
            // remove node A
            if (parts.length != 3) {
                System.out.println("命令格式错误，请使用：remove node <NodeId>");
                return;
            }
            String nodeId = parts[2];
            if (graph.removeNode(nodeId)) {
                System.out.println("已删除结点：" + nodeId);
            } else {
                System.out.println("删除结点失败，可能不存在该 ID 的结点。");
            }
        } else if ("edge".equals(subCmd)) {
            // remove edge A B
            if (parts.length != 4) {
                System.out.println("命令格式错误，请使用：remove edge <StartNodeId> <EndNodeId>");
                return;
            }
            String startId = parts[2];
            String endId   = parts[3];
            if (graph.removeEdge(startId, endId)) {
                System.out.println("已删除边：" + startId + " - " + endId);
            } else {
                System.out.println("删除边失败，可能不存在对应的边。");
            }
        } else {
            System.out.println("未知 remove 子命令：" + subCmd + "，请使用 node 或 edge。");
        }
    }

    /**
     * 处理 print 命令
     * @param parts 命令拆分数组
     */
    private void handlePrintCommand(String[] parts) {
        // 这里可扩展更多子命令，如：print matrix、print adjacency-list 等
        // 若仅有 "print graph" 就简单处理
        if (parts.length == 2 && "graph".equalsIgnoreCase(parts[1])) {
            graph.printGraphInfo();
        } else {
            System.out.println("可用的打印命令：print graph");
        }
    }
}