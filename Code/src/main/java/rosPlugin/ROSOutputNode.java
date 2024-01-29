package rosPlugin;

import com.clt.diamant.ExecutionLogger;
import com.clt.diamant.IdMap;
import com.clt.diamant.InputCenter;
import com.clt.diamant.WozInterface;
import com.clt.diamant.graph.Graph;
import com.clt.diamant.graph.Node;
import com.clt.diamant.gui.NodePropertiesDialog;
import com.clt.script.exp.Value;
import com.clt.script.exp.values.StringValue;
import com.clt.xml.XMLReader;
import com.clt.xml.XMLWriter;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.util.Map;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import id.jros2client.JRos2ClientFactory;
import id.jrosclient.TopicSubmissionPublisher;
import id.jrosmessages.std_msgs.StringMessage;
import id.jrosmessages.std_msgs.Int32Message;

public class ROSOutputNode extends Node {
    public static final String TOPIC = "rosTopic";
    private static final String MESSAGE = "rosMessageExpression";

    private String ROSMESSAGETYPE = "String"; // String, Int32

    public ROSOutputNode() {
        this.addEdge(); // have one port for an outgoing edge
        this.setProperty(TOPIC, "");
        this.setProperty(MESSAGE, "");
        setNodeTitle(TOPIC);
    }

    @Override
    public Node execute(WozInterface wozInterface, InputCenter inputCenter, ExecutionLogger executionLogger) {

        var client = new JRos2ClientFactory().createClient();
        String ros_topic = getProperty(TOPIC).toString();
        String message = getProperty(MESSAGE).toString();
        System.out.println("message type: " + ROSMESSAGETYPE);
        try {
            if ("String".equals(ROSMESSAGETYPE)) {
                var publisher = new TopicSubmissionPublisher<>(StringMessage.class, ros_topic);
                client.publish(publisher);
                publisher.submit(new StringMessage().withData(message));
            } else if ("Int32".equals(ROSMESSAGETYPE)) {
                var publisher = new TopicSubmissionPublisher<>(Int32Message.class, ros_topic);
                client.publish(publisher);
                publisher.submit(new Int32Message().withData(Integer.parseInt(message)));
            } else {
                throw new IllegalArgumentException("Unsupported ROS message type: " + ROSMESSAGETYPE);
            }

            System.out.println("Topic: " + ros_topic + " message: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getEdge(0).getTarget();

    }

    @Override
    public JComponent createEditorComponent(Map<String, Object> properties) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JPanel horiz = new JPanel();
        horiz.add(new JLabel("ROS topic"));
        horiz.add(NodePropertiesDialog.createTextField(properties, TOPIC));
        p.add(horiz);

        // Drop-down menu for ros message type
        // Add drop-down menu for additional options
        String[] options = { "String", "Int32" };
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setSelectedItem(ROSMESSAGETYPE);

        // Add an ActionListener to update the selected option
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ROSMESSAGETYPE = (String) comboBox.getSelectedItem();
                System.out.println("ROSMESSAGE TYPE: " + ROSMESSAGETYPE);
                setNodeTitle(getProperty(TOPIC).toString());

            }
        });


        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dropdownPanel.add(new JLabel("Select RosMessageType"));
        dropdownPanel.add(comboBox);
        p.add(dropdownPanel);

        horiz = new JPanel();
        horiz.add(new JLabel("message expression"));
        horiz.add(NodePropertiesDialog.createTextField(properties, MESSAGE));
        p.add(horiz);

        return p;
    }

    @Override
    public void writeAttributes(XMLWriter out, IdMap uid_map) {
        super.writeAttributes(out, uid_map);
        Graph.printAtt(out, TOPIC, this.getProperty(TOPIC).toString());
        Graph.printAtt(out, MESSAGE, this.getProperty(MESSAGE).toString());
    }

    @Override
    public void readAttribute(XMLReader r, String name, String value, IdMap uid_map) throws SAXException {

        if (name.equals(TOPIC) || name.equals(MESSAGE)) {
            this.setProperty(name, value);
        } else {
            super.readAttribute(r, name, value, uid_map);
        }
    }

    @Override
    public void writeVoiceXML(XMLWriter xmlWriter, IdMap idMap) {
    }

    public String getRosMessageType() {
        return ROSMESSAGETYPE;
    }

    // Method to set the title
    public void setNodeTitle(String newTitle) {
        super.setTitle(newTitle);
        System.out.println("setNodeTitle: " + newTitle);
    }

    private void onEditorClosed() {
        // Your code here...
        System.out.println("Editor closed!");
    }

}