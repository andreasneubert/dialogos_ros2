package rosPlugin;

import com.clt.diamant.*;
import com.clt.diamant.graph.Graph;
import com.clt.diamant.graph.Node;
import com.clt.diamant.graph.nodes.NodeExecutionException;
import com.clt.diamant.gui.NodePropertiesDialog;
import com.clt.script.exp.Value;
import com.clt.script.exp.types.ListType;
import com.clt.script.exp.values.IntValue;
import com.clt.script.exp.values.ListValue;
import com.clt.script.exp.values.RealValue;
import com.clt.script.exp.values.StringValue;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import org.xml.sax.SAXException;
import org.json.JSONObject;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import id.jros2client.JRos2ClientFactory;
import id.jros2client.JRos2Client;
import id.jrosclient.TopicSubscriber;
import id.jrosmessages.std_msgs.StringMessage;
import id.jrosmessages.std_msgs.Int32Message;

public class ROSInputNode extends Node {

    /** the topic to listen to */
    private static final String TOPIC = "dialogosTopic";
    /** the variable to write the result into (as a list) */
    private static final String RESULT_VAR = "resultVar";
    private static final String WAIT_FOR_MESSAGE = "waitForMessage";
    private static final String TIMEOUT = "timeout";

    private JRos2Client client;
    private String subscribedTopic = "dialogosTopic";
    private String currentMessage = "";

    private Semaphore semaphore = new Semaphore(0); // used to communicate a message change from subscriber to execute
                                                    // function

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ROSInputNode() {
        addEdge(); // have one port for an outgoing edge
        setProperty(TOPIC, ""); // avoid running into null-pointers later
        setProperty(RESULT_VAR, "");
        setProperty(WAIT_FOR_MESSAGE, Boolean.TRUE);
        setProperty(TIMEOUT, "");
        client = null;

        initializeClient();
        scheduleVariableCheckTask(); // Schedule the variable check task
    }

