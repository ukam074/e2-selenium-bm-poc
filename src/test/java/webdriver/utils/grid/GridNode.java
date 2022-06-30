package webdriver.utils.grid;

/**
 * GridNode represents a particular Node on the Selenium Grid
 */
public class GridNode {
    private String nodeIP;
    private int nodePort;

    public GridNode(String nodeIP, int nodePort) {
        this.nodeIP = nodeIP;
        this.nodePort = nodePort;
    }

    public String getNodeIP() {
        return nodeIP;
    }

    public void setNodeIP(String nodeIP) {
        this.nodeIP = nodeIP;
    }

    public int getNodePort() {
        return nodePort;
    }

    public void setNodePort(int nodePort) {
        this.nodePort = nodePort;
    }
}
