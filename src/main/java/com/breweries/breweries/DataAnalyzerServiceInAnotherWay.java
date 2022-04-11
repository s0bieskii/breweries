package com.breweries.breweries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class DataAnalyzerServiceInAnotherWay {

    public static final Logger LOGGER = Logger.getLogger(DataAnalyzerServiceInAnotherWay.class.getName());
    private Workbook workbook;
    private int breweriesWithWebsite = 0;
    private int numberOfBreweriesInStateOfferSpecifyFood = 0;
    private HashMap<String, Integer> numberOfBreweriesPerState = new HashMap<>();
    private HashMap<String, Integer> topCityBreweries = new HashMap<>();
    private HashMap<String, Integer> breweriesWithWine = new HashMap<>();
    private List<String> allStatesShortcuts = State.getAllStatesShortcutAsString();
    private List<String> allStatesName = State.getAllStatesNameAsString();


    public void run(String... args) {

        try {
            loadFile(Config.FILE_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                countBreweriesInStates(row.getCell(2), row.getCell(12));
                countBreweriesWithWebsite(row.getCell(2), row.getCell(14));
                findTopCityForBrewery(row.getCell(2), row.getCell(3));
                countBreweriesOfferGivenFoodInGivenState(row.getCell(2), row.getCell(12),
                        row.getCell(9), State.DE, "taco");
            }
        }
        printBreweriesPerStateFormatted();
        printTopCitiesForBreweries(10, topCityBreweries);
        System.out.println(breweriesWithWebsite + " breweries has website");
        System.out.println(numberOfBreweriesInStateOfferSpecifyFood + " breweries in " + State.DE.getFullName()
                + " state offer " + "taco");

    }

    private void countBreweriesOfferGivenFoodInGivenState(Cell categoryCell, Cell stateCell, Cell menuCell,
                                                          State stateEnum, String foodName) {
        if (isCellEmpty(categoryCell) || isCellEmpty(stateCell)) {
            return;
        }
        String category = categoryCell.getStringCellValue().toUpperCase();
        String state = stateCell.getStringCellValue().toUpperCase().trim();
        String menu;
        if (category.contains("BREW") && (stateEnum.name().equals(state) || stateEnum.getFullName().equals(state))) {
            if (!isCellEmpty(menuCell)) {
                menu = menuCell.getStringCellValue().toUpperCase().trim();
                if (menu.contains(foodName.toUpperCase())) {
                    numberOfBreweriesInStateOfferSpecifyFood++;
                    return;
                }
            }
            if (category.contains(foodName.toUpperCase())) {
                numberOfBreweriesInStateOfferSpecifyFood++;
                return;
            }
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
            numberOfBreweriesPerState.computeIfPresent(state, (key, value) -> value + 1);
            numberOfBreweriesPerState.putIfAbsent(state, 1);
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
            topCityBreweries.computeIfPresent(city, (key, value) -> value + 1);
            topCityBreweries.putIfAbsent(city, 1);
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
        System.out.println("Top " + howManyPositionPrint + " cities for breweries");
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

    private void loadFile(String path) throws FileNotFoundException {
        File file = new File(path);

        if (file == null) {
            throw new FileNotFoundException();
        }
        try {
            workbook = new XSSFWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
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
