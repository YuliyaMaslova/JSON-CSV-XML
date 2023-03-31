package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, "data.csv");

        String json = listToJson(list);
//
        writeString(json, "data.json");

        List<Employee> list1 = parseXML("data.xml");
        String json1 = listToJson(list1);
        writeString(json1, "data2.json");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String listToJson(List<Employee> employees) {
        Type type = new TypeToken<List<Employee>>() {
        }.getType();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(employees, type);

    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(path));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            List<Employee> result = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element employeeElement = (Element) node;

                    String id = employeeElement.getElementsByTagName("id").item(0).getTextContent();
                    String firstName = employeeElement.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = employeeElement.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = employeeElement.getElementsByTagName("country").item(0).getTextContent();
                    String age = employeeElement.getElementsByTagName("age").item(0).getTextContent();

                    Employee employee = new Employee(Integer.parseInt(id), firstName, lastName, country, Integer.parseInt(age));
                    result.add(employee);

                }
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }



}



