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
import id.jrosmessages.geometry_msgs.TwistMessage;
import id.jrosmessages.geometry_msgs.Vector3Message;

public class ROSOutputNode extends Node {
    public static final String TOPIC = "rosTopic";
    private static final String MESSAGE = "rosMessageExpression";
    private static final String X_VAL = "0.00";
    private static final String Y_VAL = "0.01";
    private static final String Z_VAL = "0.02";

    boolean isVectorType = false;

    private String ROSMESSAGETYPE = "String"; // String, Int32

    public ROSOutputNode() {
        this.addEdge(); // have one port for an outgoing edge
        this.setProperty(TOPIC, "");
        this.setProperty(MESSAGE, "");
        this.setProperty(X_VAL, "");
        this.setProperty(Y_VAL, "");
        this.setProperty(Z_VAL, "");

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
                publisher.close();
            } else if ("Int32".equals(ROSMESSAGETYPE)) {
                var publisher = new TopicSubmissionPublisher<>(Int32Message.class, ros_topic);
                client.publish(publisher);
                publisher.submit(new Int32Message().withData(Integer.parseInt(message)));
                publisher.close();
            } else if ("geometry_msgs/Twist/Linear".equals(ROSMESSAGETYPE)) {
                var publisher = new TopicSubmissionPublisher<>(TwistMessage.class, ros_topic);

                Double x_val = Double.parseDouble(getProperty(X_VAL).toString());
                Double y_val = Double.parseDouble(getProperty(Y_VAL).toString());
                Double z_val = Double.parseDouble(getProperty(Z_VAL).toString());
                client.publish(publisher);

                Vector3Message vec_value = new Vector3Message(x_val, y_val, z_val);
                Vector3Message vec_zero = new Vector3Message(0,0, 0);
                publisher.submit(new TwistMessage().withLinear(vec_value).withAngular(vec_zero));

                publisher.close();
            } else if ("geometry_msgs/Twist/Angular".equals(ROSMESSAGETYPE)) {
                var publisher = new TopicSubmissionPublisher<>(TwistMessage.class, ros_topic);

                Double x_val = Double.parseDouble(getProperty(X_VAL).toString());
                Double y_val = Double.parseDouble(getProperty(Y_VAL).toString());
                Double z_val = Double.parseDouble(getProperty(Z_VAL).toString());
                client.publish(publisher);

                Vector3Message vec_value = new Vector3Message(x_val, y_val, z_val);
                Vector3Message vec_zero = new Vector3Message(0, 0, 0);
                publisher.submit(new TwistMessage().withLinear(vec_zero).withAngular(vec_value));

                publisher.close();
            } else {
                throw new IllegalArgumentException("Unsupported ROS message type: " + ROSMESSAGETYPE);
            }

