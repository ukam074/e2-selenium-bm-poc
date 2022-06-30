package webdriver.utils.grid;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.openqa.selenium.remote.SessionId;

public class GridHelper {
    private String gridHostName;
    private int gridPort;
    // implement architecture later via 'Grid' POJO

    private static final Logger LOGGER = Logger.getLogger(GridHelper.class.getCanonicalName());

    public GridHelper(String gridHostName, int gridPort) {
        this.gridHostName = gridHostName;
        this.gridPort = gridPort;
    }

    /**
     * Returns a GridNode and emits session information about that particular GridNode
     * @param sessionID The current RemoteWebdriver SessionID
     * @return GridNode
     */
    public GridNode getNodeSessionInformation(SessionId sessionID) {
        GridNode node = null;
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = null;

        try {
            URL url = new URL("http://" + gridHostName + ":" + gridPort + "/grid/api/testsession?session=" + sessionID);
            HttpGet request = new HttpGet(url.toExternalForm());
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
            URL tempURL = new URL(jsonObject.getString("proxyId"));
            node = new GridNode(tempURL.getHost(), tempURL.getPort());
        } catch (Exception e) {
            String errorMessage = "An exception occurred acquiring remote webdriver node information. DEBUG:";
            LOGGER.log(Level.SEVERE, errorMessage, e);
        }

        LOGGER.info("Session " + sessionID + " was routed to " + "http://" + node.getNodeIP() + ":" + node.getNodePort());
        return node;
    }
}
