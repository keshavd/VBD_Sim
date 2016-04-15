import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JFileChooser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/*
 *	This file is part of DiseaseSim version 0.3 -  an agent based modeling research tool	*
 *	Copyright (C) 2012 Marek Laskowski				*
 *											*
 *	This program is free software: you can redistribute it and/or modify		*
 *	it under the terms of the GNU General Public License as published by		*
 *	the Free Software Foundation, either version 3 of the License, or		*
 *	(at your option) any later version.						*
 *											*
 *	This program is distributed in the hope that it will be useful,			*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of			*
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			*
 *	GNU General Public License for more details.					*
 *											*
 *	You should have received a copy of the GNU General Public License		*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.		*
 *											*
 *	email: mareklaskowski@gmail.com							*
 ****************************************************************************************/
/**
 * 
 * a class that implements a GUI for simulating vector borne disease spread
 */

public class DiseaseGUI extends JPanel implements ActionListener{
	private static final long serialVersionUID = 8832885560545657000L;
	
	private static Canvas canvas;
	
	private static ActionListener listener = null;
	private static Timer displayTimer = null;
	
	public static World theWorld;
	/**
	 * default constructor
	 */
	public DiseaseGUI(){
		
		canvas = new Canvas(){
			//private Rectangle test = new Rectangle(50,50);
			public void paint (Graphics g)
			{
			    //setBounds(20, 40, 300, 300);
			    //Rectangle r = new Rectangle(getPreferredSize());
				Rectangle r = new Rectangle(0,0,800,800);
			    setBounds(r);
				//setPreferredSize(getMaximumSize());
				setBackground(Color.white);
				theWorld.render(g, r);
				
			}
		};
		canvas.setPreferredSize(getMaximumSize());
		add(canvas, BorderLayout.SOUTH);
		
		

	}

	@Override
	/**
	 * a handler method for any gui events.. not currently used
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "any")
		{
			System.out.println("something happened!");
						
		}
		
	}
	
	/**
	 * your typical main function
	 * @param args currently ignored
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {
		
		// Create Factory, DocumentBuilder and JFileChooser
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
		final JFileChooser fc = new JFileChooser();
		
		//Opens the File Selection Dialog Box
		int returnVal = fc.showOpenDialog(null);
		
		//Retrieves the XML's location
		String filename = fc.getSelectedFile().getAbsolutePath();
		
		//Parses the XML Document
		Document document = docBuilder.parse(filename);
		
		/*
		 * PARSE XML CONFIG FILE INTO CONSTRUCTOR VARIABLES via XPath
		 */
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		//ROWS
		String expression = "/config/World/input/rows";
		Node widgetNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
		int rows = Integer.parseInt(widgetNode.getTextContent());
		//COLS
		expression = "/config/World/input/cols";
		widgetNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
		int cols = Integer.parseInt(widgetNode.getTextContent());
		//timeStepSeconds
		expression = "/config/World/input/timeStepSeconds";
		widgetNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
		double timeStepSeconds = Double.parseDouble(widgetNode.getTextContent());
		//averageMosquitoDensity
		expression = "/config/World/input/averageMosquitoDensity";
		widgetNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
		double averageMosquitoDensity = Double.parseDouble(widgetNode.getTextContent());		
		//averageHumanDensity
		expression = "/config/World/input/averageHumanDensity";
		widgetNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
		double averageHumanDensity = Double.parseDouble(widgetNode.getTextContent());
		//gender
		expression = "/config/World/input/genderRatio";
		widgetNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
		double genderRatio = Double.parseDouble(widgetNode.getTextContent());
	
		
		/* Use an appropriate Look and Feel */
        try {
           
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        
        theWorld = new World(rows, cols, timeStepSeconds, averageMosquitoDensity, averageHumanDensity, genderRatio);
		//add one or more infected agents
		Environment groundZero = World.getRandomLocation();
		Disease newInfection = new Disease("ACGT");
		Human patientZero = new Human(groundZero.getRow(), groundZero.getColumn());
		patientZero.recieveDisease(newInfection.getStrain());
		groundZero.enter(patientZero);
		
        listener = new ActionListener(){
        	  public void actionPerformed(ActionEvent event){
        		displayTimer.stop();
        		//advance the time'
	      		theWorld.tick();
        		int infectionCount = theWorld.countInfections();
    			if(infectionCount <= 0)
    			{
    				displayTimer.stop();
    				System.out.println("Simulation finished");
    			}
    			else
    			{
    				canvas.repaint();
    				System.out.println("time: " + World.getTime().getTime() + "number of infections: " + infectionCount);
    				displayTimer.restart();
	      			
    			}
        	  }
        };
        displayTimer = new Timer(1000, listener);
    	displayTimer.start();

        
	}
	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Vector Disease Model");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        //Create and set up the content pane.
        JComponent newContentPane = new DiseaseGUI();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
         
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }


}
