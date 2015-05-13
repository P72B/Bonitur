package de.p72b.bonitur;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOM {
    private Document document;
    private DocumentBuilder db = null;
    private DocumentBuilderFactory dbf;
    private Element root;
    private String tstamp,
    fileName,
    rootName = "table",
    date = "date",
    interval = "interval",
    direction = "direction",
    positionX = "positionX",
    positionY = "positionY",
    isSelected = "isSelected";

    /**
     * Creates a new DOM to hold bonitur data.
     */
    public DOM() {

    };

    
    /**
     * Creates a complete new DOM. It always starts with an empty column.
     * @param name The name of the file
     */
    public void newDOM(String name) {
        this.fileName = name;
        try {
            this.dbf = DocumentBuilderFactory.newInstance();
            this.db = this.dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        this.document = db.newDocument();
        this.root = document.createElement(rootName);
        this.tstamp = (new Date()).toString();
        this.root.setAttribute(date, tstamp);
        this.root.setAttribute(interval, "1");
        this.root.setAttribute(direction, "0");
        this.root.setAttribute(positionX, "0");
        this.root.setAttribute(positionY, "0");
        this.root.setAttribute(isSelected, "0");
        this.document.appendChild(root);
        Node firstRow = newRow(0);
        newCol(firstRow, 0, "");
        newCol(firstRow, 1, "1");
        Node secoundRow = newRow(1);
        newCol(secoundRow, 0, "1");
        newCol(secoundRow, 1, "");
    };


    /**
     * Reads a given xml file into a DOM
     * @param file to parse
     */
    public void readFile(File file) {
        this.fileName = file.getName();
        try {
            this.dbf = DocumentBuilderFactory.newInstance();
            this.db = this.dbf.newDocumentBuilder();
            this.document = this.db.parse(file);

            // optional
            this.document.getDocumentElement().normalize();
            this.root = this.document.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };


    /**
     * Gives the file name.
     * @return
     */
    public String getFileName() {
        return this.fileName;
    }


    /**
     * Gives the amount of rows back.
     * @return
     */
    public int getRowsLength() {
        return this.root.getChildNodes().getLength();
    };


    /**
     * Gives the amount of columns back.
     * @return
     */
    public int getColsLength() {
        return this.root.getLastChild().getChildNodes().getLength();
    }


    /**
     * Delivers the text content of a node at a specific position.
     * @return
     */
    public String getTextContent(int row, int col) {
        return this.root.getChildNodes().item(row).getChildNodes().item(col).getTextContent();
    }


    /**
     * Gives the date of DOM creation time.
     * @return
     */
    public String getTstamp() {
        return this.tstamp;
    }


    /**
     * Gives the file name.
     * @return
     */
    public void setFileName(String name) {
        this.fileName = name;
    }


    /**
     * Sets the content text of a column.
     * @param pos Contains the row and column
     * @param txt Text to put inside the column
     */
    public void setNodeText(int[] pos, String txt) {
        Node row = this.root.getChildNodes().item(pos[0]);
        row.getChildNodes().item(pos[1]).setTextContent(txt);
    }


    /**
     * Creates a new column in DOM.
     * @param node Contains the row where to place the new column
     * @param id ID of column
     * @param value Content text
     */
    public void newCol(Node node, int id, String value) {
        Element el = document.createElement("col");
        el.setTextContent(value);
        el.setAttribute("id", Integer.toString(id));
        node.appendChild(el);
    };


    /**
     * Create a new row in DOM an returns it element.
     * @param id ID of row
     * @return
     */
    public Node newRow(int id) {
        Element el = document.createElement("row");
        el.setAttribute("id", Integer.toString(id));
        this.root.appendChild(el);
        return el;
    };


    /**
     * Removes all child elements of DOM root element. This is needed when the table was updated.
     */
    public void clearRoot() {
        NodeList rows = this.root.getChildNodes();
        int len = rows.getLength();
        for (int i = 0; i < len; i++) {
            this.root.removeChild(rows.item(i));
        }
    };


    /**
     * Log the DOM
     */
    public String toString() {
        String domStr = "";
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(
                    output));
            domStr = output.toString("UTF-8");
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return domStr;
    }


    public int getDirection() {
        return Integer.parseInt(this.root.getAttribute(direction));
    };


    public void setDirection(int val) {
        this.root.setAttribute(direction, String.valueOf(val));
    };


    public double getInterval() {
        return Double.parseDouble(this.root.getAttribute(interval));
    };


    public void setInterval(double d) {
        this.root.setAttribute(interval, String.valueOf(d));
    };


    public boolean isSelected() {
        return Integer.parseInt(this.root.getAttribute(isSelected)) == 0? false : true;
    };


    public void setSelected(boolean val) {
        this.root.setAttribute(isSelected, val ? "1" : "0");
    };


    public void setPositionX(int x) {
        this.root.setAttribute(positionX, String.valueOf(x));
    };


    public int getPositionX() {
        return Integer.parseInt(this.root.getAttribute(positionX));
    };


    public void setPositionY(int y) {
        this.root.setAttribute(positionY, String.valueOf(y));
    };


    public int getPositionY() {
        return Integer.parseInt(this.root.getAttribute(positionY));
    };
};
