package com.breweries.breweries;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataAnalyzerService implements CommandLineRunner {

    public static final Logger LOGGER = Logger.getLogger(DataAnalyzerService.class.getName());
    private Workbook workbook;
    private int breweriesWithWebsite = 0;
    private HashMap<String, Integer> numberOfBreweriesPerState = new HashMap<>();
    private HashMap<String, Integer> topCityBreweries = new HashMap<>();
    private List<String> allStatesShortcuts = State.getAllStatesShortcutAsString();
    private List<String> allStatesName = State.getAllStatesNameAsString();

    @Override
    public void run(String... args) {

        loadFile(Config.FILE_PATH);

        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                countBreweriesInStates(row.getCell(2), row.getCell(12));
                countBreweriesWithWebsite(row.getCell(2), row.getCell(14));
                findTopCityForBrewery(row.getCell(2), row.getCell(3));
            }
        }
        printBreweriesPerStateFormatted();
        printTopCitiesForBreweries(10,topCityBreweries);
        System.out.println(breweriesWithWebsite + " breweries has website");

    }

    private void loadFile(String path) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            LOGGER.warning("File not found in this path");
            e.printStackTrace();
        }

        try {
            workbook = new XSSFWorkbook(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            LOGGER.warning("File extension exception");
            e.printStackTrace();
        }
    }

    private void countBreweriesInStates(Cell categoryCell, Cell stateCell) {

        if (isCellEmpty(categoryCell) || isCellEmpty(stateCell)) {
            return;
        }
        String category = categoryCell.getStringCellValue().toUpperCase();
        String state = stateCell.getStringCellValue().toUpperCase().trim();
        if (category.contains("BREW") && allStatesShortcuts.contains(state)
                || allStatesName.contains(state)) {
            if (!allStatesShortcuts.contains(state)) {
                state = State.findStateEnumByName(state).name();
            }
            if (!numberOfBreweriesPerState.keySet().contains(state)) {
                numberOfBreweriesPerState.put(state, 1);
            } else {
                numberOfBreweriesPerState.put(state, numberOfBreweriesPerState.get(state) + 1);
            }
        }
    }

    public void countBreweriesWithWebsite(Cell categoryCell, Cell websiteCell) {

        if (isCellEmpty(categoryCell) || isCellEmpty(websiteCell)) {
            return;
        }
        if (categoryCell.getStringCellValue().toUpperCase().contains("BREW")) {
            breweriesWithWebsite++;
        }
    }

    public void findTopCityForBrewery(Cell categoryCell, Cell cityCell) {
        if (isCellEmpty(categoryCell) || isCellEmpty(cityCell)) {
            return;
        }
        String category = categoryCell.getStringCellValue().toUpperCase();
        String city = cityCell.getStringCellValue().toUpperCase().trim();
        if (category.contains("BREW")) {
            if (!topCityBreweries.keySet().contains(city)) {
                topCityBreweries.put(city, 1);
                return;
            }
            topCityBreweries.put(city, topCityBreweries.get(city) + 1);
        }
    }

    private void printBreweriesPerStateFormatted() {

        for (String state : numberOfBreweriesPerState.keySet()) {
            String stateName = State.findStateEnumByShortcut(state).getFullName();
            String message = stateName + " state has " + numberOfBreweriesPerState.get(state) + " breweries";
            System.out.println(message);
        }
        System.out.println("----------------------------------------------");
    }

    private void printTopCitiesForBreweries(int howManyPositionPrint, HashMap<String, Integer> hashMap) {
        Object[] a = hashMap.entrySet().toArray();
        Arrays.sort(a, (Comparator) (o1, o2) -> ((Map.Entry<String, Integer>) o2).getValue()
                .compareTo(((Map.Entry<String, Integer>) o1).getValue()));
        int counter = 0;
        System.out.println("Top "+howManyPositionPrint+" cities for breweries");
        for (Object e : a) {
            if (counter == howManyPositionPrint) {
                System.out.println("----------------------------------------------");
                return;
            }
            System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
                    + ((Map.Entry<String, Integer>) e).getValue());
            counter++;
        }
    }

    private static boolean isCellEmpty(final Cell cell) {
        if (cell == null) { // use row.getCell(x, Row.CREATE_NULL_AS_BLANK) to avoid null cells
            return true;
        }

        if (cell.getCellType() == CellType.BLANK) {
            return true;
        }

        if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) {
            return true;
        }

        return false;
    }

}
