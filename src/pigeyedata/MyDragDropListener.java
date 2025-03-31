package pigeyedata;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.*;
import java.io.*;

class MyDragDropListener implements DropTargetListener {
	String FILE_PATH;
	boolean output = false;
	private String inputFilePath;
    private String outputFilePath;
    @Override
    public void drop(DropTargetDropEvent event) {

        // Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY);

        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();

        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        // Loop through the flavors
        for (DataFlavor flavor : flavors) {

            try {

                // If the drop items are files
                if (flavor.isFlavorJavaFileListType()) {

                    // Get all of the dropped files
                    List files = (List)transferable.getTransferData(flavor);

                    // Loop them through
                    for (Object file : files) {
                    	File temp = (File) file;
                        // Print out the file path
                    	FILE_PATH = temp.getAbsolutePath();
                    	if(output) {
                    		outputFilePath = FILE_PATH;
                    	}else {
                    		inputFilePath = FILE_PATH;
                    	}
                    }

                }

            } catch (Exception e) {

                // Print out the error stack
                e.printStackTrace();

            }
        }

        // Inform that the drop is complete
        event.dropComplete(true);
       
    }

    @Override
    public void dragEnter(DropTargetDragEvent event) {
    }

    @Override
    public void dragExit(DropTargetEvent event) {
    }

    @Override
    public void dragOver(DropTargetDragEvent event) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent event) {
    }
    public String getInputFilePath() {
        return inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }
}
