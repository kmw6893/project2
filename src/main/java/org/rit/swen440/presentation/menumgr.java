package org.rit.swen440.presentation;

import org.rit.swen440.control.Controller;
import org.rit.swen440.dataLayer.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class menumgr
{
    int currentLevel = 0;
    String currentCategoryName;
    String currentItemName;
    category currentCategory;
    item currentItem;
    String currentSearch;
    List<String> currentResults;

    private Controller controller;

    public menumgr()
    {
        controller = new Controller( "catalog",  "localhost",  "3306",  "cust",  "1234");

    }

    public boolean loadLevel(int level)
    {
//        System.out.println("Loading level:" + currentLevel);
        switch (currentLevel)
        {
            case -1:
                return true;
            case 0:
                Level0();
                break;
            case 1:
                Level1();
                break;
            case 2:
                Level2();
                break;
            case 3:
                Level3();
                break;
            default:
                System.out.println("Returning to main org.rit.swen440.presentation.menu");
                currentLevel = 0;
                Level0();
                break;
        }

        return false;
    }

    public void Level0()
    {
        menu m = new menu();
        List<String> categories = controller.getCategories();
        m.loadMenu(categories, true);
        m.addMenuItem("'q' to Quit");
        System.out.println("\nThe following org.rit.swen440.presentation.categories are available");
        m.printMenu();
        String result = "0";
        try
        {
            result = m.getSelection();
        }
        catch (Exception e)
        {
            result = "q";
        }
        if (Objects.equals(result,"q"))
        {
            currentLevel--;
        }
        else
        {
            int iSel = Integer.parseInt(result);
            if (iSel == 0) currentLevel = 2;
            else {
                currentLevel++;
                currentCategoryName = categories.get(iSel);
                System.out.println("\nYour Selection was:" + currentCategoryName);
            }
        }
    }

    public void Level1()
    {
        menu m = new menu();

        //items it = new items("orderSys/" + currentCategory.getName());

        // List<item> itemList = controller.getProducts(currentCategoryName);
        List<String> itemList = controller.getProducts(currentCategoryName);
        List<String> l = new ArrayList<>();
        System.out.println("");
        for (String itm: itemList)
            l.add(controller.getProductInformation(currentCategoryName, itm, Controller.PRODUCT_FIELD.NAME)
                    + "($" + controller.getProductInformation(currentCategoryName, itm, Controller.PRODUCT_FIELD.COST) + ")");

        m.loadMenu(l, true);
        m.addMenuItem("'q' to quit");
        System.out.println("The following items are available");
        m.printMenu();
        String result = m.getSelection();
        try
        {
            int iSel = Integer.parseInt(result);//Item  selected
            if (iSel == 0) currentLevel = 2;
            else {
                currentItemName = itemList.get(iSel - 1);
                //currentItem = itemList.get(iSel);
                //Now read the file and print the org.rit.swen440.presentation.items in the catalog
                OrderQty(currentCategoryName, currentItemName);
            }
        }
        catch (Exception e)
        {
            result = "q";
        }
        if (Objects.equals(result,"q")) currentLevel--;
    }


    public void Level2()
    {
        menu m = new menu();
        List<String> l = new ArrayList<>();
        m.loadMenu(l, false);
        m.addMenuItem("'q' to Quit");
        System.out.println("\nEnter a search term.");
        m.printMenu();
        String result = "0";
        Boolean found = false;
        try
        {
            result = m.getSelection();
            if (Objects.equals(result,"q"));
            else if (Objects.equals(result,"0")) result = "q";
            else {
                currentSearch = result;
                currentResults = controller.findItem(currentSearch);
                if (currentResults.size() > 0) currentLevel = 3;
                else System.out.println("\nNo matching products found for '" + currentSearch + "'.");
            }
        }
        catch (Exception e)
        {
            result = "q";
        }
        if (Objects.equals(result,"q")) currentLevel = 0;
    }

    public void Level3() {
        menu m = new menu();
        List<String> l = new ArrayList<>();
        System.out.println("");
        for (String itm: currentResults) {
            currentCategoryName = controller.getCategory(itm);
            l.add(controller.getProductInformation(currentCategoryName, itm, Controller.PRODUCT_FIELD.NAME)
                    + "($" + controller.getProductInformation(currentCategoryName, itm, Controller.PRODUCT_FIELD.COST) + ")");
        }
        m.loadMenu(l, true);
        m.addMenuItem("'q' to quit");
        System.out.println("The following items are available");
        m.printMenu();
        String result = m.getSelection();
        try
        {
            int iSel = Integer.parseInt(result);//Item  selected
            if (iSel == 0) currentLevel = 2;
            else {
                currentItemName = currentResults.get(iSel - 1);
                currentCategoryName = controller.getCategory(currentItemName);
                OrderQty(currentCategoryName, currentItemName);
            }
        }
        catch (Exception e)
        {
            result = "q";
        }
        if (Objects.equals(result,"q")) currentLevel = 0;
    }

    public void OrderQty(String category, String item)
    {
        if (category == null) return;
        System.out.println("\nYou want item from the catalog: " + item);
        System.out.println("Please select a quantity");
        System.out.println(controller.getProductInformation(category, item, Controller.PRODUCT_FIELD.NAME) +
                " availability:" + controller.getProductInformation(category, item, Controller.PRODUCT_FIELD.INVENTORY));
        System.out.print(":");
        menu m = new menu();
        String result = m.getSelection();
        System.out.println("You ordered:" + result);
    }
}