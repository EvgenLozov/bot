package ru.savkin;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataReader {


    public final File file = new File(ClassLoader.getSystemResource("text.xml").getFile());


    public final Document getContent() throws SAXException,
            ParserConfigurationException,
            IOException, BadLocationException,
            SAXException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(this.getClass().getResourceAsStream("/text.xml"));

        return document;
    }


    public Node getRootDocument() {
        NodeList list;
        return null;
    }

    public List<String> getTariffis() {
        List<String>  samokats = new ArrayList();
        try {

            NodeList l = getContent().getElementsByTagName("data");
            // System.out.println(l.getLength());

            for (int i = 0; i < l.getLength(); i++) {
                NodeList tInfo = l.item(i).getChildNodes();

                //     System.out.println("j " + j + " " + tInfo.item(j).getTextContent() );
                System.out.println(l.item(i).getTextContent());
                samokats.add(l.item(i).getTextContent());
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return samokats;
    }


    public void addData(String data) {

        try {
            Document doc = getContent();
            Element root = doc.getDocumentElement();
            Element samokatElement = doc.createElement("samokat");
            Element dataElement = doc.createElement("data");
            dataElement.appendChild(doc.createTextNode(data));
            samokatElement.appendChild(dataElement);
            root.appendChild(samokatElement);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(this.file);
            transformer.transform(domSource, streamResult);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    /*
    public static void main(String[] args) {
        DataReader dr =  new DataReader();
        dr.addData("31.09.18");
        dr.getTariffis();
    }
    */
}