    // Method to initialize the JRos2Client
    private void initializeClient() {
        if (client == null) {
            try {
                client = new JRos2ClientFactory().createClient();

                String ros_topic = getProperty(TOPIC).toString();
                subscribedTopic = ros_topic;

                client.subscribe(new TopicSubscriber<>(StringMessage.class, ros_topic) {
                    @Override
                    public void onNext(StringMessage item) {
                        // System.out.println("------------------" + item);
                        currentMessage = item.toString();
                        getSubscription().get().request(1);
                        semaphore.release();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void scheduleVariableCheckTask() {
        double intervalSeconds = 0.2; // Adjust the interval as needed (e.g., 0.5 seconds)
        scheduler.scheduleAtFixedRate(this::checkSubscribedTopicChange, 0, (long) (intervalSeconds * 1000),
                TimeUnit.MILLISECONDS);
    }

    private void checkSubscribedTopicChange() {
        String ros_topic = getProperty(TOPIC).toString();

        if (subscribedTopic != ros_topic) {
            switchSubscriberClient();
            System.out.println("Switched subscriber");
        }
    }

    private void switchSubscriberClient() {
        String ros_topic = getProperty(TOPIC).toString();
        if (!ros_topic.equals(subscribedTopic)) {
            closeClient();
            try {
                client = new JRos2ClientFactory().createClient();
                subscribedTopic = ros_topic;
                client.subscribe(new TopicSubscriber<>(StringMessage.class, subscribedTopic) {
                    @Override
                    public void onNext(StringMessage item) {
                        String jsonMessage = item.toString();
                        JSONObject jsonObject = new JSONObject(jsonMessage);
                        String data = jsonObject.getString("data");
                        currentMessage = data;
                        semaphore.release();
                        // System.out.println(data);
                        getSubscription().get().request(1);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void closeClient() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client = null;
            }
        }
    }

    @Override
    public Node execute(WozInterface wozInterface, InputCenter inputCenter, ExecutionLogger executionLogger) {

        List<String> messages = new ArrayList<>();
        String message_string = "";
        switchSubscriberClient();
        if (getBooleanProperty(WAIT_FOR_MESSAGE)) {
            try {
                currentMessage = "";
                long startTime = System.currentTimeMillis();
                long timeout = Long.parseLong(getProperty(TIMEOUT).toString());
                synchronized (semaphore) {
                    while (currentMessage.isEmpty()) {
                        long elapsedTime = System.currentTimeMillis() - startTime;
                        long remainingTime = timeout - elapsedTime;
                        if (remainingTime <= 0) {
                            break; // Break if timeout reached
                        }
                        semaphore.tryAcquire(remainingTime, TimeUnit.MILLISECONDS);
                    }

                    if (!currentMessage.isEmpty()) {
                        System.out.println("Message received: " + currentMessage);
                        if (currentMessage != null) {
                            messages.add(currentMessage);
                            message_string = currentMessage;
                        }
                    } else {
                        System.out.println("Timeout: No message received within the specified time.");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // closeClient();
        // System.out.println("exit after: " + topicName);
        ListValue messageList = new ListValue(messages.stream().map(StringValue::new).collect(Collectors.toList()));
        // set variable to result of query
        String varName = getProperty(RESULT_VAR).toString();
        Slot var = getSlot(varName);
        var.setValue(messageList);
        return getEdge(0).getTarget();
    }

    /** get the variable slot from the graph that matches the name */
    private Slot getSlot(String name) {
        List<Slot> slots = getListVariables();
        for (Slot slot : slots) {
            if (name.equals(slot.getName()))
                return slot;
        }
        throw new NodeExecutionException(this, "unable to find list variable with name " + name);
    }

    private List<Slot> getListVariables() {
        return this.getGraph().getAllVariables(Graph.LOCAL).stream().filter(slot -> slot.getType() instanceof ListType)
                .collect(Collectors.toList());
    }

    @Override
    public JComponent createEditorComponent(Map<String, Object> properties) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JPanel horiz = new JPanel();
        horiz.add(new JLabel("ROS topic "));
        horiz.add(NodePropertiesDialog.createTextField(properties, TOPIC));
        p.add(horiz);

        horiz = new JPanel();
        horiz.add(new JLabel("return list of results to: "));
        horiz.add(NodePropertiesDialog.createComboBox(properties, RESULT_VAR,
                getListVariables()));
        p.add(horiz);

        JCheckBox waitBox = NodePropertiesDialog.createCheckBox(properties, WAIT_FOR_MESSAGE,
                "wait for at least one message");
        p.add(waitBox);
        final JPanel fhoriz = new JPanel();
        fhoriz.add(new JLabel("wait no longer than: "));
        fhoriz.add(NodePropertiesDialog.createTextField(properties, TIMEOUT));
        fhoriz.add(new JLabel(" milliseconds"));
        waitBox.addItemListener(l -> {
            for (Component c : fhoriz.getComponents()) {
                c.setEnabled(waitBox.isSelected());
            }
        });
        p.add(fhoriz);

        return p;
    }

    @Override
    public void writeAttributes(XMLWriter out, IdMap uid_map) {
        super.writeAttributes(out, uid_map);
        Graph.printAtt(out, TOPIC, this.getProperty(TOPIC).toString());
        Slot v = (Slot) this.getProperty(RESULT_VAR);
        if (v != null) {
            try {
                String uid = uid_map.variables.getKey(v);
                Graph.printAtt(out, RESULT_VAR, uid);
            } catch (Exception exn) {
            } // variable deleted
        }
        Graph.printAtt(out, WAIT_FOR_MESSAGE, this.getProperty(WAIT_FOR_MESSAGE).toString());
        Graph.printAtt(out, TIMEOUT, this.getProperty(TIMEOUT).toString());
    }

    @Override
    public void readAttribute(XMLReader r, String name, String value, IdMap uid_map) throws SAXException {
        if (name.equals(TOPIC)) {
            setProperty(name, value);
        } else if (name.equals(RESULT_VAR) & value != null) {
            try {
                this.setProperty(name, uid_map.variables.get(value));
            } catch (Exception exn) {
                this.setProperty(name, value);
            }
        } else if (name.equals(WAIT_FOR_MESSAGE)) {
            setProperty(name, Boolean.valueOf(value));
        } else if (name.equals(TIMEOUT) & value != null) {
            setProperty(name, value);
        } else {
            super.readAttribute(r, name, value, uid_map);
        }
    }

    @Override
    public void writeVoiceXML(XMLWriter xmlWriter, IdMap idMap) {
    }

}
