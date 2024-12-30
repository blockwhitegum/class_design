/**
 * Main.java
 * 入口类：初始化并启动图可视化应用程序。
 *
 * 说明：
 * 1. 本示例使用面向对象方式，将图的可视化与控制台命令行处理分离到其他类中。
 * 2. 若需要结合 Swing 或 JavaFX 等图形库，请在此处作相应初始化调用。
 */

public class Main {
    public static void main(String[] args) {
        // 1. 初始化数据结构（图对象），供后续使用。
        //    （Graph、Node、Edge 等类将在其他文件中定义）
            Graph graph = new Graph();

        // 3. 启动图形界面，可视化图并与用户互动。
        //    （GraphVisualizer、UIController 等在后续文件中定义）
            GraphVisualizer visualizer = new GraphVisualizer(graph);
            UIController uiController = new UIController(graph, visualizer);
            uiController.showUI();

        // 在此处添加程序其他必需的初始化逻辑，如加载配置文件等。
        // -----------------------------------------------------------
        // 由于其他类尚未编写，这里仅示例性地展示流程。

        System.out.println("网络拓扑图可视化应用程序已启动。");
    }
}