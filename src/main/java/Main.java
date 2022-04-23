import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
        String fileCSV = "data.csv";
        String csvToJson = "data.json";
        String fileXml = "data.xml";
        String xmlToJson = "data2.json";

        List<Employee> listCsv = parseCSV(columnMapping, fileCSV);
        String json = listToJson(listCsv);
        writeToJsonFile(json, csvToJson);

        List<Employee> listXml = parseXML(fileXml);
        json = listToJson(listXml);
        writeToJsonFile(json, xmlToJson);
    }

    private static List<Employee> parseXML(String s) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(s));

            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element employee = (Element) node;
                    long id = Long.parseLong(employee.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = employee.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = employee.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = employee.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent());
                    employees.add(new Employee(id, firstName, lastName, country, age));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return employees;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvBuilder = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csvBuilder.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static void writeToJsonFile(String json, String file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}