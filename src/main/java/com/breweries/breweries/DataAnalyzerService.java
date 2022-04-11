package com.breweries.breweries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataAnalyzerService implements CommandLineRunner {

    public static final Logger LOGGER = Logger.getLogger(DataAnalyzerService.class.getName());
    private Workbook workbook;
    private List<String> allStatesShortcuts = State.getAllStatesShortcutAsString();
    private List<String> allStatesName = State.getAllStatesNameAsString();

    @Override
    public void run(String... args) {

        try {
            loadFile(Config.FILE_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        printBreweriesPerStateFormatted(countPlacesInStates("brew"));
        System.out.println("----------------------------------------------");
        printTopCitiesForBreweries(10, findTopCityForBrewery());
        System.out.println("----------------------------------------------");
        System.out.println(countGivenPlacesWithWebsite("brew") + " breweries has website");
        System.out.println("----------------------------------------------");
        System.out.println(countBreweriesOfferGivenFoodInGivenState(State.DE, "taco") + " breweries in " +
                State.DE.getFullName() + " state offer taco");
        System.out.println("----------------------------------------------");
        printBreweriesWithWinePercentage(countBreweriesOfferWine());

        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Integer> countPlacesInStates(String countingCategory) {
        HashMap<String, Integer> data = new HashMap<>();
        countingCategory = countingCategory.toUpperCase();
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                Cell categoryCell = row.getCell(Config.CATEGORY_CELL_INDEX);
                Cell stateCell = row.getCell(Config.STATE_CELL_INDEX);
                if (row.getRowNum() == 0 || isCellEmpty(categoryCell) || isCellEmpty(stateCell)) {
                    continue;
                }
                String category = categoryCell.getStringCellValue().toUpperCase();
                String state = stateCell.getStringCellValue().toUpperCase().trim();
                if (category.contains(countingCategory) && allStatesShortcuts.contains(state)
                        || allStatesName.contains(state)) {
                    if (!allStatesShortcuts.contains(state)) {
                        state = State.findStateEnumByName(state).name();
                    }
                    data.computeIfPresent(state, (key, value) -> value + 1);
                    data.putIfAbsent(state, 1);
                }
            }
        }
        return data;
    }

    public HashMap<String, Integer> findTopCityForBrewery() {
        HashMap<String, Integer> topCityBreweries = new HashMap<>();
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                Cell categoryCell = row.getCell(Config.CATEGORY_CELL_INDEX);
                Cell cityCell = row.getCell(Config.CITY_CELL_INDEX);
                if (row.getRowNum() == 0 || isCellEmpty(categoryCell) || isCellEmpty(cityCell)) {
                    continue;
                }
                String category = categoryCell.getStringCellValue().toUpperCase();
                String city = cityCell.getStringCellValue().toUpperCase().trim();
                if (category.contains("BREW")) {
                    if (!topCityBreweries.keySet().contains(city)) {
                        topCityBreweries.put(city, 1);
                        continue;
                    }
                    topCityBreweries.computeIfPresent(city, (key, value) -> value + 1);
                    topCityBreweries.putIfAbsent(city, 1);
                }
            }
        }
        return topCityBreweries;
    }

    private HashMap<String, int[]> countBreweriesOfferWine() {
        HashMap<String, Integer> breweriesInStates = countPlacesInStates("brew");
        HashMap<String, int[]> breweriesOfferWine = new HashMap<>();

        for (Map.Entry<String, Integer> set : breweriesInStates.entrySet()) {
            int[] breweryWine = new int[2];
            breweryWine[0] = breweriesInStates.get(set.getKey());
            breweryWine[1] =
                    countBreweriesOfferGivenFoodInGivenState(State.findStateEnumByShortcut(set.getKey()), "wine");
            breweriesOfferWine.putIfAbsent(set.getKey(), breweryWine);
        }
        return breweriesOfferWine;
    }

    private int countBreweriesOfferGivenFoodInGivenState(State stateEnum, String foodName) {
        int numberOfBreweriesInStateOfferGivenFood = 0;
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                Cell categoryCell = row.getCell(Config.CATEGORY_CELL_INDEX);
                Cell stateCell = row.getCell(Config.STATE_CELL_INDEX);
                Cell menuCell = row.getCell(Config.MENU_CELL_INDEX);
                if (row.getRowNum() == 0 || isCellEmpty(categoryCell) || isCellEmpty(stateCell)) {
                    continue;
                }
                String category = categoryCell.getStringCellValue().toUpperCase();
                String state = stateCell.getStringCellValue().toUpperCase().trim();
                String menu;
                if (category.contains("BREW") &&
                        (stateEnum.name().equals(state) || stateEnum.getFullName().equals(state))) {
                    if (!isCellEmpty(menuCell)) {
                        menu = menuCell.getStringCellValue().toUpperCase().trim();
                        if (menu.contains(foodName.toUpperCase())) {
                            numberOfBreweriesInStateOfferGivenFood++;
                            continue;
                        }
                    }
                    if (category.contains(foodName.toUpperCase())) {
                        numberOfBreweriesInStateOfferGivenFood++;
                        continue;
                    }
                }
            }
        }
        return numberOfBreweriesInStateOfferGivenFood;
    }

    private int countGivenPlacesWithWebsite(String placeToCount) {
        int counter = 0;
        placeToCount = placeToCount.toUpperCase();
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                Cell categoryCell = row.getCell(Config.CATEGORY_CELL_INDEX);
                Cell websiteCell = row.getCell(Config.WEBSITE_CELL_INDEX);
                if (row.getRowNum() == 0 || isCellEmpty(categoryCell) || isCellEmpty(websiteCell)) {
                    continue;
                }
                String category = categoryCell.getStringCellValue().toUpperCase();
                String website = websiteCell.getStringCellValue().toUpperCase().trim();
                if (category.contains(placeToCount) && !website.isBlank()) {
                    counter++;
                }
            }
        }
        return counter;
    }

    private void printBreweriesWithWinePercentage(HashMap<String, int[]> toPrint) {
        DecimalFormat df = new DecimalFormat("#.00");
        for (Map.Entry<String, int[]> set : toPrint.entrySet()) {
            double percentage = calculatePercentage(set.getValue()[1], set.getValue()[0]);
            System.out.println("In " + State.findStateEnumByShortcut(set.getKey()) + " " + df.format(percentage) +
                    "% breweries offer wine");
        }
    }

    private void printBreweriesPerStateFormatted(HashMap<String, Integer> toPrint) {

        for (String state : toPrint.keySet()) {
            String stateName = State.findStateEnumByShortcut(state).getFullName();
            String message = stateName + " state has " + toPrint.get(state) + " given places";
            System.out.println(message);
        }
    }

    private void printTopCitiesForBreweries(int howManyPositionPrint, HashMap<String, Integer> hashMap) {
        Object[] a = hashMap.entrySet().toArray();
        Arrays.sort(a, (Comparator) (o1, o2) -> ((Map.Entry<String, Integer>) o2).getValue()
                .compareTo(((Map.Entry<String, Integer>) o1).getValue()));
        int counter = 0;
        System.out.println("Top " + howManyPositionPrint + " cities for breweries");
        for (Object e : a) {
            if (counter == howManyPositionPrint) {
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

    private boolean isCellEmpty(final Cell cell) {
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

    private double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }

}