            System.out.println("Topic: " + ros_topic + " message: " + message);
        } catch (

        Exception e) {
            e.printStackTrace();
        }

        return

        getEdge(0).getTarget();

    }

    @Override
    public JComponent createEditorComponent(Map<String, Object> properties) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    
        // Panel for ROS topic input
        JPanel horiz = new JPanel();
        horiz.add(new JLabel("ROS topic"));
        horiz.add(NodePropertiesDialog.createTextField(properties, TOPIC));
        p.add(horiz);
    
        // Drop-down menu for ROS message type
        String[] options = { "String", "Int32", "geometry_msgs/Twist/Linear", "geometry_msgs/Twist/Angular" };
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setSelectedItem(ROSMESSAGETYPE);
    
        // ActionListener to update the selected option
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ROSMESSAGETYPE = (String) comboBox.getSelectedItem();
                if (ROSMESSAGETYPE.equals("geometry_msgs/Twist/Linear")
                        || ROSMESSAGETYPE.equals("geometry_msgs/Twist/Angular")) {
                    if (!isVectorType) {
                        isVectorType = true;
                        addVectorInputPanel(p, properties);
                        removeMessageInputPanel(p); // Remove message input panel
                    }
                } else {
                    isVectorType = false;
                    removeMessageInputPanel(p);
                    removeVectorInputPanel(p);
                    addMessageInputPanel(p, properties); // Add message input panel
                }
    
                System.out.println("ROSMESSAGE TYPE: " + ROSMESSAGETYPE);
    
                // Update node title
                setNodeTitle(getProperty(TOPIC).toString());
    
            }
        });
    
        // Panel for ROS message type selection
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dropdownPanel.add(new JLabel("Select RosMessageType"));
        dropdownPanel.add(comboBox);
        p.add(dropdownPanel);
    
        if (!isVectorType) {
            // Panel for message expression input
            addMessageInputPanel(p, properties);
        }
    
        return p;
    }
    
    // Method to add panel for message expression input
    private void addMessageInputPanel(JPanel parentPanel, Map<String, Object> properties) {
        JPanel horiz = new JPanel();
        horiz.add(new JLabel("message expression"));
        horiz.add(NodePropertiesDialog.createTextField(properties, MESSAGE));
        parentPanel.add(horiz);
    }
    
    // Method to remove panel for message expression input
    private void removeMessageInputPanel(JPanel parentPanel) {
        Component[] components = parentPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JLabel) {
                    JLabel label = (JLabel) panel.getComponent(0);
                    if ("message expression".equals(label.getText())) {
                        parentPanel.remove(panel);
                        parentPanel.revalidate();
                        parentPanel.repaint();
                        return;
                    }
                }
            }
        }
    }

    // Method to add panel for vector input
    private void addVectorInputPanel(JPanel parentPanel, Map<String, Object> properties) {
        JPanel vectorPanel = new JPanel();
        vectorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        vectorPanel.add(new JLabel("Vector values:"));

        // Input fields for x, y, and z values
        JTextField xField = NodePropertiesDialog.createTextField(properties, X_VAL);
        xField.setColumns(5); // Adjust the width of the input field
        vectorPanel.add(new JLabel("X:"));
        vectorPanel.add(xField);

        JTextField yField = NodePropertiesDialog.createTextField(properties, Y_VAL);
        yField.setColumns(5); // Adjust the width of the input field
        vectorPanel.add(new JLabel("Y:"));
        vectorPanel.add(yField);

        JTextField zField = NodePropertiesDialog.createTextField(properties, Z_VAL);
        zField.setColumns(5); // Adjust the width of the input field
        vectorPanel.add(new JLabel("Z:"));
        vectorPanel.add(zField);

        parentPanel.add(vectorPanel);
        parentPanel.revalidate();
        parentPanel.repaint();
    }

    // Method to remove panel for vector input
    private void removeVectorInputPanel(JPanel parentPanel) {
        Component[] components = parentPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JLabel) {
                    JLabel label = (JLabel) panel.getComponent(0);
                    if ("Vector values:".equals(label.getText())) {
                        parentPanel.remove(panel);
                        parentPanel.revalidate();
                        parentPanel.repaint();
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void writeAttributes(XMLWriter out, IdMap uid_map) {
        super.writeAttributes(out, uid_map);
        Graph.printAtt(out, TOPIC, this.getProperty(TOPIC).toString());
        Graph.printAtt(out, MESSAGE, this.getProperty(MESSAGE).toString());
        Graph.printAtt(out, X_VAL, this.getProperty(X_VAL).toString());
        Graph.printAtt(out, Y_VAL, this.getProperty(Y_VAL).toString());
        Graph.printAtt(out, Z_VAL, this.getProperty(Z_VAL).toString());
    }

    @Override
    public void readAttribute(XMLReader r, String name, String value, IdMap uid_map) throws SAXException {

        if (name.equals(TOPIC) || name.equals(MESSAGE) || name.equals(X_VAL) || name.equals(Y_VAL)
                || name.equals(Z_VAL)) {
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

}