/**
 * Main.java
 * 入口类：初始化并启动图可视化应用程序。
 *
 * 说明：
 * 1. 本示例使用面向对象方式
 * 2. 若需要结合 Swing 或 JavaFX 等图形库，请在此处作相应初始化调用。
 */

public class Main {
    public static void main(String[] args) {
        // 1. 初始化数据结构（图对象），供后续使用。
            Graph graph = new Graph();
        // 3. 启动图形界面，可视化图并与用户互动。
            GraphVisualizer visualizer = new GraphVisualizer(graph);
            UIController uiController = new UIController(graph, visualizer);
            uiController.showUI();
        System.out.println("网络拓扑图可视化应用程序已启动。");
    }
}