package pigeyedata;

import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class DragDropWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    public MyDragDropListener myDragDropListener;

    public DragDropWindow(String title, String label) {
    	
        // Set the frame title
        super(title);
        
        // Set the size
        this.setSize(425, 250);

        // Create the label
        JLabel myLabel = new JLabel(label, SwingConstants.CENTER);

        // Create the drag and drop listener
        myDragDropListener = new MyDragDropListener();

        // Connect the label with a drag and drop listener
        new DropTarget(myLabel, myDragDropListener);

        // Add the label to the content
        this.getContentPane().add(BorderLayout.CENTER, myLabel);

        // Show the frame
        this.setVisible(true);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
    }
    public void setWindowLabel(String label) {
    	Component[] components = this.getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                this.getContentPane().remove(component);
            }
        }

        // Create and add the new label with the updated text
        JLabel myLabel = new JLabel(label, SwingConstants.CENTER);
        new DropTarget(myLabel, myDragDropListener);
        this.getContentPane().add(BorderLayout.CENTER, myLabel);
        this.revalidate(); // Refresh the window to reflect the label change
    }

    public String getInputFilePath() {
        return myDragDropListener.getInputFilePath();
    }

    public String getOutputFilePath() {
        return myDragDropListener.getOutputFilePath();
    }

}

